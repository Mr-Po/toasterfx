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
package org.pomo.toasterfx.control;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import lombok.NonNull;
import org.pomo.toasterfx.common.Destroyable;
import org.pomo.toasterfx.model.ToastType;

import java.util.function.BiConsumer;

/**
 * <h2>消息盒子</h2>
 * <p>用于包裹真正展示Node</p>
 * <p>Ta为Node提供关闭、进度条展示。</p>
 * <p>此实例与Toaster伴生。（共享生命周期）</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 16:56:46</p>
 * <p>更新时间：2020-09-23 16:56:46</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface ToastBox extends ToasterHoverListener, Destroyable {

    /**
     * <h2>得到 包裹内容的顶级父节点</h2>
     *
     * @return 顶级父节点
     */
    Parent getBox();

    /**
     * <h2>得到 内容节点的父容器</h2>
     *
     * @return 父容器
     */
    Node getNodeContainer();

    /**
     * <h2>得到 被包裹内容节点</h2>
     *
     * @return 子节点
     */
    Node getNode();

    /**
     * <h2>设置 被包裹内容节点</h2>
     *
     * @param node 内容节点
     */
    void setNode(@NonNull Node node);

    /**
     * <h2>设置 消息类型</h2>
     *
     * @param toastType 消息类型
     */
    void setToastType(@NonNull ToastType toastType);

    /**
     * <h2>重置</h2>
     */
    void reset();

    /**
     * <h2>设置 关闭事件</h2>
     * <p>此处参数不可用：ActionEvent，因为可能不是一个按钮。</p>
     *
     * @param onClose 关闭时回调
     */
    void setOnClose(@NonNull BiConsumer<Event, Node> onClose);

    /**
     * <h2>设置 点击事件</h2>
     *
     * @param onAction 点击时回调(可能为null)
     */
    void setOnAction(BiConsumer<Event, Node> onAction);

    /**
     * <h2>设置 进度 是否启用</h2>
     *
     * @param enable 是/否
     */
    void setProgressEnable(boolean enable);

    /**
     * <h2>设置 关闭按钮 是否启用</h2>
     *
     * @param enable 是/否
     */
    void setCloseButtonEnable(boolean enable);

    /**
     * <h2>得到 进度值</h2>
     *
     * @return 进度值
     */
    double getProgress();

    /**
     * <h2>设置进度值</h2>
     *
     * @param value 进度值
     */
    void setProgress(double value);
}
