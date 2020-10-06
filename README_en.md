# ToasterFX [![语言：中文](https://img.shields.io/badge/%E8%AF%AD%E8%A8%80-%E4%B8%AD%E6%96%87-blue)](README.md)

[![JavaFX: 8.0+](https://img.shields.io/badge/javafx-8.0%2B-blue)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Build Status](https://travis-ci.com/Mr-Po/toasterfx.svg?branch=master)](https://travis-ci.com/Mr-Po/toasterfx)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/github/Mr-Po/toasterfx?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Mr-Po/toasterfx/context:java)
[![Maven Central](https://img.shields.io/maven-central/v/com.gitee.pomo/toasterfx/1)](https://search.maven.org/#search|ga|1|com.gitee.pomo.toasterfx)

[![License](https://img.shields.io/github/license/Mr-Po/toasterfx?color=blue)](LICENSE)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FMr-Po%2Ftoasterfx.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FMr-Po%2Ftoasterfx?ref=badge_shield)

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