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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.Archiveable;
import org.pomo.toasterfx.component.SimpleListToastSupplier;
import org.pomo.toasterfx.model.MultiToast;
import org.pomo.toasterfx.model.ReferenceType;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastState;
import org.pomo.toasterfx.model.scalable.MutableStateToast;
import org.pomo.toasterfx.util.FXMessages;
import org.pomo.toasterfx.util.FXUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <h2>多消息工厂</h2>
 *
 * <p>负责产生“多消息”</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:54:42</p>
 * <p>更新时间：2020-09-27 15:54:42</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class MultiToastFactory {

    // region {成员组件}
    /**
     * 消息者工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterFactory toasterFactory;

    /**
     * 国际化消息
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private FXMessages messages;

    /**
     * Node 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private NodeHelper nodeHelper;

    /**
     * ToasterFX 服务
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterService service;

    /**
     * 消息体 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToastHelper toastHelper;
    // endregion

    // region {成员变量}
    /**
     * 多消息体 生成器
     *
     * <p>其get方法用于提供可观察样式列表等的修改</p>
     */
    @Getter
    @Setter
    @NonNull
    private Supplier<? extends MultiToast> multiToastSupplier;

    /**
     * 多消息体
     */
    private MultiToast multiToast;
    // endregion

    /**
     * <h2>初始化</h2>
     */
    void initialize() {

        Objects.requireNonNull(this.toasterFactory, "toasterFactory must non-null but is null.");
        Objects.requireNonNull(this.messages, "messages must non-null but is null.");
        Objects.requireNonNull(this.nodeHelper, "nodeHelper must non-null but is null.");
        Objects.requireNonNull(this.service, "service must non-null but is null.");
        Objects.requireNonNull(this.toastHelper, "toastHelper must non-null but is null.");

        if (this.multiToastSupplier == null) {

            SimpleListToastSupplier multiToastSupplier = new SimpleListToastSupplier();

            multiToastSupplier.setService(this.service);
            multiToastSupplier.setToasterFactory(this.toasterFactory);
            multiToastSupplier.setMessages(this.messages);
            multiToastSupplier.setToastHelper(this.toastHelper);

            multiToastSupplier.initialize();

            this.multiToastSupplier = multiToastSupplier;
        }
    }

    /**
     * <h2>是否处于显示中</h2>
     *
     * @return 是/否
     */
    public boolean isShown() {

        if (this.multiToast == null) return false;

        ToastState state = this.multiToast.getState();

        return state == ToastState.SHOWN || state == ToastState.SHOWING;
    }


    /**
     * <h2>封存</h2>
     * <p>此时的消息还未展示</p>
     *
     * @param toasts 消息体集合
     */
    void archive(Collection<Toast> toasts) {

        log.trace("{} messages is archived...", toasts.size());

        toasts.forEach(this::transformArchive);

        FXUtils.smartLater(() -> this.get().getToasts().addAll(toasts));
    }

    /**
     * <h2>归档</h2>
     * <p>当Toaster过多时，将稍前的Toaster进行归档，防止信息丢失</p>
     * <p>此时的消息已展示</p>
     *
     * @param toast 消息体
     */
    void archive(Toast toast) {

        FXUtils.checkFxUserThread();

        this.doArchive(toast);

        this.get().getToasts().add(toast);
    }

    /**
     * <h2>得到一个 多消息体</h2>
     * <p>此方法是线程安全的</p>
     *
     * @return 多消息体
     */
    private MultiToast get() {

        FXUtils.checkFxUserThread();

        // 当多消息体不存在 或 当前多消息体处于关闭中时
        if (this.multiToast == null ||
                this.multiToast.getState() == ToastState.CLOSING)
            this.multiToast = this.bron();

        // 必须为可显示才允许显示
        if (this.multiToast.getState() == ToastState.ABLE_SHOW) {

            this.toasterFactory.show(this.multiToast, 0);
        }

        return this.multiToast;
    }

    /**
     * <h2>产生一个新的 多消息体</h2>
     *
     * @return 多消息体
     */
    private MultiToast bron() {

        MultiToast multiToast = this.multiToastSupplier.get();
        log.trace("{} is created.", multiToast.getClass().getName());

        ReadOnlyObjectProperty<ToastState> stateProperty = multiToast.getStateProperty();
        stateProperty.addListener(new ChangeListener<ToastState>() {

            @Override
            public void changed(ObservableValue<? extends ToastState> observable,
                                ToastState oldValue, ToastState newValue) {

                if (newValue == ToastState.DESTROY) {

                    FXUtils.checkFxUserThread();

                    stateProperty.removeListener(this);

                    // 极限情况下，可能会出现多个MultiToast（如：旧的处于退出中，此时出现大量toast）
                    if (MultiToastFactory.this.multiToast == multiToast) {

                        MultiToastFactory.this.multiToast = null;
                        log.trace("multiToast is reset.");
                    }
                }
            }
        });

        return multiToast;
    }

    /**
     * <h2>转换并归档</h2>
     *
     * @param toast 消息体
     */
    private void transformArchive(Toast toast) {

        if (toast.getState() == ToastState.ABLE_SHOW)
            FXUtils.run(toast, MutableStateToast.class, it -> it.setToastState(ToastState.ARCHIVING));

        this.doArchive(toast);
    }

    /**
     * <h2>归档</h2>
     * <p>无需处于UI线程</p>
     * <p>会触发Toast、Node上的Archiveable接口</p>
     * <p>当条件合适，会销毁Node，即：触发Toast的NodeDestroyable、Node的Destroyable</p>
     *
     * @param toast 消息体
     */
    private void doArchive(Toast toast) {

        if (toast instanceof MultiToast)
            throw new IllegalArgumentException("MultiToast can not archived.");

        ToastState state = toast.getState();

        if (state == ToastState.ARCHIVE)
            throw new IllegalArgumentException("toast[" + toast + "] already archived.");

        if (state != ToastState.ARCHIVING)
            throw new IllegalArgumentException("toast[" + toast + "], expect" + ToastState.ARCHIVING +
                    ", actual : " + state + ".");

        Node node = this.nodeHelper.tryGet(toast).orElse(null);

        // 尝试执行Toast的归档，node为null时，仍会执行
        ReferenceType referenceType = FXUtils
                .call(toast, Archiveable.class, it -> it.onArchive(toast, node))
                .orElse(null);

        this.nodeHelper.archive(toast, node, referenceType);

        // 修改状态 ——> 归档
        FXUtils.run(toast, MutableStateToast.class, it -> it.setToastState(ToastState.ARCHIVE));
    }

    /**
     * <h2>销毁</h2>
     */
    void destroy() {

        if (this.multiToast != null)
            this.toastHelper.destroy(this.multiToast);// 会触发监听置空自身

        this.multiToastSupplier = null;

        log.trace("MultiToastFactory is destroyed.");
    }
}
