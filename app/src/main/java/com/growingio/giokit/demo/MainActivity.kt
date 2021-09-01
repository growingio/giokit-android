package com.growingio.giokit.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.growingio.android.sdk.autotrack.GrowingAutotracker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.track)
        tv.setOnClickListener {
            startActivity(Intent(this,SecondActivity::class.java))
        }

        GrowingAutotracker.get().setLoginUserId("cpacm")
        GrowingAutotracker.get().setLocation(100.0, 100.0)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}