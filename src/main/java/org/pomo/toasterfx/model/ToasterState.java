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
 * <h2>消息者状态</h2>
 *
 * <p>用于标记消息者的生命周期</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 20:54:51</p>
 * <p>更新时间：2020-09-23 20:54:51</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public enum ToasterState {

    /**
     * 初始状态
     * 空闲
     */
    INITIAL,
    /**
     * 预备状态
     * 非空闲
     */
    PREPARE,
    /**
     * 显示中
     */
    SHOWING,
    /**
     * 已显示
     */
    SHOWN,
    /**
     * 隐藏中
     */
    HIDING,
    /**
     * 已隐藏
     */
    HIDDEN,
    /**
     * 重置状态
     * 空闲
     */
    RESET,
    /**
     * 销毁
     */
    DESTROY
}