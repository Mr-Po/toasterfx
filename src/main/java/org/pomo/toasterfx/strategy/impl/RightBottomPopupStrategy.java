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

import javafx.geometry.Rectangle2D;
import javafx.util.Duration;
import lombok.NonNull;
import org.pomo.toasterfx.MultiToastFactory;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.model.ToastParameter;

import java.util.List;

/**
 * <h2>右下弹出 - 策略</h2>
 *
 * <p>从右下角弹出弹窗</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:36:40</p>
 * <p>更新时间：2020-09-27 15:36:40</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class RightBottomPopupStrategy extends AbstractVerticalPopupStrategy {

    public RightBottomPopupStrategy(@NonNull MultiToastFactory multiToastFactory, @NonNull Duration duration) {
        super(multiToastFactory, duration);
    }

    @Override
    protected double getAnchorX(@NonNull Rectangle2D visualBounds,
                                @NonNull Toaster toaster, @NonNull ToastParameter parameter) {
        return visualBounds.getMaxX() - toaster.getWidth() - parameter.getFixX();
    }

    @Override
    protected double getAnchorY(@NonNull Rectangle2D visualBounds,
                                @NonNull Toaster toaster, @NonNull ToastParameter parameter) {
        return visualBounds.getMaxY() - toaster.getHeight() - parameter.getFixY();
    }

    @Override
    protected double getNewAnchorY(@NonNull Rectangle2D visualBounds,
                                   @NonNull List<Toaster> toasters,
                                   @NonNull Toaster toaster, int index,
                                   @NonNull ToastParameter parameter) {
        return visualBounds.getMaxY() - (parameter.getFixY()
                + toasters.stream().skip(index).mapToDouble(Toaster::getHeight).sum());
    }
}
