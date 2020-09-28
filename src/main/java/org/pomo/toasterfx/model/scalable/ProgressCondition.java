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
 * <h2>进度条件</h2>
 *
 * <p>实现此接口的{@code Toast}，可以调整ToastBox是否展示进度</p>
 * <p>主要针对，有超时，但不需要默认进度条</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:17:39</p>
 * <p>更新时间：2020-09-23 20:17:39</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.model.Toast
 * @see org.pomo.toasterfx.control.ToastBox
 */
@FunctionalInterface
public interface ProgressCondition {

    /**
     * <h2>是否显示进度</h2>
     * <p>返回null时，交给调用者决定</p>
     *
     * @return 是/否
     */
    Boolean isProgress();
}
