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
package org.pomo.toasterfx.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.ToastHelper;
import org.pomo.toasterfx.ToasterService;
import org.pomo.toasterfx.model.MultiToast;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastState;
import org.pomo.toasterfx.model.ToastType;
import org.pomo.toasterfx.util.FXMessages;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2>表格式 - 列表消息 - 控制器</h2>
 * <p>由FXMLLoader负责创建，并进行属性注入</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:04:56</p>
 * <p>更新时间：2020-09-23 17:04:56</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class TableViewListToastController {

    public static final String DEFAULT_THEME_STYLESHEETS
            = TableViewListToastController.class.getResource("/org/pomo/toasterfx/fxml/SimpleListToastStage.css").toExternalForm();

    public static final String DARK_THEME_STYLESHEETS =
            TableViewListToastController.class.getResource("/org/pomo/toasterfx/fxml/dark/SimpleListToastStage.css").toExternalForm();

    // region {FXML属性}
    @FXML
    private Button btnDeleteSelect;

    @FXML
    private Button btnClear;

    @FXML
    private TableView<Toast> table;

    @FXML
    private TableColumn<Toast, Boolean> colSelected;

    @FXML
    private TableColumn<Toast, ToastType> colType;

    @FXML
    private TableColumn<Toast, Long> colDate;

    @FXML
    private TableColumn<Toast, String> colDigest;

    @FXML
    private TableColumn<Toast, Void> colOperate;

    @FXML
    private CheckBox ckbAll;

    @FXML
    private Label labTotal;
    // endregion

    // region {自有属性}
    /**
     * 消息选中Map（属性扩展）
     */
    private Map<Toast, Boolean> toastSelectMap;
    private BooleanBinding itemsIsEmptyProperty;
    private MessageFormat tableTotalFormat;
    private Object[] tableTotalFormatArgs;
    private StringBinding itemsSizeBinding;
    // endregion

    // region {成员组件}

    /**
     * 国际化信息
     */
    @NonNull
    private FXMessages messages;

    /**
     * 消息体 助理
     */
    @NonNull
    private ToastHelper toastHelper;

    /**
     * ToasterFX 服务
     */
    @Setter
    @NonNull
    private ToasterService service;
    // endregion

    // region {注入属性}
    /**
     * 多消息
     */
    @Setter
    @NonNull
    private MultiToast multiToast;
    // endregion

    /**
     * <h2>初始化方法</h2>
     * <p>进行必要的属性校验</p>
     * <p>初始化表格以及其他元素</p>
     */
    public void init() {

        Objects.requireNonNull(this.multiToast, "multiToast must non-null but is null.");
        Objects.requireNonNull(this.service, "service must non-null but is null.");

        this.messages = this.service.getMessages();
        this.toastHelper = this.service.getToastHelper();

        this.initTableView();

        this.initOther();
    }

    /**
     * <h2>初始化表格</h2>
     */
    private void initTableView() {

        // region {空表格文本}
        Label placeholder = new Label();
        this.messages.bindProperty(this, placeholder.textProperty(), "toasterfx.noToast");
        this.table.setPlaceholder(placeholder);
        // endregion

        // region {复选框}

        // 可观察的行选中列表（其中可能存在已失效的）
        List<SimpleBooleanProperty> columnSelectPropertyList = new ArrayList<>();
        this.toastSelectMap = new HashMap<>();

        ckbAll.selectedProperty().addListener((observable, oldValue, newValue) -> {

            // 新值为true || 不为部分选中时
            if (newValue || !ckbAll.isIndeterminate()) {

                this.table.getItems().forEach(it -> toastSelectMap.put(it, newValue));
                columnSelectPropertyList.forEach(it -> it.set(newValue));
            }

        });

        colSelected.setCellFactory(column -> {

            // --->执行有限次数
            return new CheckBoxTableCell<>(new Callback<Integer, ObservableValue<Boolean>>() {

                private final SimpleBooleanProperty selectedProperty = new SimpleBooleanProperty();
                private Toast item;

                {
                    selectedProperty.addListener((observable, oldValue, newValue) -> {

                        // item存在，且是当前table中的一项(排除失效的cell)
                        if (item != null
                                && table.getItems().stream().anyMatch(it -> it == item)) {

                            toastSelectMap.put(item, newValue);

                            boolean allMatch = toastSelectMap.values().stream().allMatch(it -> it);

                            // fix 可见的全选了，导致全选按钮也被全选
                            if (allMatch && toastSelectMap.size() == table.getItems().size()) {

                                ckbAll.setIndeterminate(false);
                                ckbAll.setSelected(true);
                                btnDeleteSelect.setDisable(false);
                                return;
                            }

                            boolean anyMatch = toastSelectMap.values().stream().anyMatch(it -> it);
                            if (anyMatch) {

                                ckbAll.setIndeterminate(true);
                                ckbAll.setSelected(false);
                                btnDeleteSelect.setDisable(false);

                            } else {

                                ckbAll.setIndeterminate(false);
                                ckbAll.setSelected(false);
                                btnDeleteSelect.setDisable(true);
                            }
                        }
                    });

                    columnSelectPropertyList.add(selectedProperty);
                }

                @Override
                public ObservableValue<Boolean> call(Integer index) {

                    // --->执行无限次数

                    this.item = table.getItems().get(index);

                    toastSelectMap.putIfAbsent(item, false);
                    selectedProperty.set(toastSelectMap.get(item));

                    return selectedProperty;
                }
            });
        });
        // endregion

        // region {时间}
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        messages.bindProperty(this, colDate.textProperty(), "toasterfx.date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        colDate.setCellFactory(param -> new TableCell<Toast, Long>() {

            private final Tooltip tooltip;

            {
                this.getStyleClass().add("date");
                this.tooltip = new Tooltip();
            }

            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {

                    LocalDateTime localDateTime = LocalDateTime
                            .ofInstant(Instant.ofEpochMilli(item), ZoneId.systemDefault());

                    this.setText(localDateTime.format(formatter));
                    this.tooltip.setText(localDateTime.format(formatterFull));
                    this.setTooltip(this.tooltip);

                } else {

                    this.setText(null);
                    this.setGraphic(null);
                    this.tooltip.setText(null);
                    this.setTooltip(null);
                }
            }
        });
        // endregion

        // region {类型}
        messages.bindProperty(this, colType.textProperty(), "toasterfx.type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(param -> new TableCell<Toast, ToastType>() {

            private final Pane graphic = new Pane();
            private final Tooltip tooltip = new Tooltip();
            private final SimpleObjectProperty<ToastType> toastTypeProperty = new SimpleObjectProperty<>();

            {
                this.setText(null);
                this.getStyleClass().add("type");
                messages.bindProperty(colType, this.tooltip.textProperty(), () -> {

                    ToastType toastType = this.toastTypeProperty.getValue();
                    if (toastType == null) return null;

                    return messages.get(toastType.getName());

                }, this.toastTypeProperty);
            }

            @Override
            protected void updateItem(ToastType item, boolean empty) {
                super.updateItem(item, empty);

                ObservableList<String> styleClass = this.graphic.getStyleClass();
                styleClass.clear();

                this.toastTypeProperty.set(item);

                if (item == null) {

                    this.setGraphic(null);
                    this.setTooltip(null);

                } else {

                    styleClass.add("graphic");
                    styleClass.addAll(item.getStyleClass());
                    styleClass.add("small");
                    this.setGraphic(this.graphic);

                    this.setTooltip(this.tooltip);
                }
            }
        });
        // endregion

        // region {摘要}
        messages.bindProperty(this, colDigest.textProperty(), "toasterfx.digest");
        colDigest.setCellValueFactory(new PropertyValueFactory<>("digest"));
        colDigest.setCellFactory(param -> new TableCell<Toast, String>() {

            private final Tooltip tooltip;

            {
                this.setAlignment(Pos.CENTER_LEFT);
                this.getStyleClass().add("digest");
                this.tooltip = new Tooltip();
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {

                    this.setText(item);
                    this.tooltip.setText(item);
                    this.setTooltip(tooltip);

                } else {

                    this.setText(null);
                    this.tooltip.setText(null);
                    this.setTooltip(null);
                }
            }
        });
        // endregion

        // region {操作}
        messages.bindProperty(this, colOperate.textProperty(), "toasterfx.operate");
        colOperate.setCellFactory(param -> new TableCell<Toast, Void>() {

            private final Button btnShow = new Button();

            {
                // this.setAlignment(Pos.CENTER);
                this.getStyleClass().add("operate");
                this.btnShow.setOnAction(event -> show((Toast) this.getTableRow().getItem()));
                messages.bindProperty(colOperate, btnShow.textProperty(), "toasterfx.show");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty && this.getTableRow() != null) {

                    this.setGraphic(this.btnShow);

                } else {

                    this.setGraphic(null);
                }
            }
        });
        // endregion
    }

    /**
     * <h2>初始化其他</h2>
     */
    private void initOther() {

        this.messages.bindProperty(this, btnDeleteSelect.textProperty(), "toasterfx.deleteSelect");
        this.messages.bindProperty(this, btnClear.textProperty(), "toasterfx.clear");

        ObservableList<Toast> itemsProperty = multiToast.getToasts();

        this.itemsIsEmptyProperty = Bindings.createBooleanBinding(itemsProperty::isEmpty, itemsProperty);

        this.btnClear.disableProperty().bind(itemsIsEmptyProperty);
        this.colSelected.getGraphic().disableProperty().bind(itemsIsEmptyProperty);

        this.tableTotalFormat = this.messages.getFormat("toasterfx.total");
        this.tableTotalFormatArgs = new Object[1];

        this.itemsSizeBinding = Bindings.createStringBinding(() -> {
            this.tableTotalFormatArgs[0] = itemsProperty.size();
            return this.tableTotalFormat.format(this.tableTotalFormatArgs);
        }, itemsProperty);
        this.labTotal.textProperty().bind(this.itemsSizeBinding);

        // 此处不应合二为一，因为国际化会new MessageFormat，items则不会
        this.messages.addListener(this, it -> {
            this.tableTotalFormat = this.messages.getFormat("toasterfx.total");
            this.itemsSizeBinding.invalidate();
        });
    }

    /**
     * <h2>显示后回调</h2>
     */
    public void onDock() {

        this.table.setItems(this.multiToast.getToasts());
    }

    /**
     * <h2>执行清空</h2>
     */
    @FXML
    private void onClear() {

        // 销毁Toast、Node
        ObservableList<Toast> items = this.table.getItems();
        this.toastHelper.destroy(items);
        items.clear();

        this.toastSelectMap.clear();
        this.selectedClear();

        log.debug("message list was clean up.");
    }

    /**
     * <h2>执行删除选中</h2>
     */
    @FXML
    private void onDeleteSelect() {

        // 得到选中的Toast集合
        List<Toast> toasts = this.toastSelectMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 销毁Toast、Node
        this.toastHelper.destroy(toasts);

        this.table.getItems().removeAll(toasts);
        toasts.forEach(this.toastSelectMap::remove);

        this.selectedClear();

        log.debug("{} toast was delete.", toasts.size());
        toasts.clear();
    }

    /**
     * <h2>展示消息</h2>
     *
     * @param toast 消息
     */
    private void show(Toast toast) {

        this.table.getItems().remove(toast);
        this.toastSelectMap.remove(toast);

        if (this.multiToast.getToasts().isEmpty()) this.selectedClear();

        // 必须重置后，才能push
        this.toastHelper.reset(toast);

        this.service.push(toast);
    }

    /**
     * <h2>清空选中</h2>
     */
    private void selectedClear() {
        ckbAll.setIndeterminate(false);
        ckbAll.setSelected(false);
        btnDeleteSelect.setDisable(true);
    }

    /**
     * <h2>销毁</h2>
     */
    public void destroy() {

        this.btnClear.disableProperty().unbind();
        this.colSelected.getGraphic().disableProperty().unbind();
        this.itemsIsEmptyProperty.dispose();
        this.itemsIsEmptyProperty = null;

        this.labTotal.textProperty().unbind();
        this.itemsSizeBinding.dispose();
        this.itemsSizeBinding = null;
        this.tableTotalFormatArgs[0] = null;
        this.tableTotalFormatArgs = null;

        this.messages.removeListener(this);
        this.messages.disposeBinging(this);
        this.messages.disposeBinging(colType);
        this.messages.disposeBinging(colOperate);
        this.messages = null;

        // 销毁此 multiToast
        if (this.multiToast.getState() != ToastState.DESTROY)
            this.toastHelper.destroy(this.multiToast);

        this.multiToast = null;

        this.table.setItems(null);

        this.btnClear.disableProperty().unbind();
        this.colSelected.getGraphic().disableProperty().unbind();
        this.labTotal.textProperty().unbind();

        log.trace("TableViewListToastController is destroyed.");
    }
}