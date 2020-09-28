package org.pomo.toasterfx.test;

import org.junit.Assert;
import org.junit.Test;
import org.pomo.toasterfx.ToastBarToasterService;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

public class ToasterFXTest {

    @Test
    public void test() throws TimeoutException {

        FxToolkit.registerPrimaryStage();

        ToastBarToasterService toasterService = new ToastBarToasterService();
        toasterService.initialize();

        boolean flag = toasterService.success("ToasterFX", "Hello ToasterFX !");
        Assert.assertTrue("toast push fail.", flag);
    }
}
