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

/**
 * <h2>引用类型</h2>
 *
 * <p>此类型值，决定归档时，Node的处理方式</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:49:10</p>
 * <p>更新时间：2020-09-23 20:49:10</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.NodeHelper
 */
public enum ReferenceType {
    /**
     * 保持 - 强引用
     */
    STRONG,
    /**
     * 切换为 - 弱引用
     */
    WEAK,
    /**
     * 切换为 - 软引用
     */
    SOFT,
    /**
     * 直接销毁，不再持有引用
     */
    DESTROY;
}
