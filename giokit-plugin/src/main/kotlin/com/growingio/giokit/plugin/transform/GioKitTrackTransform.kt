package com.growingio.giokit.plugin.transform

import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/8/18
 */
class GioKitTrackTransform(androidProject: Project) : GioKitBaseTransform(androidProject) {

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