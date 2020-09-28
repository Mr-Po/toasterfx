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
import org.pomo.toasterfx.transition.ToasterTransition;
import org.pomo.toasterfx.util.FXUtils;

/**
 * <h2>右 - 消息者 - 退出动画</h2>
 *
 * <p>从右退出</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:41:57</p>
 * <p>更新时间：2020-09-27 15:41:57</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class RightToasterTransitionOut extends ToasterTransition {

    /**
     * 当前透明度
     */
    private double opacity;

    /**
     * 当前X锚点
     */
    private double anchorX;

    /**
     * 需要平移距离
     */
    private double value;

    public RightToasterTransitionOut(@NonNull Duration duration) {
        this.setDuration(duration);
    }

    @Override
    public void prepare() {
        super.prepare();

        opacity = this.getPopup().getOpacity();
        anchorX = this.getPopup().getAnchorX();

        value = FXUtils.getVisualBounds().getMaxX() - anchorX;
    }

    @Override
    protected void draw(double percent) {
        this.getPopup().setOpacity(opacity * (1 - percent));
        this.getPopup().setAnchorX(anchorX + value * percent);
    }
}
