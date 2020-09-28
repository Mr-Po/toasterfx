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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.ToastHelper;
import org.pomo.toasterfx.model.AbstractToast;
import org.pomo.toasterfx.model.MultiToast;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.model.scalable.ProgressCondition;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * <h2>列表消息体</h2>
 *
 * <p>多消息的具体实现，处理多消息的组装。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:11:32</p>
 * <p>更新时间：2020-09-23 17:11:32</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class ListToast extends AbstractToast<ListToast>
        implements MultiToast, ProgressCondition {

    // region {属性}
    /**
     * 消息体 助理
     */
    private final ToastHelper toastHelper;

    /**
     * 消息集 Wrapper
     */
    @Getter
    private final ObservableList<Toast> toasts;
    // endregion

    public ListToast(@NonNull ToastParameter parameter,
                     @NonNull ToastHelper toastHelper,
                     @NonNull Function<ListToast, Node> nodeSupplier) {
        super(parameter, nodeSupplier);

        this.toastHelper = toastHelper;
        this.toasts = FXCollections.observableList(new LinkedList<>());
    }

    @Override
    public Boolean isProgress() {
        return false;
    }

    @Override
    public void onDestroy() {

        log.debug("{} archive toast left.", this.toasts.size());

        // 销毁集合Toast、Node
        this.toastHelper.destroy(this.toasts);
        this.toasts.clear();

        super.onDestroy();

        log.trace("ListToast is destroyed.");
    }
}
