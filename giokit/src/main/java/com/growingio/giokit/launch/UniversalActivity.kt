package com.growingio.giokit.launch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.growingio.giokit.R
import com.growingio.giokit.launch.LaunchPage.LAUNCH_FRAGMENT_INDEX
import com.growingio.giokit.utils.SdkV3InfoUtils
import java.util.ArrayDeque

/**
 * <p>
 *
 * @author cpacm 2021/8/26
 */
//@GrowingIOPageIgnore
class UniversalActivity : AppCompatActivity() {

    private val mFragments = ArrayDeque<BaseFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.giokit_universal_title)
        SdkV3InfoUtils.ignoreActivity(this)
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

        val mFragmentClass = LaunchPage.getPageClass(index)
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

    fun showContent(target: Class<out BaseFragment>, bundle: Bundle? = null) {
        try {
            val fragment = target.newInstance()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            val fm = supportFragmentManager
            val fragmentTransaction = fm.beginTransaction()
            fragmentTransaction.add(android.R.id.content, fragment)
            mFragments.push(fragment)
            fragmentTransaction.addToBackStack("")
            fragmentTransaction.commit()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }


    override fun onBackPressed() {
        if (!mFragments.isEmpty()) {
            val fragment = mFragments.first
            if (!fragment.onBackPressed()) {
                mFragments.removeFirst()
                super.onBackPressed()
                if (mFragments.isEmpty()) {
                    finish()
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    fun doBack(fragment: BaseFragment) {
        if (mFragments.contains(fragment)) {
            mFragments.remove(fragment)
            val fm = supportFragmentManager
            fm.popBackStack()
            if (mFragments.isEmpty()) {
                finish()
            }
        }
    }
}