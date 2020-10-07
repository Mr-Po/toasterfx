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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Throwables;
import org.junit.Assert;
import org.junit.Test;
import org.pomo.toasterfx.ToastBarToasterService;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.ToasterFactory;
import org.pomo.toasterfx.model.*;
import org.pomo.toasterfx.model.impl.RandomAudio;
import org.pomo.toasterfx.model.impl.SingleAudio;
import org.pomo.toasterfx.model.impl.SingleToast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.pomo.toasterfx.model.scalable.NodeCreateable;
import org.pomo.toasterfx.strategy.impl.RightTopPopupStrategy;
import org.pomo.toasterfx.util.FXMessages;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ToasterFXTest extends ApplicationTest {

    /**
     * 1秒
     */
    private final Duration oneSecond = Duration.seconds(1);

    /**
     * 1秒关闭 - 消息体属性
     */
    private final ToastParameter parameter = ToastParameter.builder().timeout(oneSecond).build();

    /**
     * 等待超时
     */
    private final long timeout = 20 * 1000;

    private ToastBarToasterService service;
    private SimpleObjectProperty<Locale> localeProperty;

    @Override
    public void start(Stage stage) {

        WaitForAsyncUtils.autoCheckException = false;
        WaitForAsyncUtils.printException = false;

        this.localeProperty = new SimpleObjectProperty<>(Locale.getDefault());

        FXMessages messages = new FXMessages();
        messages.setLocaleProperty(this.localeProperty);

        this.service = new ToastBarToasterService(messages);
        this.service.initialize();
    }

    @Override
    public void stop() {

        this.service.destroy();
        this.handleException();

        WaitForAsyncUtils.autoCheckException = true;
        WaitForAsyncUtils.printException = true;
    }

    /**
     * <h2>测试生命周期</h2>
     */
    @Test
    public void t01() {

        SingleToast toast = service
                .born("ToasterFX", "Hello ToasterFX !", parameter, ToastTypes.INFO);

        CountDownLatch latch = new CountDownLatch(4);

        toast.setOnDock((it, node) -> {

            int visualSize = service.getToasterFactory().getVisualSize();
            Assert.assertEquals("visual toaster size not match.", 1, visualSize);

            latch.countDown();
        }).setOnUnDock((it, node) -> latch.countDown())
                .setOnClose((event, it, node) -> {
                    Assert.fail("onClose");
                    return false;
                })
                .setOnNodeDestroy((it, node) -> latch.countDown())
                .setOnDestroy(it -> latch.countDown());

        boolean flag = service.push(toast);
        Assert.assertTrue("toast push fail.", flag);

        wait(latch);

        log.info("ToasterFX life cycle test success.");
    }

    /**
     * <h2>测试批量消息</h2>
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Test(timeout = timeout)
    public void t02() {

        int num = 100;

        List<SingleToast> toasts = IntStream.range(0, num)
                .mapToObj(it ->
                        service.born(null, "batch toast.", ToastTypes.INFO))
                .peek(it -> it.setOnArchive((toast, node) -> ReferenceType.WEAK))
                .collect(Collectors.toList());

        boolean flag = service.push(toasts);
        Assert.assertTrue("toasts push fail.", flag);

        while (!service.getToastHelper().isEmpty())
            TimeUnit.SECONDS.sleep(1);

        flag = service.getMultiToastFactory().isShown();
        Assert.assertTrue("multi toast not shown.", flag);

        toasts = IntStream.range(0, num)
                .mapToObj(it ->
                        service.born(null, "batch toast.", ToastTypes.INFO))
                .peek(it -> it.setOnArchive((toast, node) -> ReferenceType.SOFT))
                .collect(Collectors.toList());

        flag = service.push(toasts);
        Assert.assertTrue("toasts push fail.", flag);

        while (!service.getToastHelper().isEmpty())
            TimeUnit.SECONDS.sleep(1);

        int referenceMapSize = service.getNodeHelper().getReferenceMapSize();
        Assert.assertNotEquals("reference map size < 0.", -1, referenceMapSize);

        ToasterFactory toasterFactory = service.getToasterFactory();
        Field field = ToasterFactory.class.getDeclaredField("visualToasters");
        field.setAccessible(true);
        List<Toaster> visualToasters = (List<Toaster>) field.get(toasterFactory);

        Toaster toaster = visualToasters.get(0);
        Toast multiToast = toaster.getToast();
        Assert.assertTrue("toast is not MultiToast", multiToast instanceof MultiToast);

        System.gc();

        clickOn(toaster.getPopup());

        this.moveTo(0, 0);

        log.info("ToasterFX batch toast test success.");
    }

    /**
     * <h2>测试暗色主题</h2>
     */
    @Test
    public void t03() {

        service.applyDarkTheme();

        SingleToast toast
                = service.born(null, "dark theme apply success.", parameter, ToastTypes.SUCCESS);

        CountDownLatch latch = new CountDownLatch(1);

        toast.setOnDestroy(it -> latch.countDown());

        boolean flag = service.push(toast);
        Assert.assertTrue("toast push fail.", flag);

        wait(latch);

        log.info("ToasterFX dark theme test success.");
    }

    /**
     * <h2>切换语言</h2>
     */
    @Test
    @SneakyThrows
    public void t04() {
        FxToolkit.setupFixture(() -> this.localeProperty.set(Locale.ENGLISH));
        FxToolkit.setupFixture(() -> this.localeProperty.set(Locale.CHINESE));
    }

    /**
     * <h2>测试错误Toast</h2>
     */
    @Test
    public void t05() {

        boolean flag = service.push(new Toast() {
            @Override
            public long getCreateTime() {
                return 0;
            }

            @Override
            public ToastParameter getParameter() {
                return null;
            }

            @Override
            public ToastType getType() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public ReadOnlyObjectProperty<ToastState> getStateProperty() {
                return null;
            }

            @Override
            public ToastState getState() {
                return null;
            }
        });
        Assert.assertFalse(flag);

        CountDownLatch latch = new CountDownLatch(1);
        class ToastImpl implements Toast, NodeCreateable {

            @Override
            public Node createNode(@NonNull Toast toast) {
                latch.countDown();
                return null;
            }

            @Override
            public long getCreateTime() {
                return 0;
            }

            @Override
            public ToastParameter getParameter() {
                return null;
            }

            @Override
            public ToastType getType() {
                return null;
            }

            @Override
            public void close() {

            }

            @Override
            public ReadOnlyObjectProperty<ToastState> getStateProperty() {
                return null;
            }

            @Override
            public ToastState getState() {
                return ToastState.ABLE_SHOW;
            }
        }
        service.push(new ToastImpl());

        wait(latch);
    }

    /**
     * <h2>测试右上弹出</h2>
     */
    @Test
    public void t06() {

        service.getToasterFactory().setPopupStrategy(new RightTopPopupStrategy(service.getMultiToastFactory(), Duration.seconds(0.7)));

        this.t01();
    }

    /**
     * <h2>测试音频</h2>
     */
    @Test
    public void t07() {

        AudioClip audioClip = new AudioClip(ToasterFXTest.class.getResource("/custom.mp3").toExternalForm());

        RandomAudio randomAudio = new RandomAudio(audioClip);

        CountDownLatch latch = new CountDownLatch(2);
        ToastParameter parameter = ToastParameter.builder().timeout(oneSecond).audio(randomAudio).build();
        SingleToast toast = service.born(null, "a", parameter, ToastTypes.WARN);
        toast.setOnDestroy(it -> {latch.countDown();
            System.out.println("ok..........1");});
        service.push(toast);

        SingleAudio singleAudio = new SingleAudio(audioClip);
        parameter = ToastParameter.builder().timeout(oneSecond).audio(singleAudio).build();
        toast = service.born(null, "a", parameter, ToastTypes.INFO);
        toast.setOnDestroy(it -> {latch.countDown();
            System.out.println("ok..........2");});
        service.push(toast);

        System.out.println("-----------------------AAAAA");

        wait(latch);
    }

    /**
     * <h2>处理异常</h2>
     */
    private void handleException() {

        while (true) {

            try {

                WaitForAsyncUtils.checkException();

            } catch (Throwable throwable) {

                Throwable rootCause = Throwables.getRootCause(throwable);

                // 排除 无法创建音频播放器的异常
                if (!("com.sun.media.jfxmedia.MediaException".equals(rootCause.getClass().getName())
                        && "Could not create player!".equals(rootCause.getMessage()))) {

                    Assert.fail(Throwables.getRootCause(throwable).getMessage());
                }

                continue;
            }

            break;
        }
    }

    /**
     * <h2>等待栅栏执行完毕</h2>
     *
     * @param latch 栅栏
     */
    @SneakyThrows
    private void wait(CountDownLatch latch) {

        boolean flag = latch.await(timeout, TimeUnit.MILLISECONDS);
        Assert.assertTrue("wait timeout.", flag);
    }
}
