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

import javafx.event.Event;
import javafx.scene.Node;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.TeFunction;
import org.pomo.toasterfx.component.SimpleDigestCalculator;
import org.pomo.toasterfx.control.impl.ToastBar;
import org.pomo.toasterfx.control.impl.ToastBarBase;
import org.pomo.toasterfx.control.impl.ToastBoxPane;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.ToastType;
import org.pomo.toasterfx.model.impl.SingleToast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.pomo.toasterfx.util.FXMessages;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * <h2>消息条 - 消息者 - 服务</h2>
 *
 * <p>快速创建消息条</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 16:15:13</p>
 * <p>更新时间：2020-09-27 16:15:13</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor
public class ToastBarToasterService extends ToasterService {

    /**
     * 摘要 - 计算器
     */
    @Setter
    @NonNull
    private BiFunction<String, String, String> digestCalculator;

    public ToastBarToasterService(FXMessages messages) {
        super(messages);
    }

    @Override
    protected void autoFill() {
        super.autoFill();

        if (this.digestCalculator == null) this.digestCalculator = new SimpleDigestCalculator();
    }

    /**
     * <h2>生成单消息体</h2>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 效果
     * @param type      类型
     * @return 单消息体
     */
    public SingleToast born(String title, @NonNull String content,
                            ToastParameter parameter, @NonNull ToastType type) {

        // 当消息参数为null时，使用缺省的消息参数
        if (parameter == null) parameter = this.getDefaultToastParameter();

        return new SingleToast(parameter, type,
                () -> this.digestCalculator.apply(title, content),
                it -> autoToastBar(title, content)
        );
    }

    /**
     * <h2>生成单消息体</h2>
     *
     * @param title   标题
     * @param content 内容
     * @param type    类型
     * @return 单消息体
     */
    public SingleToast born(String title, @NonNull String content, @NonNull ToastType type) {
        return this.born(title, content, null, type);
    }

    /**
     * <h2>自动选择合适的ToastBar</h2>
     *
     * @param title   标题
     * @param content 内容
     * @return Node
     */
    protected Node autoToastBar(String title, @NonNull String content) {

        if (title == null) return new ToastBarBase(content);

        return new ToastBar(title, content);
    }

    /**
     * <h2>弹出消息</h2>
     * <p>将 一些必要信息组装为Toast 放入 消息队列 中</p>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 效果
     * @param type      类型
     * @param consumer  扩展处理单消息体
     * @return 是否成功
     */
    public boolean bomb(String title, @NonNull String content,
                        ToastParameter parameter, @NonNull ToastType type,
                        Consumer<SingleToast> consumer) {

        SingleToast toast = this.born(title, content, parameter, type);

        if (consumer != null) consumer.accept(toast);

        return this.push(toast);
    }

    /**
     * <h2>弹出消息</h2>
     * <p>将 一些必要信息组装为Toast 放入 消息队列 中</p>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 效果
     * @param type      类型
     * @return 是否成功
     */
    public boolean bomb(String title, @NonNull String content,
                        ToastParameter parameter, @NonNull ToastType type) {

        return this.bomb(title, content, parameter, type, null);
    }

    /**
     * <h2>弹出消息</h2>
     * <p>将 一些必要信息组装为Toast 放入 消息队列 中</p>
     *
     * @param title   标题
     * @param content 内容
     * @param type    类型
     * @return 是否成功
     */
    public boolean bomb(String title, @NonNull String content,
                        @NonNull ToastType type) {
        return this.bomb(title, content, null, type, null);
    }

    /**
     * <h2>弹出消息</h2>
     * <p>将 一些必要信息组装为Toast 放入 消息队列 中</p>
     *
     * @param title    标题
     * @param content  内容
     * @param type     类型
     * @param consumer 扩展处理单消息体
     * @return 是否成功
     */
    public boolean bomb(String title, @NonNull String content,
                        @NonNull ToastType type,
                        Consumer<SingleToast> consumer) {

        return this.bomb(title, content, null, type, consumer);
    }

    /**
     * <h2>弹出消息</h2>
     * <p>将 一些必要信息组装为Toast 放入 消息队列 中</p>
     *
     * @param title    标题
     * @param content  内容
     * @param type     类型
     * @param onAction 执行
     * @return 是否成功
     */
    public boolean bomb(String title, @NonNull String content,
                        @NonNull ToastType type, ToastParameter parameter,
                        @NonNull TeFunction<Event, SingleToast, Node, Boolean> onAction) {
        return this.bomb(title, content, parameter, type, it -> it.setOnAction(onAction));
    }

    /**
     * <h2>弹出消息</h2>
     * <p>将 一些必要信息组装为Toast 放入 消息队列 中</p>
     *
     * @param title    标题
     * @param content  内容
     * @param type     类型
     * @param onAction 执行
     * @return 是否成功
     */
    public boolean bomb(String title, @NonNull String content,
                        @NonNull ToastType type,
                        @NonNull TeFunction<Event, SingleToast, Node, Boolean> onAction) {
        return this.bomb(title, content, type, it -> it.setOnAction(onAction));
    }

    /**
     * <h2>弹出成功消息</h2>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 消息体属性
     * @return 是否成功
     */
    public boolean success(String title, @NonNull String content, ToastParameter parameter) {
        return this.bomb(title, content, parameter, ToastTypes.SUCCESS);
    }

    /**
     * <h2>弹出成功消息</h2>
     *
     * @param title   标题
     * @param content 内容
     * @return 是否成功
     */
    public boolean success(String title, @NonNull String content) {
        return this.success(title, content, null);
    }

    /**
     * <h2>弹出失败消息</h2>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 消息体属性
     * @return 是否成功
     */
    public boolean fail(String title, @NonNull String content, ToastParameter parameter) {
        return this.bomb(title, content, parameter, ToastTypes.FAIL);
    }

    /**
     * <h2>弹出失败消息</h2>
     *
     * @param title   标题
     * @param content 内容
     * @return 是否成功
     */
    public boolean fail(String title, @NonNull String content) {
        return this.fail(title, content, null);
    }

    /**
     * <h2>弹出信息消息</h2>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 消息体属性
     * @return 是否成功
     */
    public boolean info(String title, @NonNull String content, ToastParameter parameter) {
        return this.bomb(title, content, parameter, ToastTypes.INFO);
    }

    /**
     * <h2>弹出信息消息</h2>
     *
     * @param title   标题
     * @param content 内容
     * @return 是否成功
     */
    public boolean info(String title, @NonNull String content) {
        return this.info(title, content, null);
    }

    /**
     * <h2>弹出警告消息</h2>
     *
     * @param title     标题
     * @param content   内容
     * @param parameter 消息体属性
     * @return 是否成功
     */
    public boolean warn(String title, @NonNull String content, ToastParameter parameter) {
        return this.bomb(title, content, parameter, ToastTypes.WARN);
    }

    /**
     * <h2>弹出警告消息</h2>
     *
     * @param title   标题
     * @param content 内容
     * @return 是否成功
     */
    public boolean warn(String title, @NonNull String content) {
        return this.warn(title, content, null);
    }

    /**
     * <h2>得到暗黑主题 - 样式表</h2>
     *
     * @return 样式表(不可修改)
     */
    public List<String> getDarkThemeStylesheets() {
        return Arrays.asList(
                ToastBoxPane.DARK_THEME_STYLESHEETS,
                ToastBarBase.DARK_THEME_STYLESHEETS,
                ToastBar.DARK_THEME_STYLESHEETS
        );
    }

    /**
     * <h2>应用暗黑主题</h2>
     */
    public void applyDarkTheme() {
        this.getToasterFactory().getStylesheets().addAll(this.getDarkThemeStylesheets());
    }
}
