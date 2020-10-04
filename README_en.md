# ToasterFX [![语言：中文](https://img.shields.io/badge/%E8%AF%AD%E8%A8%80-%E4%B8%AD%E6%96%87-brightgreen)](README.md)

[![JavaFX: 8.0+](https://img.shields.io/badge/javafx-8.0%2B-green)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Build Status](https://travis-ci.com/Mr-Po/toasterfx.svg?branch=master)](https://travis-ci.com/Mr-Po/toasterfx)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gitee.pomo/toasterfx/badge.svg)](https://search.maven.org/#search|ga|1|com.gitee.pomo.toasterfx)
[![Apache License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](LICENSE)

A message prompt library for JavaFX.

## Features
* Support text, Node, FXML
* Support click event, auto close, message list, background popup
* With default/dark theme, custom css
* Support JPMS, Jlink (require Java9+)

For more features and sample demo : [ToasterFX-DEMO](../../../toasterfx-demo).

## Installation
Maven：
```
<dependency>
    <groupId>com.gitee.pomo</groupId>
    <artifactId>toasterfx</artifactId>
    <version>1.0.2</version>
</dependency>
```
Gradle：
```
compile group: 'com.gitee.pomo', name: 'toasterfx', version: '1.0.2'
```


## Usage
```
ToastBarToasterService service = new ToastBarToasterService();
service.initialize();

// you can call this method on any thread.
service.bomb("ToasterFX","Hello ToasterFX !", ToastTypes.INFO);
```

## Screenshot
![0990E73E5E9874011F4714F9AE73E146.gif](https://i.loli.net/2020/09/28/RPShGny2mKedi5r.gif)