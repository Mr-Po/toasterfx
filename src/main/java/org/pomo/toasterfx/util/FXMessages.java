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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * <h2>国际化信息</h2>
 *
 * <p>通过此类可以实现，地区与国际化信息的绑定</p>
 * <br/>
 *
 * <p>创建时间：2020-09-27 15:47:13</p>
 * <p>更新时间：2020-09-27 15:47:13</p>
 *
 * @author Mr.Po
 * @version 1.0
 */
@Slf4j
public class FXMessages {

    // region {成员变量}
    /**
     * 区域属性
     */
    @Setter
    @NonNull
    private ReadOnlyObjectProperty<Locale> localeProperty;

    /**
     * 国际化信息提供者 服务加载器
     */
    private final ServiceLoader<FXMessageProvider> providers;

    /**
     * 语言包数组
     */
    @Getter
    private final ObservableList<String> baseNames;

    /**
     * 资源包 - 集合<br/>
     * 线程安全
     */
    private Map<String, String> resourceBundleMap;

    /**
     * <锚定物 ， 区域变化监听>
     */
    private Map<Object, InvalidationListener> localeListenerMap;

    /**
     * <锚定物 ， Set<绑定> >
     */
    private Map<Object, Set<Binding<?>>> bindingMap;

    /**
     * <锚定物 ， Set<字符串属性> >
     */
    private Map<Object, Set<Property<String>>> propertiesMap;

    /**
     * 基础名集合变动监听
     */
    private ListChangeListener<? super String> baseNamesListener;
    // endregion

    public FXMessages() {

        this.providers = ServiceLoader.load(FXMessageProvider.class);
        this.baseNames = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

        this.resourceBundleMap = Collections.synchronizedMap(new HashMap<>());

        this.localeListenerMap = new LinkedHashMap<>();
        this.bindingMap = new HashMap<>();
        this.propertiesMap = new HashMap<>();
    }

    public FXMessages(ReadOnlyObjectProperty<Locale> localeProperty) {
        this();

        this.localeProperty = localeProperty;
    }

    /**
     * <h2>初始化</h2>
     */
    public void initialize() {

        if (this.localeProperty == null) {
            ReadOnlyObjectWrapper<Locale> localeWrapper = new ReadOnlyObjectWrapper<>(Locale.getDefault());
            this.localeProperty = localeWrapper.getReadOnlyProperty();
        }

        this.baseNamesListener = change -> {

            this.providers.reload();

            if (change.wasAdded()) {

                this.build(change.getAddedSubList().toArray(new String[0]));

            } else {// 删除、排序、替换 等

                this.rebuild();
            }
        };

        this.baseNames.addListener(this.baseNamesListener);
        this.addListener(this, it -> this.build());

        this.build();
    }


    /**
     * <h2>构建</h2>
     * <p>增量 or 替换</p>
     *
     * @param baseNames 基础名数组
     */
    private void build(String[] baseNames) {

        Map<String, String> map = this.getResourceBundleMap(baseNames);
        this.resourceBundleMap.putAll(map);
    }

    /**
     * <h2>构建</h2>
     * <p>增量 or 替换</p>
     */
    private void build() {
        this.build(this.baseNames.toArray(new String[0]));
    }

    /**
     * <h2>重建</h2>
     */
    private void rebuild() {

        String[] baseNames = this.baseNames.toArray(new String[0]);

        Map<String, String> old = this.resourceBundleMap;

        this.resourceBundleMap = this.getResourceBundleMap(baseNames);

        old.clear();
    }

    /**
     * <h2>得到资源包映射</h2>
     *
     * @param baseNames 基础名数组
     * @return 资源包映射
     */
    private Map<String, String> getResourceBundleMap(String[] baseNames) {
        return Arrays.stream(baseNames)
                .map(this::getResourceBundle)
                .filter(Objects::nonNull)
                .flatMap(it ->
                        it.keySet().stream()
                                .map(k -> new AbstractMap.SimpleEntry<>(k, it.getString(k)))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {

                    log.warn("The key value of the internationalization file has the same name," +
                            " {} is replaced by {}.", v1, v2);
                    return v2;
                }));
    }

    /**
     * <h2>得到资源包</h2>
     *
     * @param baseName 基础名
     * @return 资源包，可能为null
     */
    private ResourceBundle getResourceBundle(String baseName) {

        Locale locale = this.localeProperty.get();

        for (FXMessageProvider provider : this.providers) {

            ResourceBundle resourceBundle = provider.getBundle(baseName, locale);

            if (resourceBundle != null) {

                log.debug("a valid resourceBundle[{},{}] from: {}", baseName, locale, provider.getClass());
                return resourceBundle;
            }
        }

        try {
            return ResourceBundle.getBundle(baseName, locale);
        } catch (MissingResourceException e) {// 当未提供此区域的语言包时，抛出此异常
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * <h2>得到地区</h2>
     *
     * @return 地区
     */
    public Locale getLocale() {
        return this.localeProperty.get();
    }

    /**
     * <h2>得到值</h2>
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {

        // 不存在时，返回[$key]
        return this.resourceBundleMap.computeIfAbsent(key, it -> "[" + it + "]");
    }

    /**
     * <h2>得到格式化值</h2>
     *
     * @param key  键
     * @param args 参数
     * @return 值
     */
    public String format(String key, Object... args) {
        return this.getFormat(key).format(args);
    }

    /**
     * <h2>得到格式化器</h2>
     *
     * @param key 键
     * @return 格式化器
     */
    public MessageFormat getFormat(String key) {
        return new MessageFormat(this.get(key), this.localeProperty.get());
    }

    /**
     * <h2>得到一个绑定</h2>
     *
     * @param object       锚定物
     * @param func         当区域变化时的回调
     * @param dependencies 其他依赖对象们
     * @return 绑定
     */
    public StringBinding getBinding(Object object, Callable<String> func, Observable... dependencies) {

        StringBinding binding;

        if (dependencies == null || dependencies.length == 0) {

            binding = Bindings.createStringBinding(func, this.localeProperty);

        } else {

            // Arrays.asList返回的list是不可变的
            List<Observable> observables = new ArrayList<>(Arrays.asList(dependencies));
            observables.add(this.localeProperty);

            binding = Bindings.createStringBinding(func, observables.toArray(new Observable[0]));
        }

        Set<Binding<?>> set = this.bindingMap.computeIfAbsent(object, k -> new HashSet<>());
        set.add(binding);

        return binding;
    }

    /**
     * <h2>得到一个绑定</h2>
     *
     * @param object 锚定物
     * @param key    键
     * @return 绑定
     */
    public StringBinding getBinding(Object object, String key, Observable... dependencies) {

        return this.getBinding(object, () -> this.get(key), dependencies);
    }

    /**
     * <h2>绑定属性</h2>
     *
     * @param object   锚定物
     * @param property 属性
     * @param func     当区域变化时的回调
     */
    public void bindProperty(Object object, Property<String> property,
                             Callable<String> func, Observable... dependencies) {

        StringBinding binding = this.getBinding(object, func, dependencies);

        Set<Property<String>> set = this.propertiesMap.computeIfAbsent(object, k -> new HashSet<>());
        set.add(property);

        property.bind(binding);
    }

    /**
     * <h2>绑定属性</h2>
     *
     * @param object   锚定物
     * @param property 属性
     * @param key      键
     */
    public void bindProperty(Object object, Property<String> property, String key, Observable... dependencies) {

        this.bindProperty(object, property, () -> this.get(key), dependencies);
    }

    /**
     * <h2>释放绑定</h2>
     *
     * @param object 锚定物
     */
    public void disposeBinging(Object object) {

        this.propertiesMap.computeIfPresent(object, (k, v) -> {
            v.forEach(Property::unbind);
            v.clear();
            return null;
        });

        this.bindingMap.computeIfPresent(object, (k, v) -> {
            v.forEach(Binding::dispose);
            v.clear();
            return null;
        });
    }

    /**
     * <h2>添加一个地区变化监听</h2>
     *
     * @param object               锚定物
     * @param invalidationListener 地区变化监听
     * @return 地区变化监听
     */
    public InvalidationListener addListener(Object object, InvalidationListener invalidationListener) {

        if (this.localeListenerMap.containsKey(object)) {
            throw new IllegalArgumentException("anchor object: " + object.getClass() + ", listener is existed.");
        }

        this.localeListenerMap.put(object, invalidationListener);
        this.localeProperty.addListener(invalidationListener);

        return invalidationListener;
    }

    /**
     * <h2>移除一个地区变化监听</h2>
     *
     * @param object 锚定物
     */
    public void removeListener(Object object) {

        InvalidationListener invalidationListener = this.localeListenerMap.remove(object);

        if (invalidationListener == null) {

            log.warn("anchor object: {}, listener is not found.", object.getClass());

        } else {

            this.localeProperty.removeListener(invalidationListener);
        }
    }

    /**
     * <h2>销毁</h2>
     */
    public void destroy() {

        this.removeListener(this);

        this.baseNames.removeListener(this.baseNamesListener);
        this.baseNames.clear();

        Map<Object, InvalidationListener> localeListenerMap = this.localeListenerMap;
        this.localeListenerMap = null;

        Map<Object, Set<Binding<?>>> bindingMap = this.bindingMap;
        this.bindingMap = null;

        Map<Object, Set<Property<String>>> propertiesMap = this.propertiesMap;
        this.propertiesMap = null;

        int localeListenerMapSize = localeListenerMap.size();
        int bindingMapSize = bindingMap.size();
        int propertiesMapSize = propertiesMap.size();

        if (localeListenerMapSize + bindingMapSize + propertiesMapSize > 0)
            log.warn("{} locale listener left, {} binding left, {} property left.",
                    localeListenerMapSize, bindingMapSize, propertiesMapSize);

        localeListenerMap.values().forEach(this.localeProperty::removeListener);
        localeListenerMap.clear();

        bindingMap.values().stream().flatMap(Collection::stream).forEach(Binding::dispose);
        bindingMap.values().forEach(Set::clear);
        bindingMap.clear();

        propertiesMap.values().stream().flatMap(Collection::stream).forEach(Property::unbind);
        propertiesMap.values().forEach(Set::clear);
        propertiesMap.clear();

        this.localeProperty = null;

        this.resourceBundleMap.clear();
        this.resourceBundleMap = null;

        log.trace("FXMessages is destroyed.");
    }
}
