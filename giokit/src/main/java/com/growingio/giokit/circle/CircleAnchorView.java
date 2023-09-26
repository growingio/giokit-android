package com.growingio.giokit.circle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import com.growingio.android.sdk.autotrack.GrowingAutotracker;
import com.growingio.android.sdk.autotrack.view.ViewNode;
import com.growingio.android.sdk.autotrack.view.ViewNodeProvider;
import com.growingio.android.sdk.track.view.DecorView;
import com.growingio.giokit.R;
import com.growingio.giokit.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.mattcarroll.hover.HoverView;

public class CircleAnchorView extends FloatViewContainer {

    final static String TAG = "GrowingIO.FloatView";
    static int ANCHOR_VIEW_SIZE;
    // calc the point
    private float xDownInScreen;
    private float yDownInScreen;
    private float xInView;
    private float yInView;
    private boolean mIsInTouch = false;
    private View mShowingMaskInWebView;

    private CircleMagnifierView mMagnifierView;
    private CircleExitView mExitView;
    private FloatViewContainer mMaskView;
    private int mMinMoveDistance;

    private final List<View> windowDecorViews = new ArrayList<>();

    public CircleAnchorView(Context context) {
        super(context);
        init();
    }

    public void init() {
        ANCHOR_VIEW_SIZE = DeviceUtils.dp2Px(getContext(), 48);
        setBackgroundResource(R.drawable.giokit_circle_anchor_bg);
        mMinMoveDistance = DeviceUtils.dp2Px(getContext(), 8);
        initMaskView();
        mMagnifierView = new CircleMagnifierView(getContext());
        initExitView();
    }

    private final List<ViewNode> hitViewNodes = new ArrayList<>();
    private ViewNode targetNode = null;
    private final Rect visibleRectBuffer = new Rect();

    private void initDecorViews() {
        windowDecorViews.clear();
        List<DecorView> decorViews = com.growingio.android.sdk.track.view.WindowHelper.get().getTopActivityViews();
        for (DecorView decorView : decorViews) {
            if (decorView.getView().getClass() == HoverView.class) {
                continue;
            }
            if (decorView.getView().getClass() == CircleAnchorView.class) {
                continue;
            }
            if (decorView.getView().getClass() == CircleExitView.class) {
                continue;
            }
            if (decorView.getView().getClass() == CircleMagnifierView.class) {
                continue;
            }
            if (decorView.getView().getClass() == FloatViewContainer.class) {
                continue;
            }
            windowDecorViews.add(decorView.getView());
        }

        if (!mIsInTouch) return;
        hitViewNodes.clear();
        ViewNodeProvider viewNodeProvider = GrowingAutotracker.get().getContext().getProvider(ViewNodeProvider.class);
        for (int i = 0; i < windowDecorViews.size(); i++) {
            List<ViewNode> viewNodes = viewNodeProvider.findViewNodesWithinCircle(windowDecorViews.get(i));
            hitViewNodes.addAll(viewNodes);
        }
        Collections.sort(hitViewNodes, viewNodeXPathComparator);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                mIsInTouch = true;
                initDecorViews();
                break;
            case MotionEvent.ACTION_MOVE:
                float xInScreen = event.getRawX();
                float yInScreen = event.getRawY();
                if (mIsInTouch) {
                    if (Math.abs(xInScreen - xDownInScreen) < mMinMoveDistance
                            && Math.abs(yInScreen - yDownInScreen) < mMinMoveDistance) {
                        break;
                    }
                    updateViewPosition(xInScreen, yInScreen);

                    Rect rect = new Rect();
                    getGlobalVisibleRect(rect);
                    if (!rect.contains((int) xDownInScreen, (int) yDownInScreen)) {
                        findNodeViewWithMove((int) xInScreen, (int) yInScreen);
                    } else {
                        mMagnifierView.setVisibility(GONE);
                    }
                    handled = true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsInTouch = false;
                findNodeViewWithUp((int) event.getRawX(), (int) event.getRawY());
                handled = true;
                break;
            default:
                break;
        }
        return handled;
    }

    public void setCircleInfo(ViewNode node) {
        setCircleInfo(node, null);
    }

    public void setCircleInfo(ViewNode node, String url) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int start = 0;
        ssb.append("当前：").append(url == null ? node.getView().getClass().getSimpleName() : url).append("\n");
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.hover_mask)), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = ssb.length();
        ssb.append("内容：").append(TextUtils.isEmpty(node.getViewContent()) ? "未定义" : node.getViewContent().length() > 120 ? node.getViewContent().substring(0, 120) + "..." : node.getViewContent()).append("\n");
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.hover_mask)), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = ssb.length();
        if (node.getViewPosition() != -1) {
            ssb.append("列表序号：").append(String.valueOf(node.getViewPosition())).append("\n");
            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.hover_mask)), start, start + 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = ssb.length();
        }
        ssb.append("路径：").append(node.getXPath()).append("\n");
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.hover_mask)), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = ssb.length();
        if (node.getXIndex() != null) {
            ssb.append("位置：").append(node.getXIndex()).append("\n");
            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.hover_mask)), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        post(() -> mExitView.setNodeInfo(ssb));
    }

    // update this view position
    private void updateViewPosition(float xInScreen, float yInScreen) {
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

    private void findNodeViewWithMove(int x, int y) {
        if (hitViewNodes.isEmpty()) return;
        targetNode = null;
        for (ViewNode viewNode : hitViewNodes) {
            getVisibleRectOnScreen(viewNode.getView(), visibleRectBuffer, false, null);
            boolean pointerInRect = visibleRectBuffer.contains(x, y);
            if (!pointerInRect) continue;
            targetNode = viewNode;
            break;
        }
        if (targetNode != null) {
            if (targetNode.getView() instanceof WebView) {
                callWebViewHoverOn(targetNode, x, y);
            } else {
                showMaskView(targetNode, x, y);
            }
        } else {
            disableMaskView();
        }
    }

    private void findNodeViewWithUp(int x, int y) {
        disableMaskView();
        if (targetNode == null) {
            return;
        }
        if (targetNode.getView() instanceof WebView) {
            findElementAt(targetNode.getView());
        } else {
            //showMaskView(targetNode, x, y);
            setCircleInfo(targetNode);
        }
    }


    private void callWebViewHoverOn(ViewNode viewNode, int x, int y) {
        mMaskView.setVisibility(GONE);
        mMagnifierView.setVisibility(GONE);
        int[] loc = new int[2];
        viewNode.getView().getLocationOnScreen(loc);
        hoverOn(viewNode.getView(), x - loc[0], y - loc[1]);
        mShowingMaskInWebView = viewNode.getView();
    }

    private void showMaskView(ViewNode viewNode, int xInScreen, int yInScreen) {
        Rect viewRect = new Rect();
        getVisibleRectOnScreen(viewNode.getView(), viewRect, false, null);

        WindowManager.LayoutParams params = (WindowManager.LayoutParams) mMaskView.getLayoutParams();
        mMaskView.setVisibility(VISIBLE);
        if (viewRect.left != params.x
                || viewRect.top != params.y
                || viewRect.width() != params.width
                || viewRect.height() != params.height) {
            params.width = viewRect.width();
            params.height = viewRect.height();
            params.x = viewRect.left;
            params.y = viewRect.top;

            FloatWindowManager.getInstance().removeView(mMaskView);
            FloatWindowManager.getInstance().addView(mMaskView, params);
        }
        mMagnifierView.showIfNeed(viewRect, (int) (xInScreen - xInView + ANCHOR_VIEW_SIZE / 2), (int) (yInScreen - yInView + ANCHOR_VIEW_SIZE / 2));
    }

    private void disableMaskView() {
        mMaskView.setVisibility(GONE);
        mMaskView.getLayoutParams().width = 0;
        mMagnifierView.setVisibility(GONE);
        hideMaskInWebView();
    }

    private final Comparator<ViewNode> viewNodeXPathComparator = (lhs, rhs) -> {
        int l = lhs.getXPath().length();
        int r = rhs.getXPath().length();
        return r - l;
    };

    public void hoverOn(View webView, float x, float y) {
        ViewHelper.callJavaScript(webView, "GiokitTouchJavascriptBridge.hoverOn", webView.getWidth(), x, y);
        mShowingMaskInWebView = webView;
    }

    public void findElementAt(View webView) {
        ViewHelper.callJavaScript(webView, "GiokitTouchJavascriptBridge.highLightElementAtPoint");
        mShowingMaskInWebView = webView;
    }

    public void hideMaskInWebView() {
        if (mShowingMaskInWebView != null) {
            ViewHelper.callJavaScript(mShowingMaskInWebView, "GiokitTouchJavascriptBridge.cancelHover");
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
        hideMaskInWebView();
    }

    public boolean isMoving() {
        return mIsInTouch;
    }
}
