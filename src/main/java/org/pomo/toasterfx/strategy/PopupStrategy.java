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
package org.pomo.toasterfx.strategy;

import lombok.NonNull;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.common.Destroyable;

import java.util.List;

/**
 * <h2>消息者 弹出策略</h2>
 *
 * <p>此实例与ToasterFactory伴生</p>
 * <p> 负责弹窗进入位置的确定（不含动画），与弹窗联动效果（含动画）</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:37:48</p>
 * <p>更新时间：2020-09-27 15:37:48</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface PopupStrategy extends Destroyable {

    /**
     * <h2>锚定</h2>
     * <p>设定弹窗的初始位置</p>
     *
     * @param toaster 消息者
     */
    void anchor(@NonNull Toaster toaster);

    /**
     * <h2>调节当前展示中的消息者</h2>
     * <p>即：当存在多个Toaster时，划定Toaster间的位置关系</p>
     *
     * @param toasters 消息者列表
     */
    void adjust(@NonNull List<Toaster> toasters);
}
