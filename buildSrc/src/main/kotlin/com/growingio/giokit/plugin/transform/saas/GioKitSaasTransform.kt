package com.growingio.giokit.plugin.transform.saas

import com.growingio.giokit.plugin.transform.*
import org.gradle.api.Project

/**
 * <p>
 *
 * @author cpacm 2021/9/26
 */
class GioKitSaasTransform(androidProject: Project) : GioKitBaseTransform(androidProject) {

    override val transformers = listOf(
        GioDelegateTransformer(
            transformers = listOf(
                GioInjectTransformer(),
                GioCodeTransformer(),
                GioDatabaseTransformer(),
            )
        )
    )
}