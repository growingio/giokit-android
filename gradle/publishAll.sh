#!/bin/bash

./gradlew clean \
&& ./gradlew :uikit:publish \
&& ./gradlew :giokit-no-op:publish \
&& ./gradlew :giokit:publish \
&& ./gradlew clean