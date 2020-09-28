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
package org.pomo.toasterfx.model.impl;

import javafx.event.Event;
import javafx.scene.Node;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.common.Archiveable;
import org.pomo.toasterfx.common.Closeable;
import org.pomo.toasterfx.common.Dockable;
import org.pomo.toasterfx.common.TeFunction;
import org.pomo.toasterfx.model.*;
import org.pomo.toasterfx.model.scalable.*;

import java.util.function.*;

/**
 * <h2>单消息体</h2>
 *
 * <p>实现了{@code Toast} 的全部可扩展接口。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:14:43</p>
 * <p>更新时间：2020-09-23 17:14:43</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see Toast
 * @see Archiveable
 * @see NodeDestroyable
 * @see Closeable
 * @see Dockable
 * @see NodeRecyclable
 * @see DigestSupport
 * @see ProgressCondition
 * @see CloseCondition
 */
@Slf4j
public class SingleToast extends AbstractToast<SingleToast>
        implements Archiveable, NodeDestroyable, Closeable, Dockable, NodeRecyclable,
        DigestSupport,
        ProgressCondition, CloseCondition {

    // region {属性}
    /**
     * 类型
     */
    @Getter
    private ToastType type;

    /**
     * 摘要
     */
    private String digest;

    /**
     * 摘要创建器
     */
    private Supplier<String> digestSupplier;

    /**
     * 是否显示关闭按钮
     */
    @Setter
    private Boolean isShowClose;

    /**
     * 是否进行进度条展示
     */
    @Setter
    private Boolean isProgress;

    /**
     * 当点击ToastBox中的关闭按钮时调用<br/>
     * onClose：返回false时，阻止关闭
     */
    @Setter
    @Accessors(chain = true)
    private TeFunction<Event, SingleToast, Node, Boolean> onClose;

    /**
     * 归档执行
     * node可能为null
     */
    @Setter
    @Accessors(chain = true)
    private BiFunction<SingleToast, Node, ReferenceType> onArchive;


    /**
     * Node销毁执行
     */
    @Setter
    @Accessors(chain = true)
    private BiConsumer<SingleToast, Node> onNodeDestroy;

    /**
     * 当弱/软引用中的Node被GC回收时，会触发此回调
     */
    @Setter
    @Accessors(chain = true)
    private Consumer<SingleToast> onNodeRecycle;

    /**
     * 显示时回调，播放进入动画前
     */
    @Setter
    @Accessors(chain = true)
    private BiConsumer<Toast, Node> onDock;

    /**
     * 隐藏时回调，退出动画结束后
     */
    @Setter
    @Accessors(chain = true)
    private BiConsumer<Toast, Node> onUnDock;
    // endregion

    public SingleToast(@NonNull ToastParameter parameter,
                       @NonNull ToastType type,
                       @NonNull String digest,
                       @NonNull Function<SingleToast, Node> nodeSupplier) {
        super(parameter, nodeSupplier);

        this.type = type;
        this.digest = digest;
    }

    public SingleToast(@NonNull ToastParameter parameter,
                       @NonNull ToastType type,
                       @NonNull Supplier<String> digestSupplier,
                       @NonNull Function<SingleToast, Node> nodeSupplier) {
        super(parameter, nodeSupplier);

        this.type = type;
        this.digestSupplier = digestSupplier;
    }

    public SingleToast(@NonNull ToastParameter parameter,
                       @NonNull ToastType type,
                       @NonNull Function<SingleToast, Node> nodeSupplier) {
        super(parameter, nodeSupplier);
        this.type = type;
    }

    @Override
    public String getDigest() {

        if (this.digest == null && this.digestSupplier != null) {

            this.digest = this.digestSupplier.get();
            this.digestSupplier = null;
        }

        return this.digest;
    }

    @Override
    public ReferenceType onArchive(@NonNull Toast toast, Node node) {

        ReferenceType strategy = null;

        if (this.onArchive != null) strategy = this.onArchive.apply(this, node);

        return strategy;
    }

    @Override
    public void onNodeDestroy(Node node) {
        if (this.onNodeDestroy != null) this.onNodeDestroy.accept(this, node);
    }

    @Override
    public void onNodeRecycle() {
        if (this.onNodeRecycle != null) this.onNodeRecycle.accept(this);
    }

    @Override
    public void dock(@NonNull Toast toast, @NonNull Node node) {
        if (this.onDock != null) this.onDock.accept(toast, node);
    }

    @Override
    public void unDock(@NonNull Toast toast, @NonNull Node node) {
        if (this.onUnDock != null) this.onUnDock.accept(toast, node);
    }

    @Override
    public Boolean onClose(@NonNull Event event, @NonNull Toast toast, @NonNull Node node) {

        if (this.onClose != null) return this.onClose.exec(event, (SingleToast) toast, node);

        return null;
    }

    @Override
    public Boolean isProgress() {

        if (this.isProgress == null) return !this.getParameter().getTimeout().isIndefinite();
        else return this.isProgress;
    }

    @Override
    public Boolean isShowClose() {
        return this.isShowClose;
    }

    @Override
    public String toString() {
        return "SingleToast[" + this.getDigest() + "]";
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        this.type = null;
        this.digest = null;
        this.digestSupplier = null;

        this.onClose = null;

        this.onArchive = null;
    }
}
