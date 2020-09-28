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
package org.pomo.toasterfx.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Screen;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h2>FX 工具</h2>
 *
 * <p>提供了一些JFX相关的快捷工具</p>
 * <br/>
 *
 * <p>创建时间：2020年9月27日 15:50:32</p>
 * <p>更新时间：2020年9月27日 15:50:32</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
@UtilityClass
public class FXUtils {

    /**
     * <h2>聪明的稍后执行</h2>
     * <p>当前为UI线程时，直接执行</p>
     * <p>不为UI线程时，阻塞当前线程，并等待UI线程执行r</p>
     * <p>不宜频繁调用</p>
     *
     * @param r 待执行run
     */
    public void smartLater(Runnable r) {

        if (Platform.isFxApplicationThread()) {// 当前是ui线程

            r.run();

        } else {// 非ui线程

            CountDownLatch latch = new CountDownLatch(1);

            Thread currentThread = Thread.currentThread();
            AtomicReference<Throwable> reference = new AtomicReference<>();

            Platform.runLater(() -> {

                try {

                    r.run();
                    latch.countDown();

                } catch (Throwable e) {

                    reference.set(e);

                    log.error("r execute fail.", e);

                    currentThread.interrupt();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(reference.getAndSet(null));
            }
        }
    }

    /**
     * <h2>检查当前线程是否为ui线程</h2>
     */
    public void checkFxUserThread() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Not on FX application thread; currentThread = "
                    + Thread.currentThread().getName());
        }
    }

    /**
     * <h2>得到当前可见边界（排除任务栏）</h2>
     * <p>不可作为常量，因为任务栏可能会变</p>
     *
     * @return 2d矩形
     */
    public Rectangle2D getVisualBounds() {
        return Screen.getPrimary().getVisualBounds();
    }

    /**
     * <h2>加载fxml</h2>
     *
     * @param location 路径
     * @param <T>      fxml根节点
     * @param <U>      控制器
     * @return Entry<root, controller>
     */
    @SneakyThrows
    public <T extends Node, U> Map.Entry<T, U> load(@NonNull URL location) {

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        fxmlLoader.load();

        T root = fxmlLoader.getRoot();
        U controller = fxmlLoader.getController();

        return new AbstractMap.SimpleEntry<>(root, controller);
    }

    /**
     * <h2>加载fxml</h2>
     *
     * @param location 路径
     * @param consumer 处理
     * @param <T>      fxml根节点
     * @param <U>      控制器
     * @return Entry<root, controller>
     */
    public <T extends Node, U> T load(@NonNull URL location, BiConsumer<T, U> consumer) {

        Map.Entry<T, U> entry = load(location);

        if (consumer != null) consumer.accept(entry.getKey(), entry.getValue());

        return entry.getKey();
    }

    /**
     * <h2>调用</h2>
     *
     * @param object   对象
     * @param clazz    期待类型
     * @param function 为期待类型时的回调
     * @param <T>      返回值
     * @param <P>      期待类型
     * @return 返回值
     */
    @SuppressWarnings("unchecked")
    public <T, P> Optional<T> call(Object object, @NonNull Class<P> clazz, @NonNull Function<P, T> function) {

        T t = null;

        if (clazz.isInstance(object))
            t = function.apply(((P) object));

        return Optional.ofNullable(t);
    }

    /**
     * <h2>调用</h2>
     *
     * @param object 对象
     * @param clazz  期待类型
     * @param <P>    期待类型
     */
    public <P> void run(Object object, @NonNull Class<P> clazz, @NonNull Consumer<P> consumer) {

        call(object, clazz, it -> {
            consumer.accept(it);
            return null;
        });
    }
}
