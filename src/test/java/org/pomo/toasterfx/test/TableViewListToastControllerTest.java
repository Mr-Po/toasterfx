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
package org.pomo.toasterfx.test;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.pomo.toasterfx.ToastBarToasterService;
import org.pomo.toasterfx.controller.TableViewListToastController;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.ToastState;
import org.pomo.toasterfx.model.impl.ListToast;
import org.pomo.toasterfx.model.impl.SingleToast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.pomo.toasterfx.util.FXMessages;
import org.pomo.toasterfx.util.FXUtils;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class TableViewListToastControllerTest extends ApplicationTest {

    private final SimpleObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault());
    private ToastBarToasterService service;
    private Parent root;
    private TableViewListToastController controller;

    @Override
    public void start(Stage stage) {

        Map.Entry<Parent, TableViewListToastController> entry
                = FXUtils.load(TableViewListToastControllerTest.class
                .getResource("/org/pomo/toasterfx/fxml/SimpleListToastStage.fxml"));

        this.root = entry.getKey();
        this.root.getStylesheets().addAll(
                TableViewListToastController.DEFAULT_THEME_STYLESHEETS,
                ToastTypes.DEFAULT_STYLE_SHEETS
        );
        Scene scene = new Scene(this.root);
        stage.setScene(scene);

        this.service = new ToastBarToasterService(new FXMessages(this.localeProperty));
        this.service.initialize();

        ListToast listToast = new ListToast(ToastParameter.builder().build(), service.getToastHelper(), it -> null);

        for (int i = 0; i < 20; i++) {
            SingleToast toast = service.born("a" + i, "b" + i, ToastTypes.FAIL);
            toast.setToastState(ToastState.ARCHIVE);
            listToast.getToasts().add(toast);
        }

        controller = entry.getValue();
        controller.setService(service);
        controller.setMultiToast(listToast);
        controller.init();

        stage.show();

        controller.onDock();
    }

    @Override
    public void stop() {

        controller.destroy();
        this.service.destroy();
    }

    /**
     * 测试选中删除
     */
    @Test
    public void t01() {

        Node all = lookup(".check-box").query();
        clickOn(all);

        // 选中
        long count = lookup(".check-box").queryAll().stream()
                .filter(it -> ((CheckBox) it).isSelected()).count();

        // 全部
        int size = lookup(".check-box").queryAll().size();

        Assert.assertEquals(size, count);

        // 取消选中的前三个
        lookup(".check-box").queryAll().stream().skip(1).limit(3).forEach(this::clickOn);

        count = lookup(".check-box").queryAll().stream()
                .filter(it -> ((CheckBox) it).isSelected()).count();

        Assert.assertEquals(size - 3 - 1, count);

        clickOn("#btnDeleteSelect");

        count = lookup(".check-box").queryAll().stream()
                .filter(it -> ((CheckBox) it).isSelected()).count();
        Assert.assertEquals(0, count);

        size = lookup(".check-box").queryAll().size();
        Assert.assertEquals(4, size);

        Label labTotal = lookup("#labTotal").query();
        Assert.assertTrue(labTotal.getText().contains("3"));
    }

    /**
     * 测试清空
     */
    @Test
    public void t02() {

        clickOn("#btnClear");

        Label labTotal = lookup("#labTotal").query();
        Assert.assertTrue(labTotal.getText().contains("0"));
    }

    /**
     * 展示
     */
    @Test
    public void t03() {

        clickOn(".operate");

        Label labTotal = lookup("#labTotal").query();
        Assert.assertTrue(labTotal.getText().contains("19"));
    }

    /**
     * 切换主题和地区
     */
    @Test
    public void t04() throws TimeoutException {

        FxToolkit.setupFixture(() -> {

            this.root.getStylesheets().add(TableViewListToastController.DARK_THEME_STYLESHEETS);

            localeProperty.set(Locale.CHINESE);
            localeProperty.set(Locale.ENGLISH);
        });

        t03();
    }
}