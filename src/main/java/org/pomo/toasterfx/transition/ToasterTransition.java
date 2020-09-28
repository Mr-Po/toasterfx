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

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.common.Destroyable;
import org.pomo.toasterfx.common.ToasterAware;
import org.pomo.toasterfx.control.ToastBox;

/**
 * <h2>消息者 动画</h2>
 *
 * <p>抽象的消息者动画</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:43:52</p>
 * <p>更新时间：2020-09-27 15:43:52</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public abstract class ToasterTransition extends Transition
        implements ToasterAware, Destroyable {

    /**
     * 消息者
     */
    @Setter
    @Getter(AccessLevel.PROTECTED)
    private Toaster toaster;

    /**
     * 总时长
     * 不可直接使用CycleDuration，因为ta是随时间的变化而减少的。
     */
    @Setter
    @Getter(AccessLevel.PROTECTED)
    private Duration duration;

    protected Popup getPopup() {
        return this.toaster.getPopup();
    }

    protected Node getBoxNode() {
        return this.toaster.getToastBox().getBox();
    }

    protected ToastBox getToastBox() {
        return this.toaster.getToastBox();
    }

    /**
     * 预备
     * 用于初始化某些属性值
     */
    public void prepare() {
        this.setCycleDuration(this.duration);
    }

    /**
     * 准备并开始
     */
    public void readyPlay() {
        this.prepare();
        this.play();
    }

    /**
     * 画
     *
     * @param percent 动画播放的百分比
     */
    protected abstract void draw(double percent);

    @Override
    protected final void interpolate(double frac) {

        // 偶有停止时，此方法仍会被调用，因此判断一下状态
        if (this.getStatus() == Status.RUNNING) {
            this.draw(frac);
        }
    }

    /**
     * 销毁
     * 停止当前动画
     * 置空完成回调
     * 置空toaster
     * 置空duration
     */
    @Override
    public void onDestroy() {
        this.stop();
        this.setOnFinished(null);

        this.toaster = null;
    }
}
