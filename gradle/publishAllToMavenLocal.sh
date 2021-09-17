#!/bin/bash

./gradlew clean \
&& ./gradlew :giokit-plugin:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :uikit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew clean