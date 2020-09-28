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
package org.pomo.toasterfx.model.scalable;

/**
 * <h2>Node - 可回收的</h2>
 *
 * <p>实现此接口的{@code Toast}，在Node因弱/软引用回收时，会触发回调</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 19:17:22</p>
 * <p>更新时间：2020-09-23 19:17:22</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.model.Toast
 */
@FunctionalInterface
public interface NodeRecyclable {

    /**
     * <h2>当弱/软引用中的Node被GC回收时，会触发此回调</h2>
     */
    void onNodeRecycle();
}
