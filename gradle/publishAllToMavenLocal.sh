#!/bin/bash

./gradlew clean \
&& ./gradlew :giokit-plugin:publishToMavenLocal \
&& ./gradlew :uikit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit-no-op:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew clean