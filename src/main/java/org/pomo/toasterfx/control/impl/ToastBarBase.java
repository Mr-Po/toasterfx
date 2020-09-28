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

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.common.Destroyable;
import org.pomo.toasterfx.common.ToasterAware;
import org.pomo.toasterfx.control.ToasterHoverListener;

/**
 * <h2>消息 - 条 - 基础</h2>
 * <p>用于展示消息的Node</p>
 * <p>支持图标、内容文本</p>
 * <p>支持自动展、卷长文本内容</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 16:44:52</p>
 * <p>更新时间：2020-09-23 16:44:52</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor
public class ToastBarBase extends HBox
        implements ToasterAware, ToasterHoverListener, Destroyable {

    /**
     * 默认主题样式表
     */
    public final static String DEFAULT_THEME_STYLESHEETS =
            ToastBar.class.getResource(ToastBarBase.class.getSimpleName() + ".css").toExternalForm();

    /**
     * 暗黑主题 - 样式表
     */
    public final static String DARK_THEME_STYLESHEETS =
            ToastBar.class.getResource("dark/" + ToastBarBase.class.getSimpleName() + ".css").toExternalForm();

    /**
     * 图标容器
     */
    private final StackPane graphicContainer;

    /**
     * 右容器
     */
    @Getter(AccessLevel.PROTECTED)
    private final VBox rightContainer;

    /**
     * 内容标签
     */
    private final Label contentLabel;

    /**
     * 原始的最大高度
     */
    private Double originalMaxHeight;

    @Setter
    private Toaster toaster;

    {
        this.getStyleClass().add("toast-bar-base");

        this.graphicContainer = new StackPane();
        this.graphicContainer.getStyleClass().add("graphic-container");

        this.contentLabel = new Label();
        this.contentLabel.getStyleClass().add("content");
        this.contentLabel.setWrapText(true);// 自动换行，但不会撑大父容器的MaxHeight

        this.rightContainer = new VBox(this.contentLabel);
        this.rightContainer.getStyleClass().add("right-container");
        HBox.setHgrow(this.rightContainer, Priority.ALWAYS);

        ObservableList<Node> children = this.getChildren();
        children.add(this.graphicContainer);
        children.add(this.rightContainer);
    }

    /**
     * @param content 内容
     * @param graphic 图标
     */
    public ToastBarBase(String content, Node graphic) {

        this.setContent(content);
        this.setGraphic(graphic);
    }

    /**
     * <p>使用css进行图标绘制graphic</p>
     *
     * @param content 内容
     */
    public ToastBarBase(String content) {
        this.setContent(content);

        Pane graphic = new Pane();
        graphic.getStyleClass().add("graphic");

        this.setGraphic(graphic);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DEFAULT_THEME_STYLESHEETS;
    }

    /**
     * <h2>得到内容文本的可观察对象</h2>
     *
     * @return 内容文本的可观察对象
     */
    public StringProperty getContentProperty() {
        return this.contentLabel.textProperty();
    }

    /**
     * <h2>设置内容文本</h2>
     *
     * @param content 内容文本
     */
    public void setContent(String content) {
        this.contentLabel.setText(content);
    }

    /**
     * <h2>设置图标</h2>
     *
     * @param graphic 图标Node
     */
    public void setGraphic(Node graphic) {
        ObservableList<Node> children = this.graphicContainer.getChildren();
        children.clear();

        if (graphic != null) children.add(graphic);
    }

    /**
     * <h2>检查是否需要扩张</h2>
     *
     * @return 是/否
     */
    protected boolean checkExpansion() {

        double originalHeight = this.contentLabel.getHeight();
        double targetHeight = this.contentLabel.prefHeight(this.contentLabel.getWidth());

        return targetHeight - originalHeight != 0;
    }

    @Override
    public void onHoverEnter() {

        if (checkExpansion()) {

            // 记录 原始 最大高度
            this.originalMaxHeight = this.getMaxHeight();

            this.setMaxHeight(Region.USE_COMPUTED_SIZE);
            this.toaster.fixHeight();
        }
    }

    @Override
    public void onHoverExit() {

        if (this.originalMaxHeight != null) {

            this.setMaxHeight(this.originalMaxHeight);
            this.originalMaxHeight = null;

            this.toaster.fixHeight();
        }
    }

    @Override
    public void onDestroy() {

        this.getContentProperty().unbind();

        log.trace("ToastBarBase is destroyed.");
    }
}
