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
package org.pomo.toasterfx;

import javafx.collections.ObservableList;
import javafx.stage.Window;

/**
 * <h2>后台窗体</h2>
 *
 * <p>后台窗体接口</p>
 * <br/>
 *
 * <p>创建时间：2020-09-28 21:22:02</p>
 * <p>更新时间：2020-09-28 21:22:02</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface ToasterWindow {

    /**
     * <h2>显示</h2>
     */
    void show();

    /**
     * <h2>关闭</h2>
     */
    void close();

    /**
     * <h2>是否处于显示中</h2>
     *
     * @return 是/否
     */
    boolean isShowing();

    /**
     * <h2>得到 可观察的样式表集合</h2>
     *
     * @return 可观察的样式表集合
     */
    ObservableList<String> getStylesheets();

    /**
     * <h2>得到 窗体</h2>
     *
     * @return 窗体
     */
    Window getWindow();
}
