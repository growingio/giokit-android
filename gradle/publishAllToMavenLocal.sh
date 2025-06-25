#!/bin/bash

./gradlew clean \
&& ./gradlew :uikit:publishMavenPublicationToMavenLocal \
&& ./gradlew :giokit-no-op:publishMavenPublicationToMavenLocal \
&& ./gradlew :giokit:publishMavenPublicationToMavenLocal \
&& ./gradlew clean