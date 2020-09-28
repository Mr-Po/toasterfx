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
package org.pomo.toasterfx.control.impl;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.Archiveable;
import org.pomo.toasterfx.model.MultiToast;
import org.pomo.toasterfx.model.ReferenceType;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastType;
import org.pomo.toasterfx.util.FXMessages;
import org.pomo.toasterfx.util.FXUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * <h2>列表 消息条</h2>
 * <p>可显示多消息体中，各个类型的数量</p>
 * <p>支持国际化</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 16:33:41</p>
 * <p>更新时间：2020-09-23 16:33:41</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class ListToastBar extends ToastBar
        implements Archiveable {

    private MultiToast multiToast;

    /**
     * 类型-数量 Map <消息类型，对应的数量>
     * <P>只会在UI线程操作</P>
     */
    private final Map<ToastType, Long> typeCountMap;

    /**
     * 内容
     */
    private final StringBuilder contentBuilder;

    private MessageFormat contentPrefix;
    private String valueDelimiter;
    private String contentDelimiter;
    private String contentSuffix;

    /**
     * 参数，用于contentPrefix
     */
    private final Object[] totalSizeArgs;

    private final ListChangeListener<Toast> toastListChangeListener;

    private FXMessages messages;

    {
        this.contentBuilder = new StringBuilder();

        this.typeCountMap = new HashMap<>();
        this.totalSizeArgs = new Object[1];

        this.toastListChangeListener = change -> {

            FXUtils.checkFxUserThread();

            // 更新类型数量
            while (change.next()) {

                if (change.wasAdded()) {

                    this.updateCount(change, this::addedCount);

                } else if (change.wasRemoved()) {

                    this.updateCount(change, this::removedCount);

                } else {

                    throw new IllegalArgumentException("Unknown modification !");
                }
            }

            // 更新总数
            this.totalSizeArgs[0] = this.multiToast.getToasts().size();

            this.updateContent();
        };
    }

    /**
     * @param messages   国际化信息
     * @param multiToast 多消息体
     * @param graphic    图标
     */
    public ListToastBar(@NonNull FXMessages messages,
                        @NonNull MultiToast multiToast,
                        Node graphic) {
        super();

        this.setGraphic(graphic);

        this.initialize(messages, multiToast);
    }

    public ListToastBar(@NonNull FXMessages messages,
                        @NonNull MultiToast multiToast) {
        super(null, null);// 会生成缺省graphic

        this.initialize(messages, multiToast);
    }

    /**
     * <h2>初始化</h2>
     *
     * @param messages   国际化消息
     * @param multiToast 多消息体
     */
    private void initialize(@NonNull FXMessages messages,
                            @NonNull MultiToast multiToast) {

        this.messages = messages;
        this.multiToast = multiToast;

        this.multiToast.getToasts().addListener(this.toastListChangeListener);

        this.updateLocale();
        messages.bindProperty(this, this.getTitleProperty(), "toasterfx.listToastTitle");
        messages.addListener(this, it -> this.updateLocale());
    }

    /**
     * <h2>更新内容</h2>
     */
    private void updateContent() {

        // 共有消息：{0}条。\n
        this.contentBuilder.append(this.contentPrefix.format(this.totalSizeArgs));

        this.typeCountMap.entrySet().stream().sorted(this::compareByToastType).forEach(it ->
                this.contentBuilder
                        .append(messages.get(it.getKey().getName()))
                        .append(this.valueDelimiter)
                        .append(it.getValue())
                        .append(this.contentDelimiter)
        );

        // 存在至少一条消息
        if (!this.typeCountMap.isEmpty()) {

            this.contentBuilder.setLength(this.contentBuilder.length() - this.contentDelimiter.length());

            this.contentBuilder.append(this.contentSuffix);
        }

        this.setContent(this.contentBuilder.toString());

        this.contentBuilder.setLength(0);
    }

    /**
     * <h2>增加数量</h2>
     *
     * @param type 类型
     * @param num  更新数量（可能为负数）
     */
    private void addedCount(ToastType type, long num) {

        Long count = this.typeCountMap.get(type);

        if (count == null) {

            count = num;

        } else {

            count += num;
        }

        if (count < 0) {

            throw new IllegalArgumentException("count ＜ 0 .");

        } else if (count == 0) {

            this.typeCountMap.remove(type);

        } else {

            this.typeCountMap.put(type, count);
        }
    }

    /**
     * <h2>移除数量</h2>
     *
     * @param type 类型
     * @param num  更新数量（可能为负数）
     */
    private void removedCount(ToastType type, long num) {
        this.addedCount(type, -num);
    }

    /**
     * <h2>更新数量</h2>
     *
     * @param change   改变
     * @param consumer 处理改变数量
     */
    private void updateCount(ListChangeListener.Change<? extends Toast> change, BiConsumer<ToastType, Long> consumer) {
        change.getAddedSubList().stream()
                .collect(Collectors.groupingBy(Toast::getType, Collectors.counting()))
                .forEach(consumer);
    }

    /**
     * <h2>更新地区</h2>
     */
    private void updateLocale() {

        this.contentPrefix = messages.getFormat("toasterfx.listToastContentPrefix");

        this.valueDelimiter = messages.get("toasterfx.listToastValueDelimiter");
        this.contentDelimiter = messages.get("toasterfx.listToastContentDelimiter");
        this.contentSuffix = messages.get("toasterfx.listToastContentSuffix");

        this.updateContent();
    }

    /**
     * <h2>通过消息类型排序</h2>
     *
     * @param o1 对象1
     * @param o2 对象2
     * @return 排序位码
     */
    private int compareByToastType(Map.Entry<ToastType, Long> o1, Map.Entry<ToastType, Long> o2) {
        return Integer.compare(o1.getKey().getOrder(), o2.getKey().getOrder());
    }

    @Override
    public ReferenceType onArchive(@NonNull Toast toast, Node node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.messages.disposeBinging(this);
        this.messages.removeListener(this);

        this.multiToast.getToasts().removeListener(this.toastListChangeListener);

        this.typeCountMap.clear();

        this.contentBuilder.setLength(0);

        this.contentPrefix = null;
        this.valueDelimiter = null;
        this.contentDelimiter = null;
        this.contentSuffix = null;
        this.totalSizeArgs[0] = null;

        // 此处不销毁多信息，因为可能在其它地方会用到此实例
        this.multiToast = null;

        log.trace("ListToastBar is destroyed.");
    }
}
