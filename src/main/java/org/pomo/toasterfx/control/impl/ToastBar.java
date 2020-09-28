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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>消息 条</h2>
 * <p>继承{@code ToastBarBase}，扩展支持标题</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 16:39:42</p>
 * <p>更新时间：2020-09-23 16:39:42</p>
 *
 * @see ToastBarBase
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor
public class ToastBar extends ToastBarBase {

    /**
     * 默认主题样式表
     */
    public final static String DEFAULT_THEME_STYLESHEETS =
            ToastBar.class.getResource(ToastBar.class.getSimpleName() + ".css").toExternalForm();

    /**
     * 暗黑主题 - 样式表
     */
    public final static String DARK_THEME_STYLESHEETS =
            ToastBar.class.getResource("dark/" + ToastBar.class.getSimpleName() + ".css").toExternalForm();
    /**
     * 标题标签
     */
    private final Label titleLabel;

    {

        this.getStyleClass().add("toast-bar");

        this.titleLabel = new Label();
        this.titleLabel.getStyleClass().add("title");
        this.titleLabel.setWrapText(true);// 自动换行，但不会撑大父容器的MaxHeight

        VBox titleContainer = new VBox(this.titleLabel);
        titleContainer.getStyleClass().add("title-container");

        this.getRightContainer().getChildren().add(0, titleContainer);
    }

    /**
     * @param title   标题
     * @param content 内容
     * @param graphic 图标
     */
    public ToastBar(String title, String content, Node graphic) {
        super(content, graphic);

        this.setTitle(title);
    }

    /**
     * <p>使用css进行图标绘制graphic</p>
     *
     * @param title   标题
     * @param content 内容
     */
    public ToastBar(String title, String content) {
        super(content);

        this.setTitle(title);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DEFAULT_THEME_STYLESHEETS;
    }

    /**
     * <h2>得到可观察的标题</h2>
     *
     * @return 可观察的标题
     */
    public StringProperty getTitleProperty() {
        return this.titleLabel.textProperty();
    }

    /**
     * <h2>设置标题</h2>
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    @Override
    protected boolean checkExpansion() {

        double originalHeight = this.titleLabel.getHeight();
        double targetHeight = this.titleLabel.prefHeight(this.titleLabel.getWidth());

        return targetHeight - originalHeight != 0 || super.checkExpansion();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        this.getTitleProperty().unbind();

        log.trace("ToastBar is destroyed.");
    }
}
