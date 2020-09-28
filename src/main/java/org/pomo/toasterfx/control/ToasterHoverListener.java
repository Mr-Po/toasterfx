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
package org.pomo.toasterfx.control;

/**
 * <h2>消息者 - Hover 监听</h2>
 * <p>实现此接口的Node、ToastBox、ToasterTransitionStay，当鼠标移入/移出Toaster中时，会触发回调</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:02:11</p>
 * <p>更新时间：2020-09-23 17:02:11</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface ToasterHoverListener {

    /**
     * <h2>hover 进入</h2>
     */
    default void onHoverEnter() {
    }

    /**
     * <h2>hover 离开</h2>
     */
    default void onHoverExit() {
    }
}
