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
package org.pomo.toasterfx.model.impl;


import lombok.Getter;
import org.pomo.toasterfx.model.ToastType;

/**
 * <h2>消息类型 - 枚举</h2>
 *
 * <p>提供svg图标绘制</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:25:41</p>
 * <p>更新时间：2020-09-23 17:25:41</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public enum ToastTypes implements ToastType {

    /**
     * 成功
     */
    SUCCESS,
    /**
     * 失败
     */
    FAIL,
    /**
     * 信息
     */
    INFO,
    /**
     * 警告
     */
    WARN,
    /**
     * 列表
     */
    LIST;

    @Getter
    private final String[] styleClass;

    @Getter
    private final String name;

    ToastTypes() {
        this.name = ToastTypes.class.getName() + "." + this.name();
        this.styleClass = new String[]{"toast-type-" + this.name().toLowerCase(), "svg"};
    }

    @Override
    public int getOrder() {
        return this.ordinal();
    }

    /**
     * 默认的样式表
     */
    public final static String DEFAULT_STYLE_SHEETS = ToastTypes.class.getResource(ToastTypes.class.getSimpleName() + ".css").toExternalForm();
}
