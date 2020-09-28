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
package org.pomo.toasterfx.model;

import javafx.util.Duration;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

/**
 * <h2>消息体 - 属性</h2>
 *
 * <p>构造器模式，允许设置音效、展示时长，x、y轴修复。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-25 10:13:04</p>
 * <p>更新时间：2020-09-25 10:13:04</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Getter
@Builder
public class ToastParameter {

    /**
     * 可通过 +/- 修正弹窗X轴的位置
     */
    @Builder.Default
    private final double fixX = 0.0;

    /**
     * 可通过 +/- 修正弹窗Y轴的位置
     */
    @Builder.Default
    private final double fixY = 0.0;

    /**
     * 弹窗保持时间
     * <p>当此值为：Duration.INDEFINITE时，不会自动隐藏</p>
     */
    @NonNull
    @Builder.Default
    private final Duration timeout = Duration.INDEFINITE;

    /**
     * 该弹窗弹出时的音效
     * <p>当此值为null时，无音效</p>
     */
    @Builder.Default
    private final Audio audio = null;

    /**
     * 得到音效
     *
     * @return 音效
     */
    public Optional<Audio> getAudio() {
        return Optional.ofNullable(this.audio);
    }
}
