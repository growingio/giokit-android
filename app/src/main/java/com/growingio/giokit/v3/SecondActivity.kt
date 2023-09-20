package com.growingio.giokit.v3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.growingio.android.sdk.autotrack.GrowingAutotracker

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val tv = findViewById<TextView>(R.id.track)
        tv.text = "custom track"

        val btn = findViewById<Button>(R.id.custom)
        btn.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("custom")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    fun justislongmethod() {
        GrowingAutotracker.get().trackCustomEvent("longmethod")
    }
}