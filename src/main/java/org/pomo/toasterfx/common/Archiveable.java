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

import javafx.scene.Node;
import lombok.NonNull;
import org.pomo.toasterfx.model.ReferenceType;
import org.pomo.toasterfx.model.Toast;

/**
 * <h2>可归档的组件</h2>
 *
 * <p>实现此接口的Toast、Node，当Toast被归档时，会触发此执行。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:35:47</p>
 * <p>更新时间：2020-09-23 14:35:47</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@FunctionalInterface
public interface Archiveable {

    /**
     * <h2>归档</h2>
     *
     * <p>返回null时，交给调用者决定</p>
     *
     * @param toast 消息体
     * @param node  Node，可能为null
     * @return 引用类型，可能为null
     */
    ReferenceType onArchive(@NonNull Toast toast, Node node);
}
