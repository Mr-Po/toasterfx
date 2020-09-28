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
package org.pomo.toasterfx.model;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.scene.Node;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.Toaster;
import org.pomo.toasterfx.common.Actionable;
import org.pomo.toasterfx.common.Destroyable;
import org.pomo.toasterfx.common.TeFunction;
import org.pomo.toasterfx.common.ToasterAware;
import org.pomo.toasterfx.model.scalable.MutableStateToast;
import org.pomo.toasterfx.model.scalable.NodeCreateable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h2>抽象的 - 消息体</h2>
 *
 * <p>实现了{@code Toast}需实现的必要接口</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:20:56</p>
 * <p>更新时间：2020-09-23 20:20:56</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see Toast
 * @see ToasterAware
 * @see Destroyable
 * @see Actionable
 * @see MutableStateToast
 * @see NodeCreateable
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractToast<T extends Toast>
        implements Toast, ToasterAware, Destroyable, Actionable, MutableStateToast, NodeCreateable {

    /**
     * 此条消息的创建时间
     */
    @Getter
    private final long createTime = System.currentTimeMillis();

    /**
     * 消息状态 Wrapper
     */
    private ReadOnlyObjectWrapper<ToastState> stateWrapper
            = new ReadOnlyObjectWrapper<>(ToastState.ABLE_SHOW);

    /**
     * 参数
     */
    @Getter
    @NonNull
    private ToastParameter parameter;

    @NonNull
    @Getter(AccessLevel.PROTECTED)
    private Function<T, Node> nodeSupplier;

    /**
     * 消息者
     */
    @Setter
    @Getter(AccessLevel.PROTECTED)
    private Toaster toaster;

    /**
     * 点击时调用
     * onAction：返回true时，关闭当前Toaster
     */
    @Setter
    @Accessors(chain = true)
    @Getter(AccessLevel.PROTECTED)
    private TeFunction<Event, T, Node, Boolean> onAction;

    /**
     * 销毁执行
     */
    @Setter
    @Accessors(chain = true)
    private Consumer<Toast> onDestroy;

    @Override
    @SuppressWarnings("unchecked")
    public boolean onAction(@NonNull Event event, @NonNull Toast toast, @NonNull Node node) {

        if (this.onAction == null)
            throw new IllegalArgumentException("onAction is null.");

        return this.onAction.exec(event, (T) toast, node);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createNode(@NonNull Toast toast) {
        return this.nodeSupplier.apply((T) toast);
    }

    @Override
    public boolean hasAction() {
        return this.getOnAction() != null;
    }

    @Override
    public void close() {
        this.toaster.close();
    }

    @Override
    public ReadOnlyObjectProperty<ToastState> getStateProperty() {
        return this.stateWrapper.getReadOnlyProperty();
    }

    @Override
    public ToastState getState() {
        return this.stateWrapper.get();
    }

    @Override
    public void setToastState(ToastState toastState) {
        this.stateWrapper.set(toastState);
    }

    @Override
    public void onDestroy() {

        if (this.getState() != ToastState.DESTROY)
            throw new IllegalArgumentException("Toast[" + this.getState() + "], non DESTROY state.");

        if (this.onDestroy != null) this.onDestroy.accept(this);

        this.parameter = null;
        this.toaster = null;
        this.onAction = null;
        this.stateWrapper = null;
        this.nodeSupplier = null;
        this.onDestroy = null;
    }
}
