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

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Popup;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.Actionable;
import org.pomo.toasterfx.common.Closeable;
import org.pomo.toasterfx.common.Dockable;
import org.pomo.toasterfx.common.ToasterAware;
import org.pomo.toasterfx.control.ToastBox;
import org.pomo.toasterfx.control.ToasterHoverListener;
import org.pomo.toasterfx.exception.NodeNotFoundException;
import org.pomo.toasterfx.model.Audio;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastState;
import org.pomo.toasterfx.model.ToasterState;
import org.pomo.toasterfx.model.scalable.CloseCondition;
import org.pomo.toasterfx.model.scalable.MutableStateToast;
import org.pomo.toasterfx.model.scalable.ProgressCondition;
import org.pomo.toasterfx.strategy.PopupStrategy;
import org.pomo.toasterfx.transition.ToasterTransition;
import org.pomo.toasterfx.transition.ToasterTransitionStay;
import org.pomo.toasterfx.util.FXUtils;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * <h2>消息者</h2>
 *
 * <p>负责展示消息，内部使用Popup实现</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 16:20:02</p>
 * <p>更新时间：2020-09-27 16:20:02</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class Toaster {

    // region {成员组件，不可变，除非销毁}
    /**
     * 消息者 工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterFactory toasterFactory;

    /**
     * 多消息体 工厂
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

    // region {注入属性，不可变，除非销毁}
    /**
     * 消息盒子
     */
    @Getter
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToastBox toastBox;

    /**
     * Toaster 进入动画
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterTransition transitionIn;

    /**
     * Toaster 退出动画
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterTransition transitionOut;

    /**
     * Toaster 进度动画
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterTransitionStay transitionStay;
    // endregion

    // region {自有属性，不可变，除非销毁}
    @Getter
    private Popup popup;

    /**
     * hover 监听
     */
    private ChangeListener<Boolean> hoverListener;

    /**
     * 消息盒子 点击事件
     */
    private BiConsumer<Event, Node> toastBoxOnAction;
    // endregion

    // region {自有属性，可变}
    /**
     * 消息
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Toast toast;


    /**
     * 当前弹窗的真实高度
     * 此高度可能因动画，而变化，因此，需储存一个
     */
    @Getter
    private double height;

    /**
     * 当前弹窗的真实宽度
     */
    @Getter
    private double width;

    /**
     * 消息状态
     */
    private ToasterState state = ToasterState.INITIAL;
    // endregion

    /**
     * <h2>初始化</h2>
     */
    void initialize() {

        Objects.requireNonNull(this.toasterFactory, "toasterFactory must non-null but is null.");
        Objects.requireNonNull(this.multiToastFactory, "multiToastFactory must non-null but is null.");
        Objects.requireNonNull(this.nodeHelper, "nodeHelper must non-null but is null.");
        Objects.requireNonNull(this.toastHelper, "toastHelper must non-null but is null.");

        Objects.requireNonNull(this.toastBox, "toastBox must non-null but is null.");
        Objects.requireNonNull(this.transitionIn, "transitionIn must non-null but is null.");
        Objects.requireNonNull(this.transitionOut, "transitionOut must non-null but is null.");
        Objects.requireNonNull(this.transitionStay, "transitionStay must non-null but is null.");

        this.popup = new Popup();
        this.popup.setAutoFix(false);
        this.popup.setHideOnEscape(false);
        this.popup.setAutoHide(false);
        this.popup.setOpacity(0);

        // 向ToastBox中，注入Toaster
        FXUtils.run(this.toastBox, ToasterAware.class, it -> it.setToaster(this));

        this.resetToastBox();

        // region {注册 ToastBox 的hover监听}
        this.hoverListener = (observable, oldValue, newValue) -> {

            // 消息者此时必须处于显示状态，才允许继续
            if (!this.isShow()) return;

            if (newValue) {

                this.toastBox.onHoverEnter();

                FXUtils.run(this.toastBox.getNode(),
                        ToasterHoverListener.class, ToasterHoverListener::onHoverEnter);

                this.transitionStay.onHoverEnter();

            } else {

                this.toastBox.onHoverExit();

                FXUtils.run(this.toastBox.getNode(),
                        ToasterHoverListener.class, ToasterHoverListener::onHoverExit);

                this.transitionStay.onHoverExit();
            }
        };
        this.toastBox.getBox().hoverProperty().addListener(this.hoverListener);
        // endregion

        // region {注册 ToastBox 关闭监听}
        this.toastBox.setOnClose((event, node) -> {

            // 必须是显示状态下才允许关闭
            if (this.state != ToasterState.SHOWN) return;

            final Toast toast = this.toast;

            // 根据返回值，调整是否关闭。(默认：关闭)
            boolean flag = FXUtils
                    .call(node, Closeable.class, it -> it.onClose(event, toast, node)).orElse(true);

            flag = FXUtils
                    .call(this.toast, Closeable.class, it -> it.onClose(event, toast, node)).orElse(flag);

            if (flag) this.close();
        });
        // endregion

        // region {声明 ToastBox 点击监听}
        this.toastBoxOnAction = (event, node) -> {

            // 必须是显示状态下才允许点击
            if (this.state != ToasterState.SHOWN) return;

            final Toast toast = this.toast;

            // 根据返回值，调整是否关闭。(默认：关闭)
            boolean flag = true;

            if (node instanceof Actionable && ((Actionable) node).hasAction())
                flag = ((Actionable) node).onAction(event, toast, node);

            if (toast instanceof Actionable && ((Actionable) toast).hasAction())
                flag = ((Actionable) toast).onAction(event, toast, node);

            if (flag) this.close();
        };
        // endregion

        // region {动画相关}
        this.transitionIn.setToaster(this);
        this.transitionIn.setOnFinished(it -> {

            this.state = ToasterState.SHOWN;
            FXUtils.run(this.toast, MutableStateToast.class, toast -> toast.setToastState(ToastState.SHOWN));

            // 存在持续时间，则播放进度动画
            if (this.toast.hasDuration()) this.transitionStay.play();
        });

        this.transitionStay.setToaster(this);
        this.transitionStay.setOnEnd(this::close);

        this.transitionOut.setToaster(this);
        this.transitionOut.setOnFinished(this::exited);
        // endregion
    }

    /**
     * <h2>是否空闲</h2>
     * <p>当消息者处于 初始状态 或 重置 时，方为空闲</p>
     */
    boolean isIdle() {
        return this.state == ToasterState.INITIAL || this.state == ToasterState.RESET;
    }

    /**
     * <h2>使用此 消息者</h2>
     * <p>使用后，此 消息者 处于忙碌状态（非空闲）</p>
     */
    void use() {

        if (!this.isIdle())
            throw new IllegalArgumentException("current toaster of state is " + this.state + ", stop use().");

        this.state = ToasterState.PREPARE;
    }

    /**
     * <h2>是否处于显示中</h2>
     *
     * @return 是/否
     */
    boolean isShow() {
        return this.state == ToasterState.SHOWN || this.state == ToasterState.SHOWING;
    }

    /**
     * <h2>准备</h2>
     * <p>只会在ui线程中被调用</p>
     */
    private void prepare() {

        if (this.state != ToasterState.PREPARE)
            throw new IllegalArgumentException("current toaster of state is " + this.state + ", stop prepare().");

        final Toast toast = this.toast;

        // 向Toast中，注入Toaster
        FXUtils.run(toast, ToasterAware.class, it -> it.setToaster(this));

        // 得到或创建一个Node
        Node node = this.nodeHelper.get(toast);

        // 向Node中，注入Toaster
        FXUtils.run(node, ToasterAware.class, it -> it.setToaster(this));

        this.toastBox.setNode(node);
        this.toastBox.setToastType(toast.getType());

        // 此toast，是否需要进度条展示
        FXUtils.call(toast, ProgressCondition.class, ProgressCondition::isProgress)
                .ifPresent(this.toastBox::setProgressEnable);

        // 此toast，是否需要关闭按钮展示
        FXUtils.call(toast, CloseCondition.class, CloseCondition::isShowClose)
                .ifPresent(this.toastBox::setCloseButtonEnable);

        // 是否需要注册点击事件
        if ((node instanceof Actionable && ((Actionable) node).hasAction())
                || (toast instanceof Actionable && ((Actionable) toast).hasAction())) {
            this.toastBox.setOnAction(this.toastBoxOnAction);
        }

        this.popup.getContent().add(this.toastBox.getBox());
    }

    /**
     * <h2>显示</h2>
     *
     * @param strategy 弹出策略
     */
    void show(PopupStrategy strategy) {

        FXUtils.checkFxUserThread();

        this.prepare();

        // 若没有在显示中，初次
        if (!this.popup.isShowing()) {

            this.popup.show(this.toasterFactory.getBackgroundWindow());

        } else {

            this.popup.sizeToScene();
            Bounds boundsInParent = this.toastBox.getBox().getBoundsInParent();
            this.popup.setWidth(boundsInParent.getWidth());
            this.popup.setHeight(boundsInParent.getHeight());
        }

        this.height = this.popup.getHeight();
        this.width = this.popup.getWidth();

        strategy.anchor(this);

        this.state = ToasterState.SHOWING;

        final Toast toast = this.toast;
        final Node node = this.toastBox.getNode();

        // 修改消息体状态为显示中
        FXUtils.run(toast, MutableStateToast.class, it -> it.setToastState(ToastState.SHOWING));

        // 触发Dockable.dock
        FXUtils.run(toast, Dockable.class, it -> it.dock(toast, node));
        FXUtils.run(node, Dockable.class, it -> it.dock(toast, node));

        // 准备停留动画，此处应提前准备。否则当尚未展示完毕就hover，会报错
        this.transitionStay.prepare();

        // 准备 并 播放进入动画
        this.transitionIn.readyPlay();

        // 存在音效时，播放音效
        toast.getParameter().getAudio().ifPresent(Audio::play);
    }

    /**
     * <h2>封存归档，并重置</h2>
     * <p>当Toaster过多时，将稍前的Toaster进行归档，防止信息丢失</p>
     */
    public void archive() {

        FXUtils.checkFxUserThread();

        // 只有可显示的，才允许归档
        if (!this.isShow())
            throw new IllegalArgumentException("current toaster of state is " + this.state + ", stop archive().");

        // 修改Toast状态为：归档中
        FXUtils.run(this.toast, MutableStateToast.class, it -> it.setToastState(ToastState.ARCHIVING));

        // 开始退出
        this.exit();
    }

    /**
     * <h2>关闭此Toaster，会播放退出动画</h2>
     * <p>会修改Toast的状态 -> CLOSING</p>
     * <pre>
     *     1、超时会自动调用此方法
     *     2、手动点击×，会调用此方法
     *     3、触发消息的action，可能会调用此方法
     * </pre>
     */
    public void close() {

        FXUtils.checkFxUserThread();

        // 当前Toaster，若处于未显示状态，则忽略后续执行
        if (!this.isShow()) return;

        ToastState toastState = this.toast.getState();

        // 消息体若处于“已显示”，则修改为“关闭中”
        if (toastState == ToastState.SHOWN)
            FXUtils.run(this.toast, MutableStateToast.class, it -> it.setToastState(ToastState.CLOSING));
        else if (toastState != ToastState.HIDE)
            throw new IllegalArgumentException("current toast of state is " + toastState + ", stop close().");

        this.exit();
    }

    /**
     * <h2>退出</h2>
     * <p>播放退出动画</p>
     * <p>最终会调用this.exited();【一定会被调用，除非顶级销毁】</p>
     */
    public void exit() {

        FXUtils.checkFxUserThread();

        // 当前若处于未显示状态
        if (!this.isShow())
            throw new IllegalArgumentException("current toaster of state is " + this.state + ", stop exit().");

        // 退出时，停止进入与保持动画
        this.transitionIn.stop();
        this.transitionStay.stop();

        // 保证其不会再被校正队列位置
        this.state = ToasterState.HIDING;

        this.transitionOut.readyPlay();
    }

    /**
     * <h2>已完全退出</h2>
     * <p>会重置此类，并从可见Toaster中拉出此实例</p>
     *
     * @param event 事件
     */
    private void exited(ActionEvent event) {

        // 当前未处于隐藏中状态
        if (this.state != ToasterState.HIDING) return;

        FXUtils.checkFxUserThread();

        this.state = ToasterState.HIDDEN;

        // 是否因归档而移除
        boolean fromArchive = this.toast.getState() == ToastState.ARCHIVING;

        this.reset(false);

        // 从可见队列中移除，可能会释放Toaster
        this.toasterFactory.remove(this, fromArchive);
    }

    /**
     * <h2>重置此类</h2>
     * <p>不应在此方法中，销毁Toast</p>
     *
     * @param force 是否强制
     */
    void reset(boolean force) {

        if (!force && this.state != ToasterState.HIDDEN)
            throw new IllegalArgumentException("current toaster of state is " + this.state + ", stop reset().");

        FXUtils.checkFxUserThread();

        // 停止动画
        this.stopTransition();

        this.resetPopup();

        this.resetToast(force);

        this.resetToastBox();

        this.state = ToasterState.RESET;
    }

    /**
     * <h2>重置消息体&Node</h2>
     *
     * @param force 是否强制
     */
    private void resetToast(boolean force) {

        // 获取Node
        final Toast toast = this.toast;
        final Node node = this.nodeHelper.tryGet(toast).orElseThrow(NodeNotFoundException::new);

        // 解除与Toaster的bind
        FXUtils.run(toast, ToasterAware.class, it -> it.setToaster(null));
        FXUtils.run(node, ToasterAware.class, it -> it.setToaster(null));

        // 触发Dockable.unDock
        FXUtils.run(toast, Dockable.class, it -> it.unDock(toast, node));
        FXUtils.run(node, Dockable.class, it -> it.unDock(toast, node));

        ToastState toastState = toast.getState();

        // 强制时 或 处于关闭中，正常销毁Toast、Node
        if (force || toastState == ToastState.CLOSING) {

            this.toastHelper.destroy(toast);

        } else if (toastState == ToastState.ARCHIVING) {// 处于归档中，执行归档

            // 归档，可能会销毁Node
            this.multiToastFactory.archive(toast);

        } else if (toastState == ToastState.HIDE) {// 处于隐藏状态，仅销毁Node

            this.nodeHelper.forget(toast);

        } else log.error("unknown toast of state : {}.", toastState);

        this.toast = null;
    }

    /**
     * <h2>重置 popup</h2>
     */
    private void resetPopup() {

        this.popup.setOpacity(0);
        this.popup.setOnShowing(null);
        this.popup.setOnShown(null);
        this.popup.getContent().clear();

        // 若hide，当大量popup时，会抛出NPE
        // popup.hide();
    }

    /**
     * <h2>重置消息盒子</h2>
     */
    private void resetToastBox() {

        // 流程化操作，不应放到具体实现中
        this.toastBox.setOnAction(null);
        this.toastBox.setNode(null);
        this.toastBox.setToastType(null);
        this.toastBox.setProgressEnable(this.toasterFactory.isDefaultProgressEnable());
        this.toastBox.setCloseButtonEnable(this.toasterFactory.isDefaultCloseButtonEnable());

        this.toastBox.reset();
    }

    /**
     * <h2>停止动画</h2>
     * <p>可以异步调用</p>
     */
    private void stopTransition() {

        this.transitionIn.stop();
        this.transitionOut.stop();
        this.transitionStay.stop();
    }

    /**
     * <h2>校正高度</h2>
     * <p>得到当前popup的真实高度</p>
     * <p>并重新调整队列位置</p>
     */
    public void fixHeight() {

        // 必须调用此方法后，布局高度才回被重新计算
        Parent parent = this.getToastBox().getBox().getParent();
        parent.layout();
        parent.autosize();

        this.height = this.popup.getHeight();

        this.toasterFactory.adjustVisualList();
    }

    /**
     * <h2>销毁</h2>
     */
    void destroy() {

        if (!this.isIdle())
            throw new IllegalArgumentException("current toaster of state is " + this.state + ", stop destroy().");

        this.state = ToasterState.DESTROY;

        this.toastBox.getBox().hoverProperty().removeListener(this.hoverListener);
        this.hoverListener = null;

        this.toastBoxOnAction = null;

        this.popup.hide();

        this.toastBox.onDestroy();
        this.toastBox = null;

        this.toasterFactory = null;
        this.popup = null;

        this.transitionIn.setToaster(null);
        this.transitionIn.onDestroy();
        this.transitionIn = null;

        this.transitionOut.setToaster(null);
        this.transitionOut.onDestroy();
        this.transitionOut = null;

        this.transitionStay.setToaster(null);
        this.transitionStay.onDestroy();
        this.transitionStay = null;
    }
}