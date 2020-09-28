/*
 * Copyright © 2020 Mr.Po (ldd_live@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pomo.toasterfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.model.Toast;

import java.util.*;
import java.util.concurrent.*;

/**
 * <h2>消息体处理器</h2>
 *
 * <p>通过守护线程，从队列中取出消息体，并装入候选消息队列中</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 16:38:01</p>
 * <p>更新时间：2020-09-27 16:38:01</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class ToastHandler {

    // region {成员组件}
    /**
     * 消息者工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterFactory toasterFactory;

    /**
     * 多消息工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private MultiToastFactory multiToastFactory;

    /**
     * 消息体 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToastHelper toastHelper;
    // endregion

    // region {成员属性}
    /**
     * 消息体 队列<br/>
     * push后会进入此队列<br/>
     * 会在多个线程中被使用，应该使用线程安全的实例
     */
    @Setter
    @NonNull
    private BlockingQueue<Toast> toasts;

    /**
     * 候选体消息 队列<br/>
     * 这其中的toast，会逐步被展示<br/>
     * 会在多个线程中被使用，应该使用线程安全的实例
     */
    @Setter
    @NonNull
    private Queue<Toast> candidateToasts;

    /**
     * 最大弹窗数量限制<br/>
     * 可以等于
     */
    @Setter
    private int limit = 7;

    /**
     * 消息体队列 处理器<br/>
     * 间隔 @toastsHandleInterval，执行一次
     */
    private Thread toastsHandler;

    /**
     * 候选消息队列 处理器<br/>
     * 间隔 @candidateToastsHandleInterval，执行一次
     */
    private Timeline candidateToastsHandler;

    /**
     * 候选消息体队列 处理间隔（毫秒）<br/>
     * 候选消息体不宜处理太快，因为当队列中数据太多时，实时弹出太多会导致UI卡顿。
     */
    @Setter
    private long candidateToastsHandleInterval = 200;

    /**
     * 候选消息体队列 栅栏
     */
    private final CyclicBarrier candidateToastsBarrier = new CyclicBarrier(2);

    /**
     * FX线程异常
     */
    private Throwable FXThreadThrowable;

    /**
     * 是否为首次play
     */
    private boolean firstPlay;
    // endregion

    /**
     * <h2>初始化</h2>
     */
    void initialize() {

        Objects.requireNonNull(this.multiToastFactory, "multiToastFactory must non-null but is null.");
        Objects.requireNonNull(this.toasterFactory, "toasterFactory must non-null but is null.");
        Objects.requireNonNull(this.toastHelper, "toastHelper must non-null but is null.");

        if (this.toasts == null) this.toasts = new LinkedBlockingQueue<>();
        if (this.candidateToasts == null) this.candidateToasts = new ConcurrentLinkedQueue<>();

        // region {候选消息体队列处理器}
        this.candidateToastsHandler = new Timeline(
                new KeyFrame(Duration.ZERO, it -> {

                    // 第一条消息，会立即展示
                    if (this.firstPlay) {
                        this.firstPlay = false;
                        this.show(it);
                    }
                }),
                new KeyFrame(Duration.millis(this.candidateToastsHandleInterval), this::show)
        );
        this.candidateToastsHandler.setCycleCount(Timeline.INDEFINITE);
        // endregion

        // region {消息体队列处理器}
        String threadName = "ToastHandler-Thread-" + this.hashCode();
        this.toastsHandler = new Thread(() -> {

            // 用于接收卸载下来的消息体集合
            List<Toast> list = new ArrayList<>();

            // 待归档消息集合
            List<Toast> archiveToasts = new ArrayList<>();

            while (true) {

                try {

                    Toast toast;

                    // 消息体队列 与 候选消息体队列 均为空时 (若只判断后者，则可能会stop后，又立即play)
                    if (this.isEmpty()) {

                        this.candidateToastsHandler.stop();

                        log.trace("toast queue and toast of candidate queue, start sleep...");

                        // 从toasts中的取出，没有时，阻塞
                        toast = this.toasts.take();

                        log.trace("toast queue start work...");

                    } else {

                        // 从toasts中的取出，没有时，返回null
                        toast = this.toasts.poll();

                        // 消息体队列没有新数据，但 候选消息体队列，尚有未处理完的数据
                        if (toast == null) {

                            log.trace("toast queue start rest...");

                            // 等待 候选消息体队列中的数据被处理
                            candidateToastsBarrier.await();

                            log.trace("toast queue is notified...");

                            continue;
                        }
                    }

                    // 候选消息体队列数量<限制时，立即加入候选
                    if (this.candidateToasts.size() < this.limit) {

                        this.candidateToasts.add(toast);

                        if (this.candidateToastsHandler.getStatus() == Animation.Status.STOPPED) {

                            log.trace("toast queue start work...");
                            this.firstPlay = true;
                            this.candidateToastsHandler.play();
                        }

                        continue;
                    }

                    // --- ↓ 候选消息体队列已有不菲的数据量 ↓ ---

                    list.add(toast);

                    // 卸载队列[toasts]至此集合[list]
                    this.toasts.drainTo(list);

                    // 新的候选消息队列
                    List<Toast> newCandidateToasts;

                    // 此间隔产生的消息过多
                    if (list.size() > this.limit) {

                        int index = list.size() - this.limit;

                        // 截取尾部[新的]，需要展示的(包含index)
                        newCandidateToasts = list.subList(index, list.size());

                        // 添加至待归档列表(不包含index)
                        archiveToasts.addAll(list.subList(0, index));

                    } else {

                        newCandidateToasts = list;
                    }

                    // 期待的候选消息size
                    int targetCandidateToastsSize = this.limit - newCandidateToasts.size();

                    // 旧候选展示消息队列数量过多
                    while (this.candidateToasts.size() > targetCandidateToastsSize) {

                        // 取出一部分，并添加至待归档列表
                        archiveToasts.add(this.candidateToasts.poll());
                    }

                    // 当归档列表不为空时，进行归档操作
                    if (!archiveToasts.isEmpty()) {

                        // 会被阻塞
                        this.multiToastFactory.archive(archiveToasts);
                    }

                    // 添加至候选消息中
                    this.candidateToasts.addAll(newCandidateToasts);

                } catch (InterruptedException e) {// 操作被打断

                    log.trace("{} is interrupted.", threadName);

                    break;

                } catch (BrokenBarrierException e) {// 栅栏被打断，UI线程执行出错

                    log.error("unexpected error occurred while processing toast.", this.FXThreadThrowable);
                    this.FXThreadThrowable = null;

                } catch (Throwable e) {

                    // 被销毁了
                    if (this.toasts == null) break;

                    log.error("unexpected error occurred while processing toast.", e);

                } finally {

                    list.clear();
                    archiveToasts.clear();
                }
            }
        });
        this.toastsHandler.setName(threadName);
        this.toastsHandler.setDaemon(true);
        this.toastsHandler.start();
        log.debug("{} start...", threadName);
        // endregion
    }

    /**
     * <h2>推入</h2>
     * <p>将 消息 放入 消息队列 中</p>
     * <p>当短时间内，push量很大时，此消息可能不会弹出，而是放入 消息列表 中</p>
     * <p>此方法应由ToastHelper调用</p>
     *
     * @param toast 消息
     * @return 是否添加成功（立即）
     */
    boolean push(@NonNull Toast toast) {
        return this.toasts.offer(toast);
    }

    /**
     * <h2>推入</h2>
     * <p>将 消息 放入 消息队列 中</p>
     * <p>此方法应由ToastHelper调用</p>
     *
     * @param toasts 消息集
     * @return 是否添加成功（立即）
     */
    boolean push(@NonNull Collection<Toast> toasts) {
        return this.toasts.addAll(toasts);
    }

    /**
     * <h2>是否为空</h2>
     * <p>消息列表 与 待显示消息列表 是否为空</p>
     *
     * @return 是/否
     */
    boolean isEmpty() {
        return this.toasts.isEmpty() && this.candidateToasts.isEmpty();
    }

    /**
     * <h2>展示</h2>
     */
    private void show(ActionEvent event) {

        try {

            // 取出，非阻塞
            Toast toast = this.candidateToasts.poll();

            // 当消息体不为null时，进行展示
            if (toast != null)
                this.toasterFactory.show(toast);

            // 当 候选消息体队列栅栏 ，存在等待时，将其唤醒
            if (candidateToastsBarrier.getNumberWaiting() > 0)
                candidateToastsBarrier.await();

        } catch (Throwable e) {

            this.FXThreadThrowable = e;

            log.error("unexpected error occurred while processing show toast.", e);

            this.candidateToastsBarrier.reset();
        }
    }

    /**
     * <h2>销毁</h2>
     */
    void destroy() {

        this.toastsHandler.interrupt();
        this.toastsHandler = null;

        this.candidateToastsHandler.stop();
        this.candidateToastsHandler.getKeyFrames().clear();
        this.candidateToastsHandler = null;

        BlockingQueue<Toast> toasts = this.toasts;
        this.toasts = null;// 确保接下来的遍历不会出错

        Queue<Toast> candidateToasts = this.candidateToasts;
        this.candidateToasts = null;// 确保接下来的遍历不会出错

        int toastsSize = toasts.size();
        int candidateToastsSize = candidateToasts.size();
        if (toastsSize + candidateToastsSize > 0)
            log.warn("{} toast queue left, {} toast of candidate queue left.", toastsSize, candidateToastsSize);

        toasts.forEach(this.toastHelper::destroy);
        toasts.clear();

        candidateToasts.forEach(this.toastHelper::destroy);
        candidateToasts.clear();

        log.trace("ToastHandler is destroyed.");
    }
}
