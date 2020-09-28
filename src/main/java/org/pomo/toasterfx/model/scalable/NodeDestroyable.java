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

import javafx.scene.Node;
import lombok.NonNull;

/**
 * <h2>Node - 可销毁的</h2>
 *
 * <p>实现此接口的{@code Toast}，允许在Node被销毁时，触发回调</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 19:15:06</p>
 * <p>更新时间：2020-09-23 19:15:06</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.model.Toast
 */
@FunctionalInterface
public interface NodeDestroyable {

    /**
     * <h2>当Node被销毁时调用</h2>
     * <p>当Node尚未生成时，Toast被销毁，不会触发此销毁</p>
     * <p>当弱/软引用中的Node被GC回收时，不会触发此回调，而是：{@code NodeRecyclable}</p>
     *
     * @param node Node，可能为null
     * @see org.pomo.toasterfx.model.scalable.NodeRecyclable
     */
    void onNodeDestroy(@NonNull Node node);
}
