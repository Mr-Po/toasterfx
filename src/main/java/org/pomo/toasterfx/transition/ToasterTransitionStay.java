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
package org.pomo.toasterfx.transition;

import org.pomo.toasterfx.control.ToasterHoverListener;

/**
 * 名称：Toaster 停留时动画
 * <p>
 * 描述：
 *
 * @author Mr.Po
 * @version 1.0
 * @date 2020/2/23
 */
/**
 * <h2>消息者 停留动画</h2>
 *
 * <p>抽象的消息者停留动画</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:44:42</p>
 * <p>更新时间：2020-09-27 15:44:42</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public abstract class ToasterTransitionStay extends ToasterTransition implements ToasterHoverListener {

    /**
     * <h2>停留结束</h2>
     *
     * @param runnable 结束时回调
     */
    public abstract void setOnEnd(Runnable runnable);
}
