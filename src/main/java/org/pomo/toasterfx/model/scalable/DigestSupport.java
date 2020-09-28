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
package org.pomo.toasterfx.model.scalable;

/**
 * <h2>摘要支持</h2>
 *
 * <p>实现此接口的{@code Toast}，可从其中得到摘要信息</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 17:44:42</p>
 * <p>更新时间：2020-09-23 17:44:42</p>
 *
 * @author Mr.Po
 * @version 1.0
 * @see org.pomo.toasterfx.model.Toast
 */
@FunctionalInterface
public interface DigestSupport {

    /**
     * <h2>得到摘要</h2>
     *
     * @return 消息摘要
     */
    String getDigest();
}
