package com.growingio.giokit.v3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.growingio.android.sdk.autotrack.GrowingAutotracker
import com.growingio.giokit.utils.NotificationUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //(application as App).initGioSdk()

        val tv = findViewById<TextView>(R.id.track)
        tv.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        val webBtn = findViewById<Button>(R.id.web)
        webBtn.setOnClickListener { startActivity(Intent(this, WebCircleHybridActivity::class.java)) }

        val customBtn = findViewById<Button>(R.id.custom)
        customBtn.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("giokit")
            //GrowingAutotracker.get().setLoginUserId("cpacm")
            //GrowingAutotracker.get().setLocation(100.0, 100.0)
        }

        val anrBtn = findViewById<Button>(R.id.anr)
        anrBtn.setOnClickListener {
            it.postDelayed({
                try {
                    Thread.sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }, 1000)
        }
        val notifyBtn = findViewById<Button>(R.id.notify)
        notifyBtn.setOnClickListener {

            NotificationUtils.notify(1) {
                it.setContentText("这是测试通知,内容为空")
                    .setContentTitle("Test")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build()
            }
        }

        val crashBtn = findViewById<Button>(R.id.crash)
        crashBtn.setOnClickListener {
            throw RuntimeException("crash from cpacm.")
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}