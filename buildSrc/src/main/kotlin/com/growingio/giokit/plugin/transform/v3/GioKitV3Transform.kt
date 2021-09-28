package com.growingio.giokit.plugin.transform.v3

import com.growingio.giokit.plugin.transform.*
import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/8/18
 */
class GioKitV3Transform(androidProject: Project) : GioKitBaseTransform(androidProject) {

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