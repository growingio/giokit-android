package com.growingio.giokit.plugin.transform.saas

import com.growingio.giokit.plugin.transform.*
import com.growingio.giokit.plugin.utils.GioConfig
import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/9/26
 */
class GioKitSaasTransform(androidProject: Project, gioConfig: GioConfig) :
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