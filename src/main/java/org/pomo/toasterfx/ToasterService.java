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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.pomo.toasterfx.model.Toast;
import org.pomo.toasterfx.model.ToastParameter;
import org.pomo.toasterfx.util.FXMessages;
import org.pomo.toasterfx.util.FXUtils;

import java.util.Collection;

/**
 * <h2>ToasterFX 服务</h2>
 *
 * <p>提供快速弹出弹窗api</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 16:35:06</p>
 * <p>更新时间：2020-09-27 16:35:06</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
@NoArgsConstructor
public class ToasterService {

    /**
     * 默认的基础包
     */
    public final static String DEFAULT_BASE_NAME = "org.pomo.toasterfx.language.Message";

    @Getter
    @Setter
    @NonNull
    private FXMessages messages;

    @Setter
    @Getter
    @NonNull
    private ToasterFactory toasterFactory;

    @Setter
    @Getter
    @NonNull
    private MultiToastFactory multiToastFactory;

    @Getter
    @Setter
    @NonNull
    private ToastHelper toastHelper;

    @Getter
    @Setter
    @NonNull
    private NodeHelper nodeHelper;

    /**
     * 默认的 - 消息参数
     */
    @Getter
    @Setter
    @NonNull
    private ToastParameter defaultToastParameter;

    public ToasterService(FXMessages messages) {
        this.messages = messages;
    }

    /**
     * <h2>初始化</h2>
     */
    public void initialize() {

        this.autoFill();

        this.messages.getBaseNames().add(DEFAULT_BASE_NAME);

        this.toasterFactory.setMessages(this.messages);
        this.toasterFactory.setMultiToastFactory(this.multiToastFactory);
        this.toasterFactory.setNodeHelper(this.nodeHelper);
        this.toasterFactory.setToastHelper(this.toastHelper);

        this.multiToastFactory.setToasterFactory(this.toasterFactory);
        this.multiToastFactory.setMessages(this.messages);
        this.multiToastFactory.setNodeHelper(this.nodeHelper);
        this.multiToastFactory.setToastHelper(this.toastHelper);
        this.multiToastFactory.setService(this);

        this.toastHelper.setMultiToastFactory(this.multiToastFactory);
        this.toastHelper.setToasterFactory(this.toasterFactory);
        this.toastHelper.setNodeHelper(this.nodeHelper);

        this.nodeHelper.setToastHelper(this.toastHelper);

        this.messages.initialize();
        this.toasterFactory.initialize();
        this.multiToastFactory.initialize();
        this.toastHelper.initialize();
        this.nodeHelper.initialize();
    }

    /**
     * <p>自动填充</p>
     */
    protected void autoFill() {

        if (this.messages == null) this.messages = new FXMessages();
        if (this.toastHelper == null) this.toastHelper = new ToastHelper();
        if (this.nodeHelper == null) this.nodeHelper = new NodeHelper();
        if (this.multiToastFactory == null) this.multiToastFactory = new MultiToastFactory();
        if (this.toasterFactory == null) this.toasterFactory = new ToasterFactory();

        if (this.defaultToastParameter == null) this.defaultToastParameter = ToastParameter.builder().build();
    }

    /**
     * <h2>推入</h2>
     * <p>将 消息 放入 消息队列 中</p>
     * <p>当短时间内，push量很大时。此消息可能不会弹出，而是放入 消息列表 中</p>
     *
     * @param toast 消息
     * @return 是否添加成功（立即）
     */
    public boolean push(@NonNull Toast toast) {
        return this.toastHelper.push(toast);
    }

    /**
     * <h2>推入</h2>
     * <p>将 消息 放入 消息队列 中</p>
     * <p>当短时间内，push量很大时。此消息可能不会弹出，而是放入 消息列表 中</p>
     *
     * @param toasts 消息集
     * @return 是否添加成功（立即）
     */
    public boolean push(@NonNull Collection<? extends Toast> toasts) {
        return this.toastHelper.push(toasts);
    }

    /**
     * <h2>销毁</h2>
     * <p>仅允许在ui线程调用</p>
     */
    public void destroy() {

        FXUtils.checkFxUserThread();

        this.toastHelper.destroy();
        this.toastHelper = null;

        this.toasterFactory.destroy();
        this.toasterFactory = null;

        this.multiToastFactory.destroy();
        this.multiToastFactory = null;

        this.nodeHelper.destroy();
        this.nodeHelper = null;

        this.messages.destroy();
        this.messages = null;

        log.info("ToasterFXService is destroyed.");
    }
}
