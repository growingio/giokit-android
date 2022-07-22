package com.growingio.giokit.launch.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * <p>
 *
 * @author cpacm 2020/5/9
 */
class FabScrollBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {

    @Deprecated("Deprecated in Java")
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, directTargetChild: View, target: View, axes: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        if (dyConsumed > 0) { // 向下滑动
            animateOut(child)
        } else if (dyConsumed < 0) { // 向上滑动
            animateIn(child)
        }
    }

    // FAB移出屏幕动画（隐藏动画）
    private fun animateOut(fab: FloatingActionButton) {
        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        val bottomMargin = layoutParams.bottomMargin
        fab.animate().translationY(fab.height + bottomMargin.toFloat()).setInterpolator(LinearInterpolator()).start()
    }

    // FAB移入屏幕动画（显示动画）
    private fun animateIn(fab: FloatingActionButton) {
        fab.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
    }
}