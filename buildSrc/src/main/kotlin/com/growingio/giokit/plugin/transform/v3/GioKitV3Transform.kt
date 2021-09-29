package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.transform.*
import com.growingio.giokit.plugin.utils.GioConfig
import org.gradle.api.Project

class GioKitV3Transform(androidProject: Project, gioConfig: GioConfig) :
    GioKitBaseTransform(androidProject, gioConfig) {

    override val transformers = listOf(
        GioDelegateTransformer(
            transformers = listOf(
                GioInjectTransformer(),
                GioCodeTransformer(),
                GioDatabaseTransformer(),
                GioWebViewTransformer()
            )
        )
    )
}