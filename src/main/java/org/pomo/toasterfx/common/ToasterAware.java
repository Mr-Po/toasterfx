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
package org.pomo.toasterfx.common;

import org.pomo.toasterfx.Toaster;

/**
 * <h2>消息者 注入</h2>
 * <p>实现此接口的实例(Node、Toast、ToasterTransition)，会被注入Toaster</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:51:34</p>
 * <p>更新时间：2020-09-23 14:51:34</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@FunctionalInterface
public interface ToasterAware {

    /**
     * <h2>设置 消息者</h2>
     *
     * @param toaster 消息者
     */
    void setToaster(Toaster toaster);
}
