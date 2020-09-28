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

import lombok.RequiredArgsConstructor;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.transition.ToasterTransitionStay;

/**
 * <h2>消息者 进度动画</h2>
 *
 * <p>控制透明度 和 进度</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:43:12</p>
 * <p>更新时间：2020-09-27 15:43:12</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@RequiredArgsConstructor
public class ToasterTransitionProgress extends ToasterTransitionStay {

    /**
     * 不透明度
     * 离开时的不透明度
     */
    private final double opacity;

    /**
     * 起始
     */
    private double start;

    /**
     * 待加值
     * 此值可能为负
     */
    private double value;

    @Override
    public void prepare() {
        this.setDuration(this.getToaster().getToast().getParameter().getTimeout());
        super.prepare();

        this.getToastBox().setProgress(1);
        this.start = 1;
        this.value = -1;
    }

    /**
     * <h2>递增</h2>
     */
    private void increasing() {

        this.stop();
        this.start = this.getToastBox().getProgress();
        this.value = 1 - this.start;
        this.setCycleDuration(this.getDuration().multiply(this.value));
        this.play();
    }

    /**
     * <h2>递减</h2>
     */
    private void decrement() {

        this.stop();
        this.start = this.getToastBox().getProgress();
        this.value = 0 - this.start;
        this.setCycleDuration(this.getDuration().multiply(this.start));
        this.play();
    }

    @Override
    protected void draw(double percent) {
        this.getToastBox().setProgress(this.start + (this.value * percent));
    }

    @Override
    public void setOnEnd(Runnable runnable) {
        this.setOnFinished(it -> {
            if (this.getToastBox().getProgress() == 0) runnable.run();
        });
    }


    @Override
    public void onHoverEnter() {

        Toaster toaster = this.getToaster();

        toaster.getPopup().setOpacity(1);

        if (toaster.getToast().hasDuration()) this.increasing();
    }

    @Override
    public void onHoverExit() {

        Toaster toaster = this.getToaster();

        toaster.getPopup().setOpacity(this.opacity);

        if (toaster.getToast().hasDuration()) this.decrement();
    }
}
