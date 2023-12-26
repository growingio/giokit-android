package com.growingio.giokit.setting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.growingio.giokit.R
import com.growingio.giokit.launch.LaunchPage
import com.growingio.giokit.launch.LaunchPage.LAUNCH_FRAGMENT_INDEX
import com.growingio.giokit.launch.LaunchPage.LAUNCH_FRAGMENT_TITLE
import com.growingio.giokit.utils.CheckSdkStatusManager

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
//@GrowingIOPageIgnore
class GiokitSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giokit_base)
        val bundle = intent.extras
        if (bundle == null) {
            finish()
            return
        }
        val index = bundle.getInt(LAUNCH_FRAGMENT_INDEX)
        if (index == 0) {
            finish()
            return
        }

        val mFragmentClass = LaunchPage.getSettingPageClass(index)
        if (mFragmentClass == null) {
            finish()
            Toast.makeText(
                this,
                String.format("fragment index %s not found", index),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        showContent(mFragmentClass, bundle)
    }

    fun showContent(target: Class<out PreferenceFragmentCompat>, bundle: Bundle? = null) {

        val closeIv = findViewById<View>(R.id.close)
        CheckSdkStatusManager.getInstance().ignoreViewClick(closeIv)
        closeIv.setOnClickListener {
            onBackPressed()
        }

        val titleTv = findViewById<TextView>(R.id.title)
        titleTv.setText(bundle?.getInt(LAUNCH_FRAGMENT_TITLE)?:R.string.giokit_title_default)

        try {
            val fragment = target.newInstance()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            val fm = supportFragmentManager
            val fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.replace(R.id.contentContainer, fragment)
            fragmentTransaction.commit()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}