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

/**
 * <h2>三元Function</h2>
 *
 * <p>支持三个入参的Function</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:47:31</p>
 * <p>更新时间：2020-09-23 14:47:31</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@FunctionalInterface
public interface TeFunction<T, P, U, R> {

    /**
     * <h2>执行</h2>
     *
     * @param t 此方法的参数
     * @param p 此方法的参数
     * @param u 此方法的参数
     * @return 此方法的结果
     */
    R exec(T t, P p, U u);
}
