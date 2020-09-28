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
package org.pomo.toasterfx.test.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.pomo.toasterfx.ToastBarToasterService;
import org.pomo.toasterfx.model.ToastParameter;

import java.util.List;

public class ToasterFXAppTest extends Application {

    @Override
    public void start(Stage primaryStage) {

        ToastBarToasterService toasterService = new ToastBarToasterService();
        toasterService.initialize();

        Button button = new Button("无进度");
        button.setOnAction(it -> toasterService.info("ToasterFX", "Hello ToasterFX !"));

        Button button2 = new Button("有进度");
        ToastParameter parameter = ToastParameter.builder().timeout(Duration.seconds(3)).build();
        button2.setOnAction(it -> toasterService.info("ToasterFX", "Hello ToasterFX !", parameter));

        Button button3 = new Button("退出");
        button3.setOnAction(it -> {
            toasterService.destroy();
            Platform.exit();
        });

        RadioButton rabDefault = new RadioButton("默认");
        rabDefault.setSelected(true);
        RadioButton rabDark = new RadioButton("暗黑");

        ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(rabDefault, rabDark);
        ObservableList<String> stylesheets = toasterService.getToasterFactory().getStylesheets();
        List<String> darkThemeStylesheets = toasterService.getDarkThemeStylesheets();
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == rabDefault) {
                stylesheets.removeAll(darkThemeStylesheets);
            } else {
                stylesheets.addAll(darkThemeStylesheets);
            }
        });

        HBox hBox = new HBox(rabDefault, rabDark);
        hBox.setSpacing(20);

        VBox root = new VBox(hBox, button, button2, button3);
        root.setSpacing(10);
        root.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(root));

        primaryStage.show();
    }
}