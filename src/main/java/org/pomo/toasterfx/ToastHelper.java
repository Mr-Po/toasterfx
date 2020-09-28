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

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.Destroyable;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastState;
import org.pomo.toasterfx.model.scalable.MutableStateToast;
import org.pomo.toasterfx.util.FXUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>消息体 助理</h2>
 *
 * <p>维护消息体的生命周期</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 17:55:28</p>
 * <p>更新时间：2020-09-27 17:55:28</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class ToastHelper {

    // region {成员组件}
    /**
     * Node 助理
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private NodeHelper nodeHelper;

    /**
     * 多消息工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private MultiToastFactory multiToastFactory;

    /**
     * 消息者工厂
     */
    @NonNull
    @Setter(AccessLevel.PACKAGE)
    private ToasterFactory toasterFactory;

    /**
     * 消息体处理器
     */
    @Setter
    @NonNull
    private ToastHandler toastHandler;
    // endregion

    /**
     * <h2>初始化</h2>
     */
    void initialize() {

        Objects.requireNonNull(this.nodeHelper, "nodeHelper must non-null but is null.");
        Objects.requireNonNull(this.multiToastFactory, "multiToastFactory must non-null but is null.");
        Objects.requireNonNull(this.toasterFactory, "toasterFactory must non-null but is null.");

        if (this.toastHandler == null) this.toastHandler = new ToastHandler();

        this.toastHandler.setMultiToastFactory(this.multiToastFactory);
        this.toastHandler.setToasterFactory(this.toasterFactory);
        this.toastHandler.setToastHelper(this);

        this.toastHandler.initialize();
    }

    /**
     * <h2>推入</h2>
     * <p>将 消息 放入 消息队列 中</p>
     * <p>当短时间内，push量很大时。此消息可能不会弹出，而是放入 消息列表 中</p>
     *
     * @param toast 消息
     * @return 是否添加成功（立即）
     */
    boolean push(@NonNull Toast toast) {

        ToastState state = toast.getState();

        // 不处于可展示状态，不予push
        if (state != ToastState.ABLE_SHOW) {
            log.warn("current toast of state is {}, stop push().", state);
            return false;
        }

        return this.toastHandler.push(toast);
    }

    /**
     * <h2>推入</h2>
     * <p>将 消息 放入 消息队列 中</p>
     *
     * @param toasts 消息集
     * @return 是否添加成功（立即）
     */
    boolean push(@NonNull Collection<? extends Toast> toasts) {

        List<Toast> list = toasts.stream()
                .filter(it -> it.getState() == ToastState.ABLE_SHOW)
                .collect(Collectors.toList());

        if (list.size() != toasts.size())
            log.warn("toast collection total: {}, qualified: {}.", toasts.size(), list.size());

        if (list.isEmpty()) return false;

        return this.toastHandler.push(list);
    }

    /**
     * <h2>销毁 消息体</h2>
     * <p>当Node存在时，会销毁Node</p>
     *
     * @param toast 消息体
     */
    public void destroy(@NonNull Toast toast) {

        if (toast.getState() == ToastState.DESTROY)
            throw new IllegalArgumentException("current toast of state is " + toast.getState() + ", stop destroy().");

        this.nodeHelper.forget(toast);

        this.destroyToast(toast);
    }

    /**
     * <h2>销毁 消息体集合</h2>
     * <p>当Node存在时，会销毁Node</p>
     *
     * @param toasts 消息体集合
     */
    public void destroy(@NonNull Collection<Toast> toasts) {
        toasts.forEach(this::destroy);
    }

    /**
     * <h2>重置 消息体</h2>
     *
     * @param toast 消息体
     */
    public void reset(@NonNull Toast toast) {

        if (toast.getState() != ToastState.ARCHIVE)
            throw new IllegalArgumentException("current toast of state is " + toast.getState() + ", stop reset().");

        FXUtils.run(toast, MutableStateToast.class, it -> it.setToastState(ToastState.ABLE_SHOW));
    }

    /**
     * <h2>是否为空</h2>
     * <p>消息列表 与 待显示消息列表 是否为空</p>
     *
     * @return 是/否
     */
    boolean isEmpty() {
        return this.toastHandler.isEmpty();
    }

    /**
     * <h2>仅销毁消息体</h2>
     */
    void destroyToast(@NonNull Toast toast) {

        if (toast.getState() == ToastState.DESTROY)
            throw new IllegalArgumentException("current toast of state is " + toast.getState() + ", stop destroy().");

        // 将Toast的状态修改为：销毁
        FXUtils.run(toast, MutableStateToast.class, it -> it.setToastState(ToastState.DESTROY));

        // 销毁此消息
        FXUtils.run(toast, Destroyable.class, Destroyable::onDestroy);
    }

    /**
     * <h2>销毁</h2>
     */
    void destroy() {

        this.toastHandler.destroy();
        this.toastHandler = null;

        log.trace("ToastHelper is destroyed.");
    }
}
