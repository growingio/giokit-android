package com.growingio.giokit.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.growingio.android.sdk.autotrack.GrowingAutotracker

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.track)
        tv.text = "track"
        tv.setOnClickListener {
            GrowingAutotracker.get().trackCustomEvent("custom")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    fun justislongmethod123llllloooonnnnngggggggoooonnnnngggggggnonog(){
        GrowingAutotracker.get().trackCustomEvent("longmethod")
        GrowingAutotracker.get().trackCustomEvent("longmethod")
        GrowingAutotracker.get().trackCustomEvent("longmethod")
    }
}