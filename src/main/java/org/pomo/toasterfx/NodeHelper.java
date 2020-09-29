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

import javafx.scene.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.Archiveable;
import org.pomo.toasterfx.common.Destroyable;
import org.pomo.toasterfx.exception.NotFoundNodeCreateMethodException;
import org.pomo.toasterfx.model.ReferenceType;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.scalable.NodeCreateable;
import org.pomo.toasterfx.model.scalable.NodeDestroyable;
import org.pomo.toasterfx.model.scalable.NodeRecyclable;
import org.pomo.toasterfx.util.FXUtils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * <h2>Node 助理</h2>
 *
 * <p>负责维护Node的引用</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:58:42</p>
 * <p>更新时间：2020-09-27 15:58:42</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class NodeHelper {

    // region {成员变量}
    /**
     * 消息体 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToastHelper toastHelper;

    /**
     * 默认的引用类型
     */
    @Setter
    private ReferenceType referenceType = ReferenceType.DESTROY;

    /**
     * 强引用 映射<br/>
     * 线程安全
     */
    private Map<Toast, Node> map;

    /**
     * 引用 映射<br/>
     * 线程安全
     */
    private Map<Toast, Reference<Node>> referenceMap;

    /**
     * 引用队列
     */
    private ReferenceQueue<Node> referenceQueue;

    /**
     * 引用队列处理器
     */
    private Thread referenceQueueHandler;
    // endregion

    /**
     * <h2>初始化</h2>
     */
    void initialize() {

        Objects.requireNonNull(this.toastHelper, "toastHelper must non-null but is null.");

        this.map = Collections.synchronizedMap(new HashMap<>());
    }

    /**
     * <h2>启用引用队列</h2>
     */
    private void enableReferenceQueue() {

        if (this.referenceQueue != null) return;

        synchronized (this) {

            if (this.referenceQueue != null) return;

            this.referenceMap = Collections.synchronizedMap(new HashMap<>());
            this.referenceQueue = new ReferenceQueue<>();
        }

        String threadName = "NodeHelper-Thread-" + this.hashCode();
        this.referenceQueueHandler = new Thread(() -> {

            while (true) {

                try {

                    // 没有被GC时，处于阻塞状态
                    Reference<? extends Node> reference = this.referenceQueue.remove();

                    if (reference instanceof ToastSupplier) {

                        Toast toast = ((ToastSupplier) reference).getToast();

                        log.trace("{} of Node is recovery.", toast);

                        FXUtils.run(toast, NodeRecyclable.class, NodeRecyclable::onNodeRecycle);

                    } else log.error("unknown reference : {}", reference.getClass().getName());

                } catch (InterruptedException e) {

                    log.trace("{} is interrupted.", threadName);
                    break;

                } catch (Throwable e) {

                    // 被销毁了
                    if (this.referenceMap == null) break;

                    log.error("unknown error causing reference queue handler is interrupted.", e);
                }
            }

            this.referenceQueue = null;
        });
        this.referenceQueueHandler.setName(threadName);
        this.referenceQueueHandler.setDaemon(true);
        this.referenceQueueHandler.start();
        log.debug("{} start...", threadName);
    }

    /**
     * <h2>得到引用-映射的数量</h2>
     *
     * @return 数量
     */
    public int getReferenceMapSize() {
        return this.referenceMap == null ? -1 : this.referenceMap.size();
    }

    /**
     * <h2>得到</h2>
     * <p>当不存在时，创建</p>
     *
     * @param toast 消息体
     * @return Node
     */
    Node get(@NonNull Toast toast) {

        FXUtils.checkFxUserThread();

        Node node = this.map.get(toast);

        if (node == null) {

            // 尝试从引用中提出，得不到时 —— 创建
            node = this.tryTakeByReference(toast).orElseGet(() -> this.create(toast));

            // 添加强引用
            this.map.put(toast, node);
        }

        return node;
    }

    /**
     * <h2>尝试得到</h2>
     * <p>当不存在时，不会创建</p>
     * <p>不会从引用中获取</p>
     *
     * @param toast 消息体
     * @return NodeOptional
     */
    Optional<Node> tryGet(@NonNull Toast toast) {
        return Optional.ofNullable(this.map.get(toast));
    }

    /**
     * <h2>归档</h2>
     * <p>仅会触发Node的onArchive</p>
     *
     * @param toast         消息体
     * @param node          Node(可能为null)
     * @param referenceType 引用类型(可能为null)
     */
    void archive(@NonNull Toast toast, Node node, ReferenceType referenceType) {

        if (this.referenceMap != null && this.referenceMap.containsKey(toast))
            throw new IllegalArgumentException("toast[" + toast + "] already archived.");

        if (this.map.get(toast) != node)
            throw new IllegalArgumentException("toast doesn't match node.");

        // 归档操作，并非有Node而触发，因此Toast的归档回调不应放于此处
        // 尝试执行Node的归档，node为null时，会忽略
        referenceType = FXUtils.call(node, Archiveable.class, it -> it.onArchive(toast, node))
                .orElse(referenceType);

        // 为null时，赋予默认值
        referenceType = referenceType == null ? this.referenceType : referenceType;

        // 归档策略为强引用 或 (当Node尚未生成 且 归档策略不为销毁时)
        if (referenceType == ReferenceType.STRONG ||
                (node == null && referenceType != ReferenceType.DESTROY)) return;

        // 此时，要么node不为null，要么归档策略为：销毁

        switch (referenceType) {
            case DESTROY:
                this.forget(toast);
                break;
            case WEAK:
                this.enableReferenceQueue();
                this.strong2Reference(toast, new ToastWeakReference(toast, node));
                break;
            case SOFT:
                this.enableReferenceQueue();
                this.strong2Reference(toast, new ToastSoftReference(toast, node));
                break;
        }
    }


    /**
     * <h2>忘记</h2>
     * <p>解除Toast与Node的关联关系</p>
     * <p>调用Toast、Node的onNodeDestroy</p>
     *
     * @param toast 消息体
     */
    void forget(@NonNull Toast toast) {

        Node node = this.map.remove(toast);

        if (node == null) node = this.tryTakeByReference(toast).orElse(null);

        if (node != null) this.destroyNode(toast, node);
    }

    /**
     * <h2>创建Node</h2>
     * <p>会触发Createable的onCreate</p>
     *
     * @param toast 消息体
     * @return Node
     */
    private Node create(@NonNull Toast toast) {

        Node node = FXUtils.call(toast, NodeCreateable.class, it -> it.createNode(toast))
                .orElseThrow(NotFoundNodeCreateMethodException::new);

        log.trace("Node[{}] is created.", node);

        return node;
    }

    /**
     * <h2>强引用 转 引用</h2>
     *
     * @param toast     消息体
     * @param reference 引用
     */
    private void strong2Reference(@NonNull Toast toast, Reference<Node> reference) {
        this.referenceMap.put(toast, reference);
        this.map.remove(toast);
    }

    /**
     * <h2>销毁Node</h2>
     * <p>调用Toast、Node的onNodeDestroy</p>
     *
     * @param toast 消息体
     * @param node  Node
     */
    private void destroyNode(@NonNull Toast toast, @NonNull Node node) {

        // Node的销毁，触发的Toast的回调，因此↓，应该放于此处
        FXUtils.run(toast, NodeDestroyable.class, it -> it.onNodeDestroy(node));

        // 尝试执行Node的Node销毁回调，Node为null时，会忽略
        FXUtils.run(node, Destroyable.class, Destroyable::onDestroy);
    }

    /**
     * <h2>尝试提出，从引用映射中</h2>
     *
     * @param toast 消息体
     * @return NodeOptional
     */
    private Optional<Node> tryTakeByReference(@NonNull Toast toast) {

        if (this.referenceMap == null) return Optional.empty();

        Reference<Node> reference = this.referenceMap.remove(toast);
        Node node = null;

        if (reference != null) {

            node = reference.get();
            reference.clear();
        }

        return Optional.ofNullable(node);
    }

    /**
     * <h2>销毁</h2>
     * <p>此处只负责销毁Node，Toast的集中销毁，交由ToasterHelper</p>
     * <p>一般来讲，ToastHelper、MultiToastFactory销毁后，此类中应该不再存在关联关系了</p>
     */
    void destroy() {

        if (this.referenceQueueHandler != null) {
            this.referenceQueueHandler.interrupt();
            this.referenceQueueHandler = null;
        }

        Map<Toast, Node> map = this.map;
        this.map = null;// 确保接下来的遍历不会出错

        Map<Toast, Reference<Node>> referenceMap = this.referenceMap;
        this.referenceMap = null;// 确保接下来的遍历不会出错

        if (!map.isEmpty()) {

            log.warn("{} not destroyed on strong map", map.size());

            Iterator<Map.Entry<Toast, Node>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {

                Map.Entry<Toast, Node> entry = iterator.next();
                Toast toast = entry.getKey();

                this.destroyNode(toast, entry.getValue());
                this.toastHelper.destroyToast(toast);

                iterator.remove();
            }
        }

        if (referenceMap != null && !referenceMap.isEmpty()) {

            log.warn("{} not destroyed on reference map", referenceMap.size());

            Iterator<Map.Entry<Toast, Reference<Node>>> iterator = referenceMap.entrySet().iterator();
            while (iterator.hasNext()) {

                Map.Entry<Toast, Reference<Node>> entry = iterator.next();
                Toast toast = entry.getKey();
                Reference<Node> reference = entry.getValue();

                this.destroyNode(toast, reference.get());
                this.toastHelper.destroy(toast);

                reference.clear();
                iterator.remove();
            }
        }

        this.referenceType = null;

        log.trace("NodeHelper is destroyed.");
    }

    // region {ToastReference}

    /**
     * <h2>消息体获取器</h2>
     *
     * <p>用于获取消息体</p>
     * <br/>
     *
     * <p>创建时间：2020-09-27 16:12:56</p>
     * <p>更新时间：2020-09-27 16:12:56</p>
     *
     * @author Mr.Po
     * @version 1.0
     */
    private interface ToastSupplier {

        /**
         * <h2>得到消息体</h2>
         *
         * @return 消息体
         */
        Toast getToast();
    }

    /**
     * <h2>消息体弱引用</h2>
     *
     * <p>实现了{@code ToastSupplier}，的弱引用</p>
     * <br/>
     *
     * <p>创建时间：2020-09-27 16:13:35</p>
     * <p>更新时间：2020-09-27 16:13:35</p>
     *
     * @author Mr.Po
     * @version 1.0
     * @see ToastSupplier
     */
    private class ToastWeakReference extends WeakReference<Node> implements ToastSupplier {

        @Getter
        private final Toast toast;

        public ToastWeakReference(@NonNull Toast toast, @NonNull Node referent) {

            super(referent, NodeHelper.this.referenceQueue);

            this.toast = toast;
        }
    }

    /**
     * <h2>消息体弱引用</h2>
     *
     * <p>实现了{@code ToastSupplier}，的软引用</p>
     * <br/>
     *
     * <p>创建时间：2020-09-27 16:13:35</p>
     * <p>更新时间：2020-09-27 16:13:35</p>
     *
     * @author Mr.Po
     * @version 1.0
     * @see ToastSupplier
     */
    private class ToastSoftReference extends SoftReference<Node> implements ToastSupplier {

        @Getter
        private final Toast toast;

        public ToastSoftReference(@NonNull Toast toast, @NonNull Node referent) {

            super(referent, NodeHelper.this.referenceQueue);

            this.toast = toast;
        }
    }
    // endregion
}