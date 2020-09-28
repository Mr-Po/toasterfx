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

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.pomo.toasterfx.ToasterFactory;
import org.pomo.toasterfx.ToasterService;
import org.pomo.toasterfx.controller.TableViewListToastController;
import org.pomo.toasterfx.model.ToastState;
import org.pomo.toasterfx.model.impl.ListToast;
import org.pomo.toasterfx.model.impl.ToastTypes;
import org.pomo.toasterfx.util.FXUtils;

import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * <h2>简单 - 列表消息体 - 生成器</h2>
 * <p>提供打开SimpleListToastStage.fxml窗体</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 16:32:14</p>
 * <p>更新时间：2020-09-23 16:32:14</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public class SimpleListToastSupplier extends AbstractListToastSupplier {

    public static final String DEFAULT_THEME_STYLESHEETS
            = SimpleListToastSupplier.class.getResource("/org/pomo/toasterfx/fxml/SimpleListToastStage.css").toExternalForm();

    public static final String DARK_THEME_STYLESHEETS =
            SimpleListToastSupplier.class.getResource("/org/pomo/toasterfx/fxml/dark/SimpleListToastStage.css").toExternalForm();


    // region {成员组件}
    /**
     * 消息者 工厂
     */
    @Setter
    @NonNull
    private ToasterFactory toasterFactory;

    /**
     * ToasterFX 服务
     */
    @Setter
    @NonNull
    private ToasterService service;
    // endregion

    // region {成员属性}
    /**
     * 窗体 - 图标
     */
    @Getter
    @Setter
    @NonNull
    private Image windowIcon;

    /**
     * 窗体 - 样式
     */
    @Getter
    @NonNull
    private ObservableList<String> windowStylesheets;

    /**
     * 使用默认的样式表
     */
    @Getter
    @Setter
    private boolean useDefaultStylesheets = true;
    // endregion

    @Override
    public void initialize() {
        super.initialize();

        Objects.requireNonNull(this.toasterFactory, "toasterFactory must non-null but is null.");
        Objects.requireNonNull(this.service, "service must non-null but is null.");

        if (this.windowIcon == null)
            this.windowIcon = new Image("/org/pomo/toasterfx/image/list.png");

        this.windowStylesheets = FXCollections.observableArrayList();

        if (this.isUseDefaultStylesheets())
            this.windowStylesheets.add(DEFAULT_THEME_STYLESHEETS);

        if (this.toasterFactory.isUseDefaultToastTypeStyleSheets())
            this.windowStylesheets.add(ToastTypes.DEFAULT_STYLE_SHEETS);
    }

    /**
     * <h2>执行点击事件</h2>
     *
     * @param event 事件
     * @param toast 消息体
     * @return 是否关闭Toaster
     */
    @Override
    protected boolean onAction(@NonNull Event event, @NonNull ListToast toast, @NonNull Node node) {

        this.openMultiToastWindow(toast);

        toast.setToastState(ToastState.HIDE);

        return true;
    }

    /**
     * <h2>打开多消息窗体</h2>
     * <p>子类可重写此方法，实现打开自定义窗体</p>
     *
     * @param multiToast 多消息体
     */
    protected void openMultiToastWindow(ListToast multiToast) {

        URL url = SimpleListToastSupplier.class
                .getResource("/org/pomo/toasterfx/fxml/SimpleListToastStage.fxml");

        Map.Entry<VBox, TableViewListToastController> entry = FXUtils.load(url);

        VBox root = entry.getKey();
        TableViewListToastController controller = entry.getValue();

        controller.setToasterFactory(this.toasterFactory);
        controller.setMessages(this.getMessages());
        controller.setToastHelper(this.getToastHelper());
        controller.setService(this.service);

        controller.setMultiToast(multiToast);

        controller.init();

        Bindings.bindContent(root.getStylesheets(), this.windowStylesheets);

        Stage stage = new Stage();

        // 窗体关闭时，销毁Controller
        stage.setOnCloseRequest(event -> {

            // 移除自身，防止重复执行
            stage.setOnCloseRequest(null);

            Bindings.unbindContent(root.getStylesheets(), this.windowStylesheets);

            this.getMessages().disposeBinging(stage);
            controller.destroy();
        });


        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.getIcons().add(this.windowIcon);

        this.getMessages().bindProperty(stage, stage.titleProperty(), "toasterfx.listToastTitle");

        stage.show();

        controller.onDock();

        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());

        multiToast.setOnDestroy(it -> {

            EventHandler<WindowEvent> eventHandler = stage.getOnCloseRequest();

            // 来自非窗体关闭
            if (eventHandler != null) {
                eventHandler.handle(null);
                stage.close();
            }
        });
    }
}
