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
package org.pomo.toasterfx.component;

import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * <h2>空Scene</h2>
 *
 * <p>用于{@code BackgroundWindow}、{@code DelegateStageWindow}设置空Scene</p>
 * <br/>
 *
 * <p>创建时间：2020-10-02 09:54:37</p>
 * <p>更新时间：2020-10-02 09:54:37</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see BackgroundStage
 * @see BackgroundWindow
 * @see DelegateStageWindow
 */
class EmptyScene extends Scene {

    /**
     * <h2>空父节点</h2>
     *
     * <p>空的父容器节点</p>
     * <br/>
     *
     * <p>创建时间：2020-10-02 09:59:40</p>
     * <p>更新时间：2020-10-02 09:59:40</p>
     *
     * @author Mr.Po
     * @version 1.0
     */
    static class EmptyParent extends Parent {
    }

    EmptyScene() {
        super(new EmptyParent());
    }
}
