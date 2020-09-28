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
 * <h2>可关闭的组件</h2>
 *
 * <p>实现此接口的Toast、Node，当通过点击ToastBox中的“x”按钮，会触发此执行。</p>
 * <p>直接调用Toast或Toaster的close()，不会触发此执行。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:37:37</p>
 * <p>更新时间：2020-09-23 14:37:37</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@FunctionalInterface
public interface Closeable {

    /**
     * <h2>关闭回调</h2>
     *
     * <p>当点击ToastBox的关闭按钮时，会被触发。</p>
     * <p>若返回值为false时，关闭会被阻止，返回null时，交给上级调用者决定。</p>
     *
     * @param event 点击事件
     * @param toast 消息体
     * @param node  Node
     * @return 是否关闭，可能为null
     */
    Boolean onClose(@NonNull Event event, @NonNull Toast toast, @NonNull Node node);
}
