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
import javafx.stage.Window;
import org.pomo.toasterfx.ToasterWindow;

/**
 * <h2>后台窗体</h2>
 *
 * <p>支持异步实例化，不支持断言模式</p>
 * <p>当show()被调用时，不会获得焦点</p>
 * <br/>
 *
 * <p>创建时间：2020-10-04 14:01:08</p>
 * <p>更新时间：2020-10-04 14:01:08</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class BackgroundWindow extends Window implements ToasterWindow {

    public BackgroundWindow() {

        // 全透明
        this.setOpacity(0);

        this.setScene(new EmptyScene());
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
