package com.growingio.giokit.instant

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.growingio.giokit.launch.db.GioKitEventBean
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

/**
 * <p>
 *
 * @author cpacm 2022/8/11
 */
object InstantEventCache {
    class D {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return "thisRef"
        }
    }

    private val cacheEvent = arrayListOf<GioKitEventBean>()
    private const val INSTANT_BUNDLE_WAIT_TIME = 500L
    private const val INSTANT_DISPLAY_TIME = 15000L
    const val INSTANT_DISPLAY_MAX_COUNT = 8
    private val observers: ArrayList<WeakReference<InstantDataObserver>> = arrayListOf()

    private var instantEventMonitorEnabled = false

    private val instantHandler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                val bundleEvent = arrayListOf<GioKitEventBean>()
                bundleEvent.addAll(cacheEvent)
                cacheEvent.clear()
                if (bundleEvent.size == 2) {
                    dispatchInstantData(arrayListOf(bundleEvent.first()))
                    dispatchInstantData(arrayListOf(bundleEvent.last()), 200L)
                } else {
                    dispatchInstantData(bundleEvent)
                }
            } else if (msg.what == 0) {
                if (msg.obj != null && msg.obj is InstantData) {
                    removeInstantData(msg.obj as InstantData)
                }
            }
        }
    }

    private fun dispatchInstantData(bundleEvent: List<GioKitEventBean>, extraTime: Long = 0L) {
        val instantData = InstantData(bundleEvent)
        observers.forEach {
            it.get()?.dispatchInstantData(instantData)
        }
        val message = Message.obtain()
        message.what = 0
        message.obj = instantData
        instantHandler.sendMessageAtTime(message, SystemClock.uptimeMillis() + INSTANT_DISPLAY_TIME + extraTime)
    }

    fun enableInstantEventMonitor() {
        this.instantEventMonitorEnabled = true

        val fakeGio = GioKitEventBean()
        fakeGio.type = "Start"
        fakeGio.data = "实时事件监测开始"
        fakeGio.gsid = 0
        acceptEvent(fakeGio)
    }

    fun disableInstantEventMonitor() {
        this.instantEventMonitorEnabled = false
    }

    fun isInstantEventMonitorEnable(): Boolean {
        return this.instantEventMonitorEnabled
    }

    fun removeInstantData(data: InstantData) {
        observers.forEach {
            it.get()?.removeInstantData(data)
        }
    }

    fun acceptEvent(event: GioKitEventBean) {
        if (!instantEventMonitorEnabled) return
        cacheEvent.add(event)
        instantHandler.removeMessages(1)
        instantHandler.sendEmptyMessageAtTime(1, SystemClock.uptimeMillis() + INSTANT_BUNDLE_WAIT_TIME)
    }

    fun addInstantObserver(observer: InstantDataObserver) {
        observers.forEach {
            if (it.get() == observer) return
        }
        observers.add(WeakReference(observer))
    }

    fun removeInstantObserver(observer: InstantDataObserver) {
        val filter = observers.filter { it.get() != observer }
        observers.clear()
        observers.addAll(filter)
    }

    data class InstantData(val events: List<GioKitEventBean>, var isVisible: Boolean = true)

    interface InstantDataObserver {
        fun dispatchInstantData(data: InstantData)

        fun removeInstantData(data: InstantData)
    }
}