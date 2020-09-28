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
 * <h2>消息体 - 类型</h2>
 *
 * <p>可通过实现此接口自定义消息类型</p>
 * <br/>
 *
 * <p>创建时间：2020-09-25 10:22:08</p>
 * <p>更新时间：2020-09-25 10:22:08</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.model.impl.ToastTypes
 */
public interface ToastType {

    /**
     * <h2>得到样式集</h2>
     * <p>用于修饰：进度条、ToastBar左侧的图标</p>
     *
     * @return 样式集
     */
    String[] getStyleClass();

    /**
     * <h2>得到顺位</h2>
     * <p>用于在消息列表bar中进行排序</p>
     * <p>此顺位不会影响table中的顺序，table中会使用消息的创建时间</p>
     *
     * @return 顺位
     * @see org.pomo.toasterfx.controller.TableViewListToastController
     */
    int getOrder();

    /**
     * <h2>得到名字</h2>
     * <p>可能为ResourceBundle的key值</p>
     *
     * @return 名称/key
     */
    String getName();
}
