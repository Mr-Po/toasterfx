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
package org.pomo.toasterfx;

import javafx.embed.swing.JFXPanel;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

public class ToastBarToasterServiceTest {

    private static ToastBarToasterService service;

    @BeforeClass
    public static void start() {

        new JFXPanel();
        service = new ToastBarToasterService();
        service.initialize();
    }

    @Test
    public void bomb() {
        service.bomb("a", "b", ToastParameter.builder().build(), ToastTypes.SUCCESS, Assert::assertNotNull);
    }

    @Test
    public void testBomb() {
        service.bomb("a", "b", ToastTypes.SUCCESS, ToastParameter.builder().build(),
                (event, singleToast, node) -> true);
    }

    @Test
    public void testBomb1() {
        service.bomb("a", "b", ToastTypes.SUCCESS,
                (event, singleToast, node) -> true);
    }

    @Test
    public void testBomb2() {
        service.bomb("a", "b", ToastTypes.SUCCESS, null,
                (event, singleToast, node) -> true);
    }

    @Test
    public void testBomb3() {
        service.bomb("a", "b", ToastTypes.SUCCESS);
    }

    @Test
    public void testBomb4() {
        service.bomb("a", "b", ToastTypes.SUCCESS, Assert::assertNotNull);
    }

    @Test
    public void success() {
        service.success("a", "b", null);
        service.success("a", "b");
    }

    @Test
    public void fail() {
        service.fail("a", "b", null);
        service.fail("a", "b");
    }

    @Test
    public void info() {
        service.info("a", "b", null);
        service.info("a", "b");
    }

    @Test
    public void warn() {
        service.warn("a", "b", null);
        service.warn("a", "b");
    }

    @Test(expected = NullPointerException.class)
    public void setDigestCalculator() {
        service.setDigestCalculator(String::concat);
        service.setDigestCalculator(null);
    }

    @Test(expected = IllegalStateException.class)
    public void initialize01() {

        ToastBarToasterService service = new ToastBarToasterService();
        service.fail(null, "a");

        Assert.fail();
    }

    @Test
    public void initialize02() {

        ToastBarToasterService service = new ToastBarToasterService();
        service.destroy();
    }

    @Test
    public void initialize03() throws TimeoutException {

        ToastBarToasterService service = new ToastBarToasterService();
        service.initialize();

        ToasterFactory toasterFactory = service.getToasterFactory();

        service.initialize();

        Assert.assertEquals(toasterFactory, service.getToasterFactory());

        FxToolkit.setupFixture(service::destroy);
    }
}