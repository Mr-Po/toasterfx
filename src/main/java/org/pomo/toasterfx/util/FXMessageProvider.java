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
package org.pomo.toasterfx.util;

import lombok.NonNull;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <h2>国际化资源包 - 提供者</h2>
 *
 * <p>针对Java9+，进行国际化包扩展</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:46:09</p>
 * <p>更新时间：2020-09-27 15:46:09</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
public interface FXMessageProvider {

    /**
     * <h2>得到一个资源包</h2>
     *
     * @param baseName 基础名
     * @param locale   地区
     * @return 资源包，允许返回null
     */
    ResourceBundle getBundle(@NonNull String baseName, @NonNull Locale locale);
}
