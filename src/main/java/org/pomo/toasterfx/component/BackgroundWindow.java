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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import org.pomo.toasterfx.ToasterWindow;

/**
 * <h2>默认的后台窗体</h2>
 *
 * <p>用于展示Popup，使其在主界面隐藏时，仍能正常展示消息</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:53:05</p>
 * <p>更新时间：2020-09-27 15:53:05</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class BackgroundWindow extends Window implements ToasterWindow {

    public BackgroundWindow() {

        this.setOpacity(0);

        this.setScene(new Scene(new Parent() {
        }));
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void close() {
        hide();
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
