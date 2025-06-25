#!/bin/bash

./gradlew clean \
&& ./gradlew :uikit:publishToMavenCentral \
&& ./gradlew :giokit-no-op:publishToMavenCentral \
&& ./gradlew :giokit:publishToMavenCentral \
&& ./gradlew clean