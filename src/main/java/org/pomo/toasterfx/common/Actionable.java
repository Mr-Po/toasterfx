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

import javafx.event.Event;
import javafx.scene.Node;
import lombok.NonNull;
import org.pomo.toasterfx.model.Toast;

/**
 * <h2>可执行的组件</h2>
 *
 * <p>实现此接口的Toast、Node，当Node被点击时，可能会触发此执行。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:20:08</p>
 * <p>更新时间：2020-09-23 14:20:08</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface Actionable {

    /**
     * <h2>执行操作</h2>
     *
     * <p>一般来讲就是点击</p>
     *
     * @param event 事件
     * @param toast 消息体
     * @param node  Node
     * @return true：关闭此Toaster；false：什么都不做
     */
    boolean onAction(@NonNull Event event, @NonNull Toast toast, Node node);

    /**
     * <h2>是否存在执行操作</h2>
     *
     * <p>用来调节hover时，是否切换鼠标图标</p>
     *
     * @return 是否存在
     */
    boolean hasAction();
}
