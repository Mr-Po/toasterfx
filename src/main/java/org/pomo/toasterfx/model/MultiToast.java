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

import javafx.collections.ObservableList;
import org.pomo.toasterfx.model.impl.ToastTypes;

/**
 * <h2>多消息体</h2>
 *
 * <p>可包含多个Toast</p>
 * <p>其生命周期由{@code MultiToastFactory}维护</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:45:25</p>
 * <p>更新时间：2020-09-23 20:45:25</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface MultiToast extends Toast {

    /**
     * <h2>得到消息类型</h2>
     * <p>默认{@code ToastTypes.LIST}</p>
     *
     * @return 消息类型
     * @see ToastTypes
     */
    default ToastType getType() {
        return ToastTypes.LIST;
    }

    /**
     * <h2>得到一个可观察消息体集合</h2>
     *
     * @return 可观察消息体集合
     */
    ObservableList<Toast> getToasts();
}
