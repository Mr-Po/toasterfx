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
package org.pomo.toasterfx.transition.impl;

import javafx.util.Duration;
import lombok.NonNull;


/**
 * <h2>右下 - 消息者 - 进入动画</h2>
 *
 * <p>从右下进入</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:40:23</p>
 * <p>更新时间：2020-09-27 15:40:23</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class RightBottomToasterTransitionIn extends AbstractTranslateScaleToasterTransitionIn {

    public RightBottomToasterTransitionIn(@NonNull Duration duration, double opacity) {
        super(duration, opacity);
    }

    @Override
    protected double getTranslateX() {
        return this.getPopup().getWidth() / 2;
    }

    @Override
    protected double getTranslateY() {
        return this.getPopup().getHeight() / 2;
    }
}
