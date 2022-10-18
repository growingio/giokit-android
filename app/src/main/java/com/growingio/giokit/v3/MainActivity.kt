package com.growingio.giokit.v3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.growingio.android.sdk.autotrack.GrowingAutotracker

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

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}