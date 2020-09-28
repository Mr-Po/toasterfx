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
package org.pomo.toasterfx.component;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.pomo.toasterfx.ToastHelper;
import org.pomo.toasterfx.control.impl.ListToastBar;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.impl.ListToast;
import org.pomo.toasterfx.util.FXMessages;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h2>抽象的 - 列表消息体 - 生成器</h2>
 * <p>用于生成特定的：{@code ListToast}，并为其绑定action，但action的具体操作由子类实现。</p>
 * <p>提供默认的消息参数，不会自动关闭的</p>
 * <p>提供默认Node生成器，其生成：{@code ListToastBar}</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:53:03</p>
 * <p>更新时间：2020-09-23 14:53:03</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see ListToast
 * @see ListToastBar
 */
@NoArgsConstructor
public abstract class AbstractListToastSupplier implements Supplier<ListToast> {

    // region {成员组件}
    /**
     * 消息体助理
     */
    @Getter
    @Setter
    @NonNull
    private ToastHelper toastHelper;

    /**
     * 国际化信息
     */
    @Getter
    @Setter
    @NonNull
    private FXMessages messages;
    // endregion

    // region {成员属性}
    /**
     * 多消息Node - 生成器
     */
    @NonNull
    private Function<ListToast, Node> nodeSupplier;

    /**
     * 消息参数
     */
    @NonNull
    private ToastParameter parameter;
    // endregion

    /**
     * <h2>初始化方法</h2>
     *
     * <p>1、进行必要的校验</p>
     * <p>2、进行默认参数的补充</p>
     */
    public void initialize() {

        Objects.requireNonNull(this.messages, "messages must non-null but is null.");
        Objects.requireNonNull(this.toastHelper, "toastHelper must non-null but is null.");

        if (this.parameter == null) this.parameter = ToastParameter.builder().timeout(Duration.INDEFINITE).build();
        if (this.nodeSupplier == null) this.nodeSupplier = it -> new ListToastBar(this.messages, it);
    }

    /**
     * <h2>执行操作</h2>
     *
     * <p>由子类实现</p>
     *
     * @param event 事件
     * @param toast 列表消息体
     * @param node  Node
     * @return 是否关闭
     */
    protected abstract boolean onAction(@NonNull Event event, @NonNull ListToast toast, @NonNull Node node);

    @Override
    public ListToast get() {

        ListToast listToast = new ListToast(this.parameter, this.toastHelper, this.nodeSupplier);
        listToast.setOnAction(this::onAction);

        return listToast;
    }
}
