# ToasterFX [![language: English](https://img.shields.io/badge/language-English-brightgreen)](README_en.md)
[![JavaFX: 8.0+](https://img.shields.io/badge/javafx-8.0%2B-green)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Build Status](https://travis-ci.com/Mr-Po/toasterfx.svg?branch=master)](https://travis-ci.com/Mr-Po/toasterfx)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gitee.pomo/toasterfx/badge.svg)](https://search.maven.org/#search|ga|1|com.gitee.pomo.toasterfx)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](LICENSE)

JavaFX 的一个消息提示库。

## 特性
* 支持 文本消息、自定义Node、自定义FXML
* 支持 点击事件、自动关闭、消息列表、后台弹出
* 自带 默认/暗黑主题，可自定义CSS
* 支持 JPMS、Jlink (需要 Java9+)

更多特性及样例请访问：ToasterFX-DEMO。

## 安装
Maven：
```
<dependency>
    <groupId>com.gitee.pomo</groupId>
    <artifactId>toasterfx</artifactId>
    <version>1.0</version>
</dependency>
```
Gradle：
```
compile group: 'com.gitee.pomo', name: 'toasterfx', version: '1.0'
```

## 使用
```
ToastBarToasterService service = new ToastBarToasterService();
service.initialize();

// 你可以在任何线程中，调用此方法。
service.bomb("ToasterFX","Hello ToasterFX !", ToastTypes.INFO);
```

## 截图
![0990E73E5E9874011F4714F9AE73E146.gif](https://i.loli.net/2020/09/28/RPShGny2mKedi5r.gif)