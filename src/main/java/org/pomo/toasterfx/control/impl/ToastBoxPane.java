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
package org.pomo.toasterfx.control.impl;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.control.ToastBox;
import org.pomo.toasterfx.model.ToastType;

import java.util.function.BiConsumer;

/**
 * <h2>消息盒子 - 面板</h2>
 * <p>{@code ToastBox}的具体实现</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 16:50:04</p>
 * <p>更新时间：2020-09-23 16:50:04</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see ToastBox
 */
@Slf4j
public class ToastBoxPane extends VBox implements ToastBox {

    /**
     * 默认主题 - 样式表
     */
    public final static String DEFAULT_THEME_STYLESHEETS =
            ToastBoxPane.class.getResource(ToastBoxPane.class.getSimpleName() + ".css").toExternalForm();

    /**
     * 暗黑主题 - 样式表
     */
    public final static String DARK_THEME_STYLESHEETS =
            ToastBoxPane.class.getResource("dark/" + ToastBoxPane.class.getSimpleName() + ".css").toExternalForm();

    @Setter
    private BiConsumer<Event, Node> onClose;

    private BiConsumer<Event, Node> onAction;

    /**
     * 进度条
     */
    private ProgressBar progressBar;

    /**
     * 关闭按钮
     */
    private Button closeButton;

    /**
     * 内容节点的父容器
     */
    private StackPane nodeContainer;

    /**
     * 内容节点
     */
    @Getter
    private Node node;

    /**
     * 消息类型
     */
    private ToastType toastType;

    public ToastBoxPane() {

        getStyleClass().add("toast-box");

        // region {关闭按钮}
        this.closeButton = new Button();
        this.closeButton.getStyleClass().add("close-button");
        this.closeButton.setOnAction(this::onClose);

        StackPane graphic = new StackPane();
        graphic.getStyleClass().add("graphic");
        this.closeButton.setGraphic(graphic);
        // endregion

        // region {容器}
        StackPane container = new StackPane();
        container.getStyleClass().add("container");

        // Node 容器
        this.nodeContainer = new StackPane();
        container.getChildren().add(this.nodeContainer);

        container.getChildren().add(this.closeButton);
        StackPane.setAlignment(this.closeButton, Pos.TOP_RIGHT);

        getChildren().add(container);
        // endregion

        // region {进度条}
        this.progressBar = new ProgressBar(1);
        this.progressBar.setRotate(180); // 旋转180°，让进度条从右开始
        this.progressBar.setMaxWidth(Double.MAX_VALUE);// 与容器同宽
        getChildren().add(progressBar);
        // endregion
    }

    @Override
    public String getUserAgentStylesheet() {
        return DEFAULT_THEME_STYLESHEETS;
    }

    @Override
    public Parent getBox() {
        return this;
    }

    @Override
    public Node getNodeContainer() {
        return this.nodeContainer;
    }

    /**
     * <h2>设置 被包裹内容节点</h2>
     *
     * @param node 内容节点
     */
    public void setNode(Node node) {

        ObservableList<Node> children = this.nodeContainer.getChildren();

        if (this.node != null) children.remove(this.node);
        if (node != null) children.add(node);

        this.node = node;
    }

    /**
     * <h2>设置 消息类型</h2>
     *
     * @param toastType 消息类型
     */
    public void setToastType(ToastType toastType) {

        ObservableList<String> styleClass = this.getStyleClass();

        if (this.toastType != null) styleClass.removeAll(this.toastType.getStyleClass());

        if (toastType != null) styleClass.addAll(toastType.getStyleClass());

        this.toastType = toastType;
    }

    @Override
    public void setCloseButtonEnable(boolean enable) {
        this.closeButton.setVisible(enable);
        this.closeButton.setManaged(enable);
    }

    @Override
    public void setProgressEnable(boolean enable) {
        this.progressBar.setVisible(enable);
        this.progressBar.setManaged(enable);
    }

    @Override
    public double getProgress() {
        return this.progressBar.getProgress();
    }

    @Override
    public void setProgress(double value) {
        this.progressBar.setProgress(value);
    }

    @Override
    public void setOnAction(BiConsumer<Event, Node> onAction) {

        this.onAction = onAction;

        if (onAction == null) this.nodeContainer.setOnMouseClicked(null);
        else this.nodeContainer.setOnMouseClicked(this::onAction);
    }

    /**
     * <h2>点击事件</h2>
     *
     * @param event 事件
     */
    private void onAction(MouseEvent event) {
        if (this.onAction != null) this.onAction.accept(event, this.getNode());
    }

    /**
     * <h2>关闭事件</h2>
     *
     * @param event 事件
     */
    private void onClose(ActionEvent event) {
        if (this.onClose != null) this.onClose.accept(event, this.getNode());
    }


    @Override
    public void onHoverEnter() {
        if (this.onAction != null) this.setCursor(Cursor.HAND);
    }

    @Override
    public void onHoverExit() {
        if (this.onAction != null) this.setCursor(Cursor.DEFAULT);
    }

    @Override
    public void reset() {
        this.setProgress(1);
    }

    @Override
    public void onDestroy() {

        this.setNode(null);
        this.setToastType(null);

        this.onClose = null;
        this.onAction = null;

        this.closeButton.setOnAction(null);
        this.closeButton = null;

        this.nodeContainer.setOnMouseClicked(null);
        this.nodeContainer = null;

        this.progressBar = null;

        this.getChildren().clear();

        log.trace("ToastBoxPane is destroyed.");
    }
}
