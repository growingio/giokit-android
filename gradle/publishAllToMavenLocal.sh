#!/bin/bash

./gradlew clean \
&& ./gradlew :uikit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit-no-op:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew :giokit:publishMavenAgentPublicationToMavenLocal \
&& ./gradlew clean