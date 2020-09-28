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

/**
 * <h2>消息体 - 状态</h2>
 *
 * <p>此枚举揭示消息体的生命周期</p>
 * <br/>
 *
 * <p>创建时间：2020-09-25 10:18:31</p>
 * <p>更新时间：2020-09-25 10:18:31</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public enum ToastState {

    /**
     * 可展示的
     * <p>刚生成时处于此状态，或归档后重新待展示也会处于此状态</p>
     */
    ABLE_SHOW,
    /**
     * 展示中
     * <p>处于展示动画中，尚未完全展示出来</p>
     */
    SHOWING,
    /**
     * 已展示的
     * <p>展示动画已播放完毕</p>
     */
    SHOWN,
    /**
     * 归档中
     * <p>MultiToast无此状态</p>
     */
    ARCHIVING,
    /**
     * 归档
     * <p>MultiToast无此状态</p>
     */
    ARCHIVE,
    /**
     * 隐藏
     * <p>此状态下，不会产生和显示MultiToaster</p>
     * <p>适用于MultiToast</p>
     */
    HIDE,
    /**
     * 关闭中
     * <p>此状态，若产生大量消息，会出现一个新的MultiToaster</p>
     * <p>当Toast被调用close()后，会立即处于此状态</p>
     * <p>此状态下，播放退出动画</p>
     */
    CLOSING,
    /**
     * 销毁
     * <p>当Toast被调用destroy()后，处于此状态</p>
     */
    DESTROY
}
