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

import javafx.embed.swing.JFXPanel;
import javafx.fxml.LoadException;
import javafx.scene.Node;
import org.junit.Assert;
import org.junit.Test;
import org.pomo.toasterfx.common.Destroyable;
import org.testfx.api.FxToolkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class FXUtilsTest {

    @Test(expected = InvocationTargetException.class)
    public void constructor() throws IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        Constructor<FXUtils> constructor = FXUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test(expected = RuntimeException.class)
    public void smartLater() throws TimeoutException {
        new JFXPanel();

        FxToolkit.setupFixture(() -> FXUtils.smartLater(() -> {
        }));

        FXUtils.smartLater(() -> {
            throw new UnsupportedOperationException();
        });
    }

    @Test(expected = IllegalStateException.class)
    public void checkFxUserThread() {
        FXUtils.checkFxUserThread();
    }

    @Test
    public void load01() {

        new JFXPanel();

        URL resource = FXUtilsTest.class.getResource("/org/pomo/toasterfx/fxml/SimpleListToastStage.fxml");

        Map.Entry<Node, Object> entry = FXUtils.load(resource);
        Assert.assertNotNull(entry);
        Assert.assertNotNull(entry.getKey());
        Assert.assertNotNull(entry.getValue());

        FXUtils.load(resource, null);

        FXUtils.load(resource, (node, o) -> {
            Assert.assertNotNull(node);
            Assert.assertNotNull(o);
        });
    }

    @Test(expected = LoadException.class)
    public void load02() {

        new JFXPanel();
        URL resource = FXUtilsTest.class.getResource("/org/pomo/toasterfx/fxml/SimpleListToastStage.css");
        FXUtils.load(resource);
    }

    @Test(expected = NullPointerException.class)
    public void load03() {
        FXUtils.load(null);
    }

    @Test(expected = NullPointerException.class)
    public void load04() {
        FXUtils.load(null, null);
    }

    @Test
    public void call01() {
        Optional<Boolean> call = FXUtils.call(null, Destroyable.class, destroyable -> true);
        Assert.assertFalse(call.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void call02() {
        FXUtils.call(null, null, destroyable -> true);
    }

    @Test(expected = NullPointerException.class)
    public void call03() {
        FXUtils.call(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void call04() {
        FXUtils.call(null, Destroyable.class, null);
    }

    @Test(expected = NullPointerException.class)
    public void run01() {
        FXUtils.run(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void run02() {
        FXUtils.run(null, Destroyable.class, null);
    }
}