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
package org.pomo.toasterfx;

import lombok.experimental.UtilityClass;
import org.pomo.toasterfx.component.BackgroundStage;
import org.pomo.toasterfx.component.BackgroundWindow;
import org.pomo.toasterfx.util.FXUtils;

/**
 * <h2>消息者 - 窗体 - 助理</h2>
 *
 * <p>提供{@code ToasterWindow}的一些辅助方法</p>
 * <br/>
 *
 * <p>创建时间：2020-10-04 18:29:31</p>
 * <p>更新时间：2020-10-04 18:29:31</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see ToasterWindow
 * @see BackgroundStage
 * @see BackgroundWindow
 */
@UtilityClass
public class ToasterWindowHelper {

    /**
     * <h2>创建合适的消息者窗体</h2>
     * <p>断言时，使用{@code BackgroundStage}，非断言使用{@code BackgroundWindow}</p>
     *
     * @return 消息者窗体
     */
    @SuppressWarnings({"AssertWithSideEffects", "ConstantConditions"})
    ToasterWindow create() {

        boolean isAssert = false;

        assert isAssert = true;

        ToasterWindow window;

        if (isAssert) window = FXUtils.smartGet(BackgroundStage::new);
        else window = new BackgroundWindow();

        return window;
    }
}
