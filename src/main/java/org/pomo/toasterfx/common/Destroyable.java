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
package org.pomo.toasterfx.common;

/**
 * <h2>可销毁的组件</h2>
 *
 * <p>实现此接口的实例(Node、Toast、ToastBox、ToasterTransition、PopupStrategy)，当Toaster被close时，会触发此执行。</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 14:40:44</p>
 * <p>更新时间：2020-09-23 14:40:44</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@FunctionalInterface
public interface Destroyable {

    /**
     * <h2>销毁回调</h2>
     *
     * <p>当作用于Node时，若Node因处于弱/软引用中被回收，则此回调永不会被调用。</p>
     * <p>因此，若有一定要销毁的执行，请使用dock/unDock</p>
     */
    void onDestroy();
}
