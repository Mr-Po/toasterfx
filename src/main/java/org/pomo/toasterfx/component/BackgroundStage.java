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

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.pomo.toasterfx.ToasterWindow;

/**
 * <h2>后台舞台</h2>
 *
 * <p>需要同步实例化，支持断言模式</p>
 * <br/>
 *
 * <p>创建时间：2020-10-04 14:01:08</p>
 * <p>更新时间：2020-10-04 14:01:08</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class BackgroundStage extends Stage implements ToasterWindow {

    public BackgroundStage() {

        this.setOpacity(0);
        this.setScene(new EmptyScene());
    }

    @Override
    public ObservableList<String> getStylesheets() {
        return this.getScene().getStylesheets();
    }

    @Override
    public Window getWindow() {
        return this;
    }
}
