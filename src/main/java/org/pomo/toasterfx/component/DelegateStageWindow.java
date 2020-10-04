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
import org.pomo.toasterfx.util.FXUtils;

/**
 * <h2>委托舞台窗体</h2>
 *
 * <p>接收一个外部的Stage，将ToasterWindow的行为全部委托给传入的Stage</p>
 * <br/>
 *
 * <p>创建时间：2020-10-02 09:46:16</p>
 * <p>更新时间：2020-10-02 09:46:16</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see ToasterWindow
 */
public class DelegateStageWindow implements ToasterWindow {

    private final Stage stage;

    public DelegateStageWindow(Stage stage) {

        this.stage = stage;

        this.stage.setOpacity(0);
        if (this.stage.getScene() == null) FXUtils.smartLater(() -> this.stage.setScene(new EmptyScene()));
    }

    @Override
    public void show() {
        this.stage.show();
    }

    @Override
    public void close() {
        this.stage.close();
    }

    @Override
    public boolean isShowing() {
        return this.stage.isShowing();
    }

    @Override
    public ObservableList<String> getStylesheets() {
        return this.stage.getScene().getStylesheets();
    }

    @Override
    public Window getWindow() {
        return this.stage;
    }
}
