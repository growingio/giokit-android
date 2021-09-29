package com.growingio.giokit.saas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.growingio.android.sdk.collection.GrowingIO

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv = findViewById<TextView>(R.id.track)
        tv.setOnClickListener {
            //startActivity(Intent(this,SecondActivity::class.java))
        }

        val webBtn = findViewById<Button>(R.id.web)
        webBtn.setOnClickListener { startActivity(Intent(this, WebCircleHybridActivity::class.java)) }

        GrowingIO.getInstance().setUserId("cpacm")
        GrowingIO.getInstance().setGeoLocation(100.0, 100.0)
    }

}