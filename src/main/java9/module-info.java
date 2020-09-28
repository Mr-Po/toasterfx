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
module org.pomo.toasterfx {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.slf4j;

    opens org.pomo.toasterfx.image;
    opens org.pomo.toasterfx.controller to javafx.fxml;

    exports org.pomo.toasterfx;
    exports org.pomo.toasterfx.util;
    exports org.pomo.toasterfx.model;
    exports org.pomo.toasterfx.common;
    exports org.pomo.toasterfx.control;
    exports org.pomo.toasterfx.strategy;
    exports org.pomo.toasterfx.component;
    exports org.pomo.toasterfx.transition;
    exports org.pomo.toasterfx.model.impl;
    exports org.pomo.toasterfx.control.impl;
    exports org.pomo.toasterfx.strategy.impl;
    exports org.pomo.toasterfx.model.scalable;
    exports org.pomo.toasterfx.transition.impl;

    uses org.pomo.toasterfx.util.FXMessageProvider;
}