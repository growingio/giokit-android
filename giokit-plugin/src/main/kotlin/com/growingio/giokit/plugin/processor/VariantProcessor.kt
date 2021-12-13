package com.growingio.giokit.plugin.processor

import com.android.build.gradle.api.BaseVariant

/**
 * <p>
 *
 * @author cpacm 2021/12/10
 */
interface VariantProcessor {
    fun process(variant: BaseVariant)
}