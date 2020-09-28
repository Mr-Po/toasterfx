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
package org.pomo.toasterfx.strategy.impl;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.MultiToastFactory;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.model.MultiToast;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.strategy.PopupStrategy;
import org.pomo.toasterfx.util.FXUtils;

import java.util.List;

/**
 * <h2>抽象的 - 垂直 - 弹出策略</h2>
 *
 * <p>子类可继承此类，实现明确的弹出方式</p>
 * <br/>
 *
 * <p>创建时间：2020-09-26 11:58:36</p>
 * <p>更新时间：2020-09-26 11:58:36</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see RightTopPopupStrategy
 * @see RightBottomPopupStrategy
 */
@Slf4j
public abstract class AbstractVerticalPopupStrategy implements PopupStrategy {

    /**
     * 多消息体 - 工厂
     */
    private MultiToastFactory multiToastFactory;

    /**
     * 校正队列时间
     */
    private Duration duration;

    /**
     * 并行动画
     */
    private ParallelTransition parallelTransition;

    public AbstractVerticalPopupStrategy(@NonNull MultiToastFactory multiToastFactory,
                                         @NonNull Duration duration) {
        this.multiToastFactory = multiToastFactory;
        this.duration = duration;

        this.parallelTransition = new ParallelTransition();

        // 当动画播放完毕时，清空子动画
        this.parallelTransition.setOnFinished(it -> parallelTransition.getChildren().clear());
    }

    @Override
    public void anchor(@NonNull Toaster toaster) {

        Rectangle2D visualBounds = FXUtils.getVisualBounds();

        ToastParameter parameter = toaster.getToast().getParameter();
        Popup popup = toaster.getPopup();

        popup.setAnchorX(this.getAnchorX(visualBounds, toaster, parameter));
        popup.setAnchorY(this.getAnchorY(visualBounds, toaster, parameter));
    }

    @Override
    public void adjust(@NonNull List<Toaster> toasters) {

        // 停止正在播放的动画
        this.parallelTransition.stop();

        // 清空子动画
        ObservableList<Animation> children = this.parallelTransition.getChildren();
        children.clear();

        // 得到当前可见边界
        Rectangle2D visualBounds = FXUtils.getVisualBounds();

        double minY = visualBounds.getMinY();
        double maxY = visualBounds.getMaxY();

        for (int i = 0; i < toasters.size(); i++) {

            Toaster toaster = toasters.get(i);
            ToastParameter parameter = toaster.getToast().getParameter();

            double newAnchorX = this.getNewAnchorX(visualBounds, toasters, toaster, i, parameter);
            double newAnchorY = this.getNewAnchorY(visualBounds, toasters, toaster, i, parameter);

            // 新的锚定Y值，超出边界
            if (newAnchorY < minY || newAnchorY > maxY - toaster.getHeight()) {

                // 是否为 【多消息体者】
                if (toaster.getToast() instanceof MultiToast) {// 是

                    // 存在下一个
                    if (i + 1 < toasters.size()) {

                        // 移除 并 归档
                        toasters.remove(i + 1).archive();

                        i--;
                        continue;

                    } else {// 不存在，【列表消息】自身超高

                        log.warn("Message List Height over boundary , actual : {} ; expect : {} > Y < {} .",
                                newAnchorY, minY, maxY - toaster.getHeight());

                        newAnchorY = minY;

                        minY = minY + toaster.getHeight();
                        maxY = maxY - toaster.getHeight();
                    }

                } else {// 普通消息体 超出边界

                    // 移除 并 归档（可能产生多消息者）
                    toasters.remove(i).archive();

                    // 此时必定已存在【多消息体】，再判断其是否因↑的archive而产生 && 排除hide状态
                    if (i == 0 && this.multiToastFactory.isShown()) return;

                    i--;
                    continue;
                }
            }

            Popup popup = toaster.getPopup();

            double valueX = newAnchorX - popup.getAnchorX();
            double valueY = newAnchorY - popup.getAnchorY();

            children.add(new VerticalFloatTransition(popup, valueX, valueY));
        }

        this.parallelTransition.play();
    }

    /**
     * <h2>得到 X轴坐标</h2>
     *
     * @param visualBounds 显示边界
     * @param toaster      消息者
     * @param parameter    消息体 - 属性
     * @return X轴坐标
     */
    protected abstract double getAnchorX(@NonNull Rectangle2D visualBounds,
                                         @NonNull Toaster toaster, @NonNull ToastParameter parameter);

    /**
     * <h2>得到 Y轴坐标</h2>
     *
     * @param visualBounds 显示边界
     * @param toaster      消息者
     * @param parameter    消息体 - 属性
     * @return Y轴坐标
     */
    protected abstract double getAnchorY(@NonNull Rectangle2D visualBounds,
                                         @NonNull Toaster toaster, @NonNull ToastParameter parameter);

    /**
     * <h2>得到 新的X轴坐标</h2>
     *
     * @param visualBounds 可见边界
     * @param toasters     可见消息者集合
     * @param toaster      消息者
     * @param index        下标
     * @param parameter    消息体属性
     * @return 新的X轴坐标
     */
    protected double getNewAnchorX(@NonNull Rectangle2D visualBounds,
                                   @NonNull List<Toaster> toasters,
                                   @NonNull Toaster toaster, int index,
                                   @NonNull ToastParameter parameter) {

        double anchorX = this.getAnchorX(visualBounds, toaster, parameter);

        if (anchorX < visualBounds.getMinX()) {

            anchorX = visualBounds.getMinX() + parameter.getFixX();

        } else if (anchorX + toaster.getWidth() > visualBounds.getMaxX()) {

            anchorX = visualBounds.getMaxX() - toaster.getWidth() + parameter.getFixX();
        }

        return anchorX;
    }

    /**
     * <h2>得到 新的Y轴坐标</h2>
     *
     * @param visualBounds 可见边界
     * @param toasters     可见消息者集合
     * @param toaster      消息者
     * @param index        下标
     * @param parameter    消息体属性
     * @return 新的X轴坐标
     */
    protected abstract double getNewAnchorY(@NonNull Rectangle2D visualBounds,
                                            @NonNull List<Toaster> toasters,
                                            @NonNull Toaster toaster, int index,
                                            @NonNull ToastParameter parameter);

    /**
     * <h2>垂直浮动变换器</h2>
     *
     * <p>进行垂直方向上的变换动画</p>
     * <br/>
     *
     * <p>创建时间：2020-09-27 15:13:39</p>
     * <p>更新时间：2020-09-27 15:13:39</p>
     *
     * @author Mr.Po
     * @version 1.0
     */
    class VerticalFloatTransition extends Transition {

        private final Popup popup;

        private final double anchorX;
        private final double anchorY;

        private final double valueX;
        private final double valueY;

        VerticalFloatTransition(@NonNull Popup popup, double valueX, double valueY) {
            this.popup = popup;
            this.setCycleDuration(AbstractVerticalPopupStrategy.this.duration);

            this.anchorY = this.popup.getAnchorY();
            this.anchorX = this.popup.getAnchorX();

            this.valueX = valueX;
            this.valueY = valueY;
        }

        @Override
        protected void interpolate(double frac) {
            this.popup.setAnchorX((this.anchorX + (valueX * frac)));
            this.popup.setAnchorY((this.anchorY + (valueY * frac)));
        }
    }

    @Override
    public void onDestroy() {

        this.parallelTransition.stop();
        this.parallelTransition.getChildren().clear();
        this.parallelTransition = null;

        this.duration = null;
        this.multiToastFactory = null;
    }
}
