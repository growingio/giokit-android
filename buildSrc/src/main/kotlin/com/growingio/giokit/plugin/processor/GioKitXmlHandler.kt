package com.growingio.giokit.plugin.processor

import com.growingio.giokit.plugin.utils.GioConfigUtils
import com.growingio.giokit.plugin.utils.println
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

const val ATTR_NAME = "android:name"

const val MANIFEST_ATTR_NAME = "package"

class GioKitXmlHandler : DefaultHandler() {
    var appPackageName: String = ""
    val applications = mutableSetOf<String>()
    val activities = mutableSetOf<String>()
    val services = mutableSetOf<String>()
    val providers = mutableSetOf<String>()
    val receivers = mutableSetOf<String>()

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        val name: String = attributes.getValue(ATTR_NAME) ?: ""
        val packageName: String = attributes.getValue(MANIFEST_ATTR_NAME) ?: ""
        when (qName) {
            "manifest" -> {
                appPackageName = packageName
            }
            "application" -> {
                applications.add(name)
            }
            "activity" -> {
                activities.add(name)
            }
            "service" -> {
                services.add(name)
            }
            "provider" -> {
                providers.add(name)
            }
            "receiver" -> {
                receivers.add(name)
            }
            "data" -> {
                attributes.getValue("android:scheme")?.let {
                    if (it.startsWith("growing")) GioConfigUtils.xmlScheme = it
                }
            }

        }
    }

}