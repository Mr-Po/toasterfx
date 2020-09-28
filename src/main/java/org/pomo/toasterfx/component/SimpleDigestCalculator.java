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
package org.pomo.toasterfx.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * <h2>简单的 - 摘要计算器</h2>
 * <p>可定制摘要长度、多段文本的连接符、省略文本</p>
 * <p>默认的构造函数，提供不限长度、\t、...</p>
 * <br/>
 *
 * <p>创建时间：2020-09-23 15:04:46</p>
 * <p>更新时间：2020-09-23 15:04:46</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@RequiredArgsConstructor
public class SimpleDigestCalculator implements BiFunction<String, String, String> {

    /**
     * 摘要长度
     */
    private final Integer length;

    /**
     * 连接符
     */
    @NonNull
    private final String delimiter;

    /**
     * 省略字符串
     */
    @NonNull
    private final String ellipsisString;

    /**
     * <h4>默认构造函数</h4>
     *
     * <p>提供不限长度、\t、...</p>
     */
    public SimpleDigestCalculator() {
        this.length = null;
        this.delimiter = "\t";
        this.ellipsisString = "...";
    }

    @Override
    public String apply(String s, String s2) {
        return this.calculateDigest(this.length, this.delimiter, s, s2);
    }

    /**
     * <h2>计算摘要</h2>
     *
     * @param len       长度限制
     * @param delimiter 连接符
     * @param contents  内容组
     * @return 摘要
     */
    protected String calculateDigest(Integer len, @NonNull String delimiter, @NonNull String... contents) {

        String digest = Arrays.stream(contents)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(delimiter))
                .replace("\n", delimiter);

        if (len != null && digest.length() > len)
            digest = digest.substring(0, len) + this.ellipsisString;

        return digest;
    }
}
