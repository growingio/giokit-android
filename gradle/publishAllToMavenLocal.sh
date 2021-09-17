#!/bin/bash

./gradlew clean \
&& ./gradlew :giokit-plugin:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :uikit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit-no-op:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew clean