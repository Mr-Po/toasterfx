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

import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.pomo.toasterfx.ToastBarToasterService;
import org.pomo.toasterfx.ToasterFactory;
import org.pomo.toasterfx.ToasterWindow;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.impl.SingleToast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.testfx.api.FxToolkit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ToasterFXTest {

    @Test
    public void test() throws TimeoutException, InterruptedException {

        Stage stage = FxToolkit.registerPrimaryStage();

        ToasterWindow window = new ToasterWindow() {

            {
                stage.setOpacity(0);
                FxToolkit.setupScene(() -> new Scene(new Parent() {
                }));
            }

            @Override
            @SneakyThrows
            public void show() {
                FxToolkit.showStage();
            }

            @Override
            @SneakyThrows
            public void close() {
                FxToolkit.hideStage();
            }

            @Override
            public boolean isShowing() {
                return stage.isShowing();
            }

            @Override
            public ObservableList<String> getStylesheets() {
                return stage.getScene().getStylesheets();
            }

            @Override
            public Window getWindow() {
                return stage;
            }
        };

        ToasterFactory toasterFactory = new ToasterFactory();
        toasterFactory.setWindow(window);

        ToastBarToasterService toasterService = new ToastBarToasterService();
        toasterService.setToasterFactory(toasterFactory);
        toasterService.initialize();

        ToastParameter parameter = ToastParameter.builder().timeout(Duration.seconds(3)).build();
        SingleToast toast = toasterService.born("ToasterFX", "Hello ToasterFX !", parameter, ToastTypes.INFO);

        CountDownLatch countDownLatch = new CountDownLatch(2);

        toast.setOnDock((it, node) -> {

            int visualSize = toasterFactory.getVisualSize();
            Assert.assertEquals("visual toaster size not match.", 1, visualSize);

            countDownLatch.countDown();

        }).setOnDestroy(it -> countDownLatch.countDown());

        boolean flag = toasterService.push(toast);
        Assert.assertTrue("toast push fail.", flag);

        flag = countDownLatch.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("wait timeout.", flag);

        System.out.println("ToasterFXTest success !");
    }
}
