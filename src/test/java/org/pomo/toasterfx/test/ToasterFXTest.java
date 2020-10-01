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
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.pomo.toasterfx.ToastBarToasterService;
import org.pomo.toasterfx.ToasterFactory;
import org.pomo.toasterfx.ToasterWindow;
import org.pomo.toasterfx.model.ReferenceType;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.impl.SingleToast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.pomo.toasterfx.util.FXUtils;
import org.testfx.api.FxToolkit;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <h2>ToasterFX 测试</h2>
 *
 * <p>用于打包和远端测试</p>
 * <br/>
 *
 * <p>创建时间：2020-09-29 09:28:16</p>
 * <p>更新时间：2020-09-29 09:28:16</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ToasterFXTest {

    /**
     * 三秒关闭 - 消息体属性
     */
    private static final ToastParameter PARAMETER = ToastParameter.builder().timeout(Duration.seconds(3)).build();

    /**
     * 消息者服务
     */
    private static ToastBarToasterService toasterService;

    /**
     * 等待超时
     */
    private static final int TIMEOUT = 10;

    /**
     * <h2>初始化</h2>
     */
    @BeforeClass
    @SneakyThrows
    public static void init() {

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

        toasterService = new ToastBarToasterService();
        toasterService.setToasterFactory(toasterFactory);
        toasterService.initialize();
    }

    /**
     * <h2>销毁</h2>
     */
    @AfterClass
    public static void destroy() {

        FXUtils.smartLater(() -> toasterService.destroy());

        System.out.println("ToasterFX test success !");
    }

    /**
     * <h2>生命周期测试</h2>
     */
    @Test
    public void execute01() {

        SingleToast toast = toasterService
                .born("ToasterFX", "Hello ToasterFX !", PARAMETER, ToastTypes.INFO);

        CountDownLatch latch = new CountDownLatch(4);

        toast
                .setOnDock((it, node) -> {

                    int visualSize = toasterService.getToasterFactory().getVisualSize();
                    Assert.assertEquals("visual toaster size not match.", 1, visualSize);

                    latch.countDown();
                })
                .setOnUnDock((it, node) -> latch.countDown())
                .setOnClose((event, it, node) -> {
                    Assert.fail("onClose");
                    return false;
                })
                .setOnNodeDestroy((it, node) -> latch.countDown())
                .setOnDestroy(it -> latch.countDown());

        boolean flag = toasterService.push(toast);
        Assert.assertTrue("toast push fail.", flag);

        wait(latch);
    }

    /**
     * <h2>批量消息体测试</h2>
     */
    @SneakyThrows
    @Test(timeout = TIMEOUT * 1000)
    public void execute02() {

        int num = 100;
        List<SingleToast> toasts = IntStream.range(0, num)
                .mapToObj(it ->
                        toasterService.born(null, "batch toast.", ToastTypes.INFO))
                .peek(it -> it.setOnArchive((toast, node) -> ReferenceType.WEAK))
                .collect(Collectors.toList());

        boolean flag = toasterService.push(toasts);
        Assert.assertTrue("toasts push fail.", flag);

        while (!toasterService.getToastHelper().isEmpty())
            TimeUnit.SECONDS.sleep(1);

        flag = toasterService.getMultiToastFactory().isShown();
        Assert.assertTrue("multi toast not shown.", flag);

        toasts = IntStream.range(0, num)
                .mapToObj(it ->
                        toasterService.born(null, "batch toast.", ToastTypes.INFO))
                .peek(it -> it.setOnArchive((toast, node) -> ReferenceType.WEAK))
                .collect(Collectors.toList());

        flag = toasterService.push(toasts);
        Assert.assertTrue("toasts push fail.", flag);

        while (!toasterService.getToastHelper().isEmpty())
            TimeUnit.SECONDS.sleep(1);

        int referenceMapSize = toasterService.getNodeHelper().getReferenceMapSize();

        Assert.assertNotEquals("reference map size < 0.", -1, referenceMapSize);
    }

    /**
     * <h2>黑色主题测试</h2>
     */
    @Test
    public void execute03() {

        FXUtils.smartLater(() -> toasterService.getToasterFactory().clear());

        toasterService.applyDarkTheme();

        SingleToast toast
                = toasterService.born(null, "dark theme apply success.", PARAMETER, ToastTypes.SUCCESS);

        CountDownLatch latch = new CountDownLatch(1);

        toast.setOnDestroy(it -> latch.countDown());

        boolean flag = toasterService.push(toast);
        Assert.assertTrue("toast push fail.", flag);

        wait(latch);
    }

    /**
     * <h2>等待栅栏执行完毕</h2>
     *
     * @param latch 栅栏
     */
    @SneakyThrows
    private void wait(CountDownLatch latch) {

        boolean flag = latch.await(TIMEOUT, TimeUnit.SECONDS);
        Assert.assertTrue("wait timeout.", flag);
    }
}
