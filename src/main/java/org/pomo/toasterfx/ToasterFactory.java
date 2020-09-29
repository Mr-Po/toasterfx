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

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.component.BackgroundWindow;
import org.pomo.toasterfx.control.ToastBox;
import org.pomo.toasterfx.control.impl.ToastBoxPane;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.pomo.toasterfx.strategy.PopupStrategy;
import org.pomo.toasterfx.strategy.impl.RightBottomPopupStrategy;
import org.pomo.toasterfx.transition.ToasterTransition;
import org.pomo.toasterfx.transition.ToasterTransitionStay;
import org.pomo.toasterfx.transition.impl.RightBottomToasterTransitionIn;
import org.pomo.toasterfx.transition.impl.RightToasterTransitionOut;
import org.pomo.toasterfx.transition.impl.ToasterTransitionProgress;
import org.pomo.toasterfx.util.FXMessages;
import org.pomo.toasterfx.util.FXUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <h2>消息者 工厂</h2>
 *
 * <p>负责生产“消息者”</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 16:28:05</p>
 * <p>更新时间：2020-09-27 16:28:05</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class ToasterFactory {

    // region {成员组件}
    /**
     * 国际化消息
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private FXMessages messages;

    /**
     * 多消息者工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private MultiToastFactory multiToastFactory;

    /**
     * Node 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private NodeHelper nodeHelper;

    /**
     * 消息体 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToastHelper toastHelper;
    // endregion

    // region {成员变量}
    /**
     * 消息盒子 生成器
     */
    @Setter
    @Getter
    @NonNull
    private Supplier<ToastBox> toastBoxSupplier;

    /**
     * 进入动画生成器
     */
    @Setter
    @Getter
    @NonNull
    private Supplier<ToasterTransition> transitionInSupplier;

    /**
     * 退出动画生成器
     */
    @Setter
    @Getter
    @NonNull
    private Supplier<ToasterTransition> transitionOutSupplier;

    /**
     * 停留动画生成器
     */
    @Setter
    @Getter
    @NonNull
    private Supplier<ToasterTransitionStay> transitionStaySupplier;

    /**
     * 默认的 进度 是否启用
     */
    @Getter
    @Setter
    private boolean defaultProgressEnable = true;

    /**
     * 默认的 关闭按钮 是否启用
     */
    @Getter
    @Setter
    private boolean defaultCloseButtonEnable = true;

    /**
     * 是否使用默认的 消息类型样式表
     */
    @Getter
    @Setter
    private boolean useDefaultToastTypeStyleSheets = true;

    /**
     * 消息者窗体<br/>
     * 用于进行弹窗展示
     */
    @Setter
    @NonNull
    private ToasterWindow window;

    /**
     * 消息者 池<br/>
     * 包含所有的消息者
     */
    @Setter
    @NonNull
    private Set<Toaster> pool;


    /**
     * 展示中的Toaster 列表<br/>
     * 只会在ui线程中被操作，允许使用线程不安全的实例
     * 数量不会太多
     */
    @Setter
    @NonNull
    private List<Toaster> visualToasters;

    /**
     * 弹出策略
     */
    @Setter
    @Getter
    @NonNull
    private PopupStrategy popupStrategy;

    /**
     * 可观察屏幕集合
     */
    private ObservableList<Screen> screens;

    /**
     * 屏幕集无效化监听
     */
    private InvalidationListener screensInvalidationListener;
    // endregion

    /**
     * <h2>初始化</h2>
     */
    void initialize() {

        Objects.requireNonNull(this.messages, "messages must non-null but is null.");
        Objects.requireNonNull(this.nodeHelper, "nodeHelper must non-null but is null.");
        Objects.requireNonNull(this.toastHelper, "toastHelper must non-null but is null.");
        Objects.requireNonNull(this.multiToastFactory, "multiToastFactory must non-null but is null.");

        if (this.toastBoxSupplier == null) this.toastBoxSupplier = ToastBoxPane::new;

        if (this.transitionInSupplier == null)
            this.transitionInSupplier = () -> new RightBottomToasterTransitionIn(Duration.seconds(0.4), 0.93);

        if (this.transitionOutSupplier == null)
            this.transitionOutSupplier = () -> new RightToasterTransitionOut(Duration.seconds(0.6));

        if (this.transitionStaySupplier == null)
            this.transitionStaySupplier = () -> new ToasterTransitionProgress(0.93);

        if (this.pool == null) this.pool = new HashSet<>();
        if (this.visualToasters == null) this.visualToasters = new LinkedList<>();

        if (this.popupStrategy == null)
            this.popupStrategy = new RightBottomPopupStrategy(this.multiToastFactory, Duration.seconds(0.35));

        if (this.window == null)
            this.window = new BackgroundWindow();

        this.screens = Screen.getScreens();

        // 当屏幕边界变化时，校正可见队列
        this.screensInvalidationListener = observable -> {
            if (!this.screens.isEmpty()) this.adjustVisualList();
        };
        this.screens.addListener(this.screensInvalidationListener);

        if (this.isUseDefaultToastTypeStyleSheets())
            this.window.getStylesheets().add(ToastTypes.DEFAULT_STYLE_SHEETS);
    }

    /**
     * <h2>得到 窗体</h2>
     *
     * @return 窗体
     */
    Window getWindow() {
        return this.window.getWindow();
    }

    /**
     * <h2>得到 展示中的消息者数量</h2>
     *
     * @return 展示中的消息者数量
     */
    public int getVisualSize() {
        return this.visualToasters.size();
    }

    /**
     * <h2>得到样式表</h2>
     *
     * @return 可观察的样式表
     */
    public ObservableList<String> getStylesheets() {
        return this.window.getStylesheets();
    }

    /**
     * <h2>清空当前可见&缓存的消息者池</h2>
     * <p>必须在ui线程调用此方法</p>
     */
    public void clear() {

        FXUtils.checkFxUserThread();

        this.clear(this.visualToasters, this.pool);
    }

    /**
     * <h2>显示</h2>
     *
     * <p>显示 此消息至指定位置</p>
     * <p>会校正 可见队列</p>
     *
     * @param toast 消息
     * @param index 消息所在列表的下标
     */
    void show(@NonNull Toast toast, int index) {

        Toaster toaster = this.born();

        toaster.setToast(toast);

        this.visualToasters.add(index, toaster);

        // 调用此方法后，当前执行会被挂起，其他执行可能会使用此UI线程执行新的show
        toaster.show(this.popupStrategy);

        this.adjustVisualList();
    }

    /**
     * <h2>显示</h2>
     *
     * <p>显示 此消息至末尾</p>
     * <p>会校正 可见队列</p>
     *
     * @param toast 消息
     */
    void show(@NonNull Toast toast) {
        this.show(toast, this.visualToasters.size());
    }

    /**
     * <h2>移除</h2>
     *
     * <p>将指定Toaster从 可见Toaster列表 中移除</p>
     * <p>执行完毕后，会校正 可见Toaster队列</p>
     * <p>可能会执行this.clean();</p>
     *
     * @param toaster     消息者
     * @param fromArchive 是否因归档而移除
     */
    void remove(@NonNull Toaster toaster, boolean fromArchive) {

        this.visualToasters.remove(toaster);

        // 不因归档而移除，即：超时关闭、手动关闭、触发action关闭
        if (!fromArchive) {

            // 消息列表 与 待显示消息列表 为空
            if (this.toastHelper.isEmpty() &&
                    (this.visualToasters.isEmpty() // 显示中的Toaster列表为空
                            || this.visualToasters.stream().map(Toaster::getToast).noneMatch(Toast::hasDuration))// 只存在驻留
            ) this.clean();

            this.adjustVisualList();
        }
    }

    /**
     * <h2>校正 可见队列</h2>
     */
    void adjustVisualList() {

        // 不为空时，才执行校正调节
        if (!this.visualToasters.isEmpty()) {

            // 只要显示中的，即：SHOWING、SHOWN
            List<Toaster> list = this.visualToasters.stream()
                    .filter(Toaster::isShow)
                    .collect(Collectors.toList());

            this.popupStrategy.adjust(list);
        }
    }

    /**
     * <h2>创建一个 消息者</h2>
     * <p>进行必要的参数初始化</p>
     *
     * @return 消息者
     */
    private Toaster create() {

        Toaster toaster = new Toaster();

        toaster.setToasterFactory(this);
        toaster.setMultiToastFactory(this.multiToastFactory);
        toaster.setNodeHelper(this.nodeHelper);
        toaster.setToastHelper(this.toastHelper);

        toaster.setToastBox(this.toastBoxSupplier.get());
        toaster.setTransitionIn(this.transitionInSupplier.get());
        toaster.setTransitionOut(this.transitionOutSupplier.get());
        toaster.setTransitionStay(this.transitionStaySupplier.get());

        toaster.initialize();

        pool.add(toaster);

        log.trace("a new Toaster is created, toaster pool : {}.", pool.size());

        return toaster;
    }

    /**
     * <h2>生成一个 Toaster</h2>
     * <p>可能来自创建，也可能来自POOL</p>
     *
     * @return Popup
     */
    private Toaster born() {

        FXUtils.checkFxUserThread();

        Toaster toaster = this.pool.stream()
                .filter(Toaster::isIdle)
                .findAny()
                .orElseGet(this::create);

        toaster.use();

        // 判断背景Window是否处于显示中
        if (!this.window.isShowing()) this.window.show();

        return toaster;
    }

    /**
     * <h2>清空指定Toasters</h2>
     *
     * @param visualToasters 可见消息者
     * @param pool           消息者池
     */
    private void clear(@NonNull List<Toaster> visualToasters, @NonNull Set<Toaster> pool) {

        log.debug("{} visual toaster left, {} toaster pool left, will be force cleaned up.",
                visualToasters.size(), pool.size());

        // 重置 展示中 的Toaster，会销毁其内部的Toast、Node
        visualToasters.forEach(it -> it.reset(true));
        visualToasters.clear();

        pool.forEach(Toaster::destroy);
        pool.clear();
    }

    /**
     * <h2>清理</h2>
     *
     * <p>空闲的Toaster，并将其销毁Toaster</p>
     */
    private void clean() {

        FXUtils.checkFxUserThread();

        // 待清理列表
        List<Toaster> list = this.pool.stream().filter(Toaster::isIdle).collect(Collectors.toList());

        // 移除
        this.pool.removeAll(list);

        //销毁
        list.forEach(Toaster::destroy);

        log.debug("clean: {}, {} toaster pool left.", list.size(), this.pool.size());

        list.clear();

        // 当池中没有Toaster时，隐藏背景Window
        if (this.pool.isEmpty()) this.window.close();
    }

    /**
     * <h2>销毁</h2>
     */
    void destroy() {

        this.screens.removeListener(this.screensInvalidationListener);
        this.screensInvalidationListener = null;

        List<Toaster> visualToasters = this.visualToasters;
        this.visualToasters = null;// 确保接下来的遍历不会出错

        Set<Toaster> pool = this.pool;
        this.pool = null;// 确保接下来的遍历不会出错

        this.popupStrategy.onDestroy();
        this.popupStrategy = null;

        this.clear(visualToasters, pool);

        this.window.getStylesheets().clear();
        this.window.close();
        this.window = null;

        log.trace("ToasterFactory is destroyed.");
    }
}
