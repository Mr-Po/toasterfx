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

/**
 * <h2>抽象的 - 位移&缩放 - 消息者进入动画</h2>
 *
 * <p>抽象实现了部分{@code ToasterTransition}</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:39:14</p>
 * <p>更新时间：2020-09-27 15:39:14</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see ToasterTransition
 */
public abstract class AbstractTranslateScaleToasterTransitionIn extends ToasterTransition {

    /**
     * 不透明度
     * 进入完成时，最终的不透明度
     */
    private final double opacity;

    /**
     * X轴位移值
     */
    private double translateX;

    /**
     * Y轴位移值
     */
    private double translateY;

    public AbstractTranslateScaleToasterTransitionIn(@NonNull Duration duration, double opacity) {
        this.setDuration(duration);
        this.opacity = opacity;
    }

    @Override
    public void prepare() {
        super.prepare();

        this.translateX = this.getTranslateX();
        this.translateY = this.getTranslateY();

        this.getBoxNode().setTranslateX(this.translateX);
        this.getBoxNode().setTranslateY(this.translateY);

        this.getBoxNode().setScaleX(this.getScaleX());
        this.getBoxNode().setScaleY(this.getScaleY());
    }

    @Override
    protected void draw(double percent) {

        // 逐渐不透明
        this.getPopup().setOpacity(this.opacity * percent);

        double remaining = 1 - percent;

        // 逐渐回归锚定位
        this.getBoxNode().setTranslateX(this.translateX * remaining);
        this.getBoxNode().setTranslateY(this.translateY * remaining);

        // 逐渐恢复为原始大小
        this.getBoxNode().setScaleX(percent);
        this.getBoxNode().setScaleY(percent);
    }

    /**
     * <h2>得到 X轴缩放值</h2>
     *
     * @return X轴缩放值
     */
    protected double getScaleX() {
        return 0;
    }

    /**
     * <h2>得到 Y轴缩放值</h2>
     *
     * @return Y轴缩放值
     */
    protected double getScaleY() {
        return 0;
    }

    /**
     * <h2>得到 开始的位移X</h2>
     *
     * @return X轴位移值
     */
    protected abstract double getTranslateX();

    /**
     * <h2>得到 起始的位移Y</h2>
     *
     * @return Y轴位移值
     */
    protected abstract double getTranslateY();
}
