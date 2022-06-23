#!/bin/bash

./gradlew clean \
&& ./gradlew :giokit-plugin:publish \
&& ./gradlew :uikit:publish \
&& ./gradlew :giokit-no-op:publish \
&& ./gradlew :giokit:publish \
&& ./gradlew clean