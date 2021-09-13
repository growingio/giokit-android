package com.growingio.giokit.circle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.growingio.giokit.R;
import com.growingio.giokit.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@TargetApi(15)
public class CircleAnchorView extends FloatViewContainer {

    final static String TAG = "GrowingIO.FloatView";
    private final int MAX_OVERLAP_EDGE_DISTANCE = 10;
    static int ANCHOR_VIEW_SIZE;
    // calc the point
    private float xInScreen;
    private float yInScreen;
    private float xDownInScreen;
    private float yDownInScreen;
    private float xInView;
    private float yInView;
    private boolean mIsInTouch = false;
    private View mShowingMaskInWebView;

    private Point mLastMovePoint = null;
    private CircleMagnifierView mMagnifierView;
    private CircleExitView mExitView;
    private FloatViewContainer mMaskView;
    private Rect mHitRect;
    private ViewNode mTopsideHitView;
    private Rect mVisibleRectBuffer = new Rect();
    private List<ViewNode> mHitViewNodes = new ArrayList<ViewNode>();
    private View[] mWindowRootViews;
    private int mMinMoveDistance;

    public CircleAnchorView(Context context) {
        super(context);
        init();
    }

    public void init() {
        ANCHOR_VIEW_SIZE = DeviceUtils.dp2Px(getContext(), 48);
        setBackgroundResource(R.drawable.circle_anchor_bg);
        mMinMoveDistance = DeviceUtils.dp2Px(getContext(), 4);
        initMaskView();
        mMagnifierView = new CircleMagnifierView(getContext());
        initExitView();
    }

    private void initExitView() {
        mExitView = new CircleExitView(getContext());
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        WindowManager.LayoutParams exitParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT,
                -1, flags, PixelFormat.TRANSLUCENT);
        exitParams.gravity = Gravity.BOTTOM;
        exitParams.setTitle("ExitWindow:" + getContext().getPackageName());
        FloatWindowManager.getInstance().addView(mExitView, exitParams);

        //mExitView.setOnExitListener(v -> remove());
    }

    @SuppressLint("RtlHardcoded")
    private void initMaskView() {
        mMaskView = new FloatViewContainer(getContext());
        float radius = DeviceUtils.dp2Px(getContext(), 3);
        ShapeDrawable background = new ShapeDrawable(new RoundRectShape(
                new float[]{radius, radius, radius, radius, radius, radius, radius, radius}, null, null
        ));
        background.getPaint().setColor(0X4CFF4824);
        background.getPaint().setStrokeWidth(DeviceUtils.dp2Px(getContext(), 1));
        background.getPaint().setAntiAlias(true);
        mMaskView.setBackgroundDrawable(background);
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        WindowManager.LayoutParams maskParams = new WindowManager.LayoutParams(
                0, 0,
                -1, flags, PixelFormat.TRANSLUCENT);
        maskParams.gravity = Gravity.TOP | Gravity.LEFT;
        maskParams.setTitle("MaskWindow:" + getContext().getPackageName());

        FloatWindowManager.getInstance().addView(mMaskView, maskParams);
    }

    @SuppressLint("RtlHardcoded")
    public void show() {
        if (getParent() == null) {
            Point point = new Point(100, 100);
            boolean portrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            point.x = (DeviceUtils.getWidthPixels(getContext()) - ANCHOR_VIEW_SIZE) / 2;
            point.y = (DeviceUtils.getRealHeightPixels(getContext()) - ANCHOR_VIEW_SIZE) / 2;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    ANCHOR_VIEW_SIZE, ANCHOR_VIEW_SIZE,
                    -1, flags, PixelFormat.TRANSLUCENT);
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.x = portrait ? point.x : point.y;
            layoutParams.y = portrait ? point.y : point.x;
            layoutParams.setTitle("AnchorWindow:" + getContext().getPackageName());
            FloatWindowManager.getInstance().addView(this, layoutParams);
            mMagnifierView.attachToWindow();
        } else {
            setVisibility(VISIBLE);
            bringToFront();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();

                mIsInTouch = true;
                mWindowRootViews = WindowHelper.getSortedWindowViews();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsInTouch) {
                    xInScreen = event.getRawX();
                    yInScreen = event.getRawY();
                    if (Math.abs(xInScreen - xDownInScreen) < mMinMoveDistance
                            && Math.abs(yInScreen - yDownInScreen) < mMinMoveDistance) {
                        break;
                    }
                    updateViewPosition();
                    Rect rect = new Rect();
                    getGlobalVisibleRect(rect);
                    if (!rect.contains((int) xDownInScreen, (int) yDownInScreen)) {
                        mLastMovePoint = new Point((int) xInScreen, (int) yInScreen);
                        findTopsideHitView();
                    } else {
                        mMagnifierView.setVisibility(GONE);
                    }
                    handled = true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mIsInTouch = false;
                mMaskView.setVisibility(GONE);
                mMagnifierView.setVisibility(GONE);
                mMaskView.getLayoutParams().width = 0;
                hideMaskInWebView();

                if (mHitRect != null) {
                    mHitViewNodes.clear();
                    reverseArray(mWindowRootViews);
                    ViewHelper.traverseWindows(mWindowRootViews, mTraverseMask);
                    if (mHitViewNodes.size() > 0) {
                        View first = mHitViewNodes.get(0).mView;
                        if (first instanceof WebView) {
                            findElementAt(first);
                        } else {
                            Collections.sort(mHitViewNodes, mViewNodeComparator);
                            setCircleInfo(mHitViewNodes);

                            //showEventDetailDialog("elem", mHitViewNodes);
                        }
                    }
                    mHitRect = null;
                    handled = true;
                } else {
                    if (Math.abs(xInScreen - xDownInScreen) < mMinMoveDistance
                            && Math.abs(yInScreen - yDownInScreen) < mMinMoveDistance) {
                        performClick();
                    }
                }
                mWindowRootViews = null;

                break;
            default:
                break;
        }
        return handled;
    }

    public void setCircleInfo(List<ViewNode> nodes) {
        StringBuilder sb = new StringBuilder();
        if (nodes.size() > 0) {
            ViewNode node = nodes.get(0);
            try {
                sb.append("当前：").append(node.mViewName).append("\n")
                        .append("内容：").append(node.mViewContent == null ? "未定义" : node.mViewContent).append("\n");
                if (node.mHasListParent) {
                    sb.append("列表：").append(node.mHasListParent ? "是" : "否").append("\n")
                            .append("位置：").append(node.mViewPosition).append("\n");
                }
                sb.append("xpath：").append(node.mParentXPath.toString()).append("\n");
            } catch (Exception e) {

            }
        }
        post(() -> mExitView.setNodeInfo(sb.toString()));
    }

    private void reverseArray(Object[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        for (int i = 0; i < array.length / 2; i++) {
            Object temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    // update this view position
    private void updateViewPosition() {
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
        int x = (int) (xInScreen - xInView);
        int y = (int) (yInScreen - yInView);

        // check the point is out edge
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        boolean portrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        int maxWidth = (portrait ? DeviceUtils.getWidthPixels(getContext()) : DeviceUtils.getRealHeightPixels(getContext())) - getWidth();
        int maxHeight = (portrait ? DeviceUtils.getRealHeightPixels(getContext()) : DeviceUtils.getWidthPixels(getContext())) - getHeight();
        if (x > maxWidth) x = maxWidth;
        if (y > maxHeight) y = maxHeight;
        if (y < 0) y = 0;

        params.x = x;
        params.y = y;
        FloatWindowManager.getInstance().updateViewLayout(this, params);
    }

    private Comparator<ViewNode> mViewNodeComparator = (lhs, rhs) -> {
        int l = lhs.mView instanceof AdapterView ? -1 : 1;
        int r = rhs.mView instanceof AdapterView ? -1 : 1;
        return r - l;
    };

    // the first traverse, used to get the topside view
    private ViewTraveler mTraverseHover = new ViewTraveler() {
        @Override
        public boolean needTraverse(ViewNode viewNode) {
            if (ViewHelper.isViewSelfVisible(viewNode.mView)) {
                getVisibleRectOnScreen(viewNode.mView, mVisibleRectBuffer, viewNode.mFullScreen, null);
                return mVisibleRectBuffer.contains(mLastMovePoint.x, mLastMovePoint.y);
            }
            return false;
        }

        @Override
        public void traverseCallBack(ViewNode viewNode) {
            boolean isClickable = ClassExistHelper.isViewClickable(viewNode.mView);
            if (!isClickable && (viewNode.mInClickableGroup || TextUtils.isEmpty(viewNode.mViewContent))) {
                return;
            }
            mHitViewNodes.add(0, viewNode);
        }
    };

    public static void getVisibleRectOnScreen(View view, Rect rect, boolean ignoreOffset, int[] screenLocation) {
        if (ignoreOffset) {
            view.getGlobalVisibleRect(rect);
        } else {
            if (screenLocation == null || screenLocation.length != 2) {
                screenLocation = new int[2];
            }
            view.getLocationOnScreen(screenLocation);
            rect.set(0, 0, view.getWidth(), view.getHeight());
            rect.offset(screenLocation[0], screenLocation[1]);
        }
    }

    // the second traverse, used to get all views under the topside
    private ViewTraveler mTraverseMask = new ViewTraveler() {

        @Override
        public void traverseCallBack(ViewNode viewNode) {
            boolean isClickable = ClassExistHelper.isViewClickable(viewNode.mView);
            if (!isClickable && (viewNode.mInClickableGroup || TextUtils.isEmpty(viewNode.mViewContent))) {
                return;
            }
            getVisibleRectOnScreen(viewNode.mView, mVisibleRectBuffer, viewNode.mFullScreen, null);
            if (isFuzzyContainRect(mHitRect, mVisibleRectBuffer)) {
                mHitViewNodes.add(0, viewNode);
            }
        }
    };

    private boolean isFuzzyContainRect(Rect parent, Rect child) {
        return parent.contains(child) && parent.width() - child.width() < MAX_OVERLAP_EDGE_DISTANCE
                && parent.height() - child.height() < MAX_OVERLAP_EDGE_DISTANCE;
    }

    private void findTopsideHitView() {
        mTopsideHitView = null;
        mHitRect = null;
        mHitViewNodes.clear();
        ViewHelper.traverseWindows(mWindowRootViews, mTraverseHover);
        updateMaskViewPosition();
    }

    private void updateMaskViewPosition() {
        if (mHitViewNodes.size() > 0) {
            mTopsideHitView = mHitViewNodes.get(0);
            mHitRect = new Rect();
            getVisibleRectOnScreen(mTopsideHitView.mView, mHitRect, mTopsideHitView.mFullScreen, null);
            if (mTopsideHitView.mView instanceof WebView) {
                mMaskView.setVisibility(GONE);
                mMagnifierView.setVisibility(GONE);
                int[] loc = new int[2];
                mTopsideHitView.mView.getLocationOnScreen(loc);
                hoverOn(mTopsideHitView.mView, xInScreen - loc[0], yInScreen - loc[1]);
                mShowingMaskInWebView = mTopsideHitView.mView;
            } else {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) mMaskView.getLayoutParams();
                mMaskView.setVisibility(VISIBLE);
                if (mHitRect.left != params.x
                        || mHitRect.top != params.y
                        || mHitRect.width() != params.width
                        || mHitRect.height() != params.height) {
                    params.width = mHitRect.width();
                    params.height = mHitRect.height();
                    params.x = mHitRect.left;
                    params.y = mHitRect.top;

                    FloatWindowManager.getInstance().removeView(mMaskView);
                    FloatWindowManager.getInstance().addView(mMaskView, params);
                }
                mMagnifierView.showIfNeed(mHitRect, (int) (xInScreen - xInView + ANCHOR_VIEW_SIZE / 2), (int) (yInScreen - yInView + ANCHOR_VIEW_SIZE / 2));
            }
        } else {
            mMaskView.setVisibility(GONE);
            mMaskView.getLayoutParams().width = 0;
            mMagnifierView.setVisibility(GONE);
            hideMaskInWebView();
        }
    }

    public void hoverOn(View webView, float x, float y) {
        ViewHelper.callJavaScript(webView, "_vds_hybrid.hoverOn", x, y);
    }

    public void findElementAt(View webView) {
        ViewHelper.callJavaScript(webView, "_vds_hybrid.findElementAtPoint");
        mShowingMaskInWebView = webView;
    }

    public void hideMaskInWebView() {
        if (mShowingMaskInWebView != null) {
            ViewHelper.callJavaScript(mShowingMaskInWebView, "_vds_hybrid.cancelHover");
            mShowingMaskInWebView = null;
        }
    }

    public void remove() {
        FloatWindowManager.getInstance().removeView(this);
        FloatWindowManager.getInstance().removeView(mMaskView);
        FloatWindowManager.getInstance().removeView(mExitView);
        if (mMagnifierView != null) {
            mMagnifierView.remove();
        }
    }

    public boolean isMoving() {
        return mIsInTouch;
    }
}
