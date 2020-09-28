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

/**
 * <h2>音效</h2>
 *
 * <p>音效接口，提供播放/停止方法</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:23:48</p>
 * <p>更新时间：2020-09-23 20:23:48</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface Audio {

    /**
     * <h2>播放音效</h2>
     */
    void play();

    /**
     * <h2>停止播放</h2>
     */
    void stop();
}
