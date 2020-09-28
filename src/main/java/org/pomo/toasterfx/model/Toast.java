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
package org.pomo.toasterfx.model;

import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * <h2>消息体</h2>
 *
 * <p>消息体接口，负责提供消息的基本属性</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:51:12</p>
 * <p>更新时间：2020-09-23 20:51:12</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.model.impl.SingleToast
 * @see org.pomo.toasterfx.model.impl.ListToast
 */
public interface Toast {

    /**
     * <h2>得到 创建时间</h2>
     *
     * @return UTC
     */
    long getCreateTime();

    /**
     * <h2>得到 消息参数</h2>
     *
     * @return 数据
     */
    ToastParameter getParameter();

    /**
     * <h2>得到 消息类型</h2>
     *
     * @return 消息类型
     */
    ToastType getType();

    /**
     * <h2>关闭此消息</h2>
     * <p>当点击ToastBox中的关闭按钮时，不会触发此方法</p>
     */
    void close();

    /**
     * <h2>得到 只读的消息状态Property</h2>
     *
     * @return 只读的消息状态Property
     */
    ReadOnlyObjectProperty<ToastState> getStateProperty();

    /**
     * <h2>得到 消息状态</h2>
     *
     * @return 消息状态
     */
    ToastState getState();

    /**
     * <h2>是否存在持续时间</h2>
     * <p>默认使用消息属性的超时作为依据</p>
     *
     * @return 是/否
     */
    default boolean hasDuration() {
        return !this.getParameter().getTimeout().isIndefinite();
    }
}