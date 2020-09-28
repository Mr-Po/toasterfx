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
package org.pomo.toasterfx.model.impl;

import javafx.scene.media.AudioClip;
import lombok.RequiredArgsConstructor;
import org.pomo.toasterfx.model.Audio;

import java.util.Random;

/**
 * <h2>随机音效</h2>
 *
 * <p>传入AudioClip数组，随机对AudioClip数组进行播放</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:13:07</p>
 * <p>更新时间：2020-09-23 17:13:07</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@RequiredArgsConstructor
public class RandomAudio implements Audio {

    private final AudioClip[] audios;

    private final Random random = new Random();

    @Override
    public void play() {
        this.audios[this.random.nextInt(this.audios.length)].play();
    }

    @Override
    public void stop() {
        for (AudioClip audioClip : this.audios) audioClip.stop();
    }
}
