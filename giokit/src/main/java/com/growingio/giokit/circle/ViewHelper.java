package com.growingio.giokit.circle;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.growingio.giokit.GioKitImpl;

import java.util.ArrayList;

import io.mattcarroll.hover.HoverView;

/**
 * Created by xyz on 15/11/3.
 */
public class ViewHelper {
    private final static String TAG = "GIO.ViewHelper";

    public static boolean isWindowNeedTraverse(View root, String prefix, boolean skipOtherActivity) {
        if (GioKitImpl.curActivity.get() != null && root.hashCode() == GioKitImpl.curActivity.get().getWindow().getDecorView().hashCode())
            return true;
        return !(root instanceof FloatViewContainer
                || !(root instanceof ViewGroup)
                || root instanceof HoverView
                || (skipOtherActivity
                && (root.getWindowVisibility() == View.GONE
                || root.getVisibility() != View.VISIBLE
                || TextUtils.equals(prefix, WindowHelper.getMainWindowPrefix())
                || root.getWidth() == 0
                || root.getHeight() == 0))
        );
    }

    public static int getMainWindowCount(View[] windowRootViews) {
        int mainWindowCount = 0;
        WindowHelper.init();
        for (View windowRootView : windowRootViews) {
            if (windowRootView == null) continue;
            String prefix = WindowHelper.getWindowPrefix(windowRootView);
            mainWindowCount += prefix.equals(WindowHelper.getMainWindowPrefix()) ? 1 : 0;
        }
        return mainWindowCount;
    }

    public static void traverseWindows(View[] windowRootViews, ViewTraveler traverseHover) {
        boolean skipOtherActivity = getMainWindowCount(windowRootViews) > 1;
        WindowHelper.init();
        try {
            for (View windowRootView : windowRootViews) {
                View root = windowRootView;
                String prefix = WindowHelper.getWindowPrefix(root);
                if (isWindowNeedTraverse(root, prefix, skipOtherActivity)) {
                    traverseWindow(root, prefix, traverseHover);
                }
            }
        } catch (OutOfMemoryError ignored) {
        }
    }

    public static void traverseWindow(View rootView, String windowPrefix, ViewTraveler callBack) {
        if (rootView == null) {
            return;
        }
        int[] offset = new int[2];
        rootView.getLocationOnScreen(offset);
        boolean fullscreen = (offset[0] == 0 && offset[1] == 0);
        ViewNode rootNode = new ViewNode(rootView, 0, -1, ClassExistHelper.isListView(rootView), fullscreen, false, false,
                LinkedString.fromString(windowPrefix),
                LinkedString.fromString(windowPrefix), windowPrefix, callBack);
        if (rootNode.isNeedTrack()) {
            // TODO: 2018/12/17 没太明白这里是为了防止什么， 不敢改
            if (!WindowHelper.isDecorView(rootView)) {
                rootNode.traverseViewsRecur();
            } else {
                rootNode.traverseChildren();
            }
        }
    }

    public static ViewNode getClickViewNode(View view) {
        if (view == null) {
            return null;
        }
        ViewNode viewNode = getViewNode(view, sClickTraveler);

        if (viewNode == null) {
            return null;
        }

        sClickTraveler.resetActionStructList();
        sClickTraveler.traverseCallBack(viewNode);
        viewNode.traverseChildren();

        return viewNode;
    }

    public static boolean isViewSelfVisible(View mView) {
        if (mView == null || mView.getWindowVisibility() == View.GONE) {
            return false;
        }

        // home键back后, DecorView的visibility是 INVISIBLE, 即onResume时Window并不可见, 对GIO而言此时是可见的
        if (WindowHelper.isDecorView(mView))
            return true;

        if (!(mView.getWidth() > 0
                && mView.getHeight() > 0
                && mView.getAlpha() > 0
                && mView.getLocalVisibleRect(new Rect()))) {
            return false;
        }

        //动画导致用户可见但是仍然 invisible,
        if (mView.getVisibility() != View.VISIBLE
                && mView.getAnimation() != null
                && mView.getAnimation().getFillAfter()) {
            return true;
        } else return mView.getVisibility() == View.VISIBLE;

    }


    public static boolean viewVisibilityInParents(View view) {
        if (view == null)
            return false;

        if (!isViewSelfVisible(view))
            return false;

        ViewParent viewParent = view.getParent();
        while (viewParent instanceof View) {
            if (isViewSelfVisible((View) viewParent)) {
                viewParent = viewParent.getParent();
                if (viewParent == null) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }


    private static boolean shouldChangeOn(View view, ViewNode viewNode) {
        return false;
    }


    public static ViewNode getViewNode(View view, @Nullable ViewTraveler viewTraveler) {
        ArrayList<View> viewTreeList = new ArrayList<View>(8);
        ViewParent parent = view.getParent();
        viewTreeList.add(view);
        /*
         * view:
         * "AppCompactButton[2]#login_btn
         * parents:
         * ["LinearLayout[3]#login_container" ,"RelativeLayout[1]", ,"FrameLayout[0]#content", "PhoneWindow$DecorView"]
         */
        while (parent instanceof ViewGroup) {
            viewTreeList.add((ViewGroup) parent);
            parent = parent.getParent();
        }

        int endIndex = viewTreeList.size() - 1;
        View rootView = viewTreeList.get(endIndex);
        WindowHelper.init();

        String bannerText = null;
        String inheritableObjInfo = null;

        int viewPosition = 0;
        int listPos = -1;
        boolean mHasListParent = false;
        boolean mParentIdSettled = false;
        String prefix = WindowHelper.getWindowPrefix(rootView);
        String opx = prefix;
        String px = prefix;

        if (!WindowHelper.isDecorView(rootView) && !(rootView.getParent() instanceof View)) {
            opx += "/" + rootView.getClass().getSimpleName();
            px = opx;
        }
        if (rootView instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) rootView;
            for (int i = endIndex - 1; i >= 0; i--) {
                viewPosition = 0;
                View childView = viewTreeList.get(i);
                String viewName = childView.getClass().getSimpleName();
                viewPosition = parentView.indexOfChild(childView);
                if (ClassExistHelper.instanceOfAndroidXViewPager(parentView)) {
                    viewPosition = ((androidx.viewpager.widget.ViewPager) parentView).getCurrentItem();
                    mHasListParent = true;
                } else if (ClassExistHelper.instanceOfSupportViewPager(parentView)) {
                    viewPosition = ((ViewPager) parentView).getCurrentItem();
                    mHasListParent = true;
                } else if (parentView instanceof AdapterView) {
                    AdapterView listView = (AdapterView) parentView;
                    viewPosition = listView.getFirstVisiblePosition() + viewPosition;
                    mHasListParent = true;
                } else if (ClassExistHelper.instanceOfRecyclerView(parentView)) {
                    int adapterPosition = getChildAdapterPositionInRecyclerView(childView, parentView);
                    if (adapterPosition >= 0) {
                        mHasListParent = true;
                        viewPosition = adapterPosition;
                    }
                }
                if (parentView instanceof ExpandableListView) {
                    ExpandableListView listParent = (ExpandableListView) parentView;
                    long elp = listParent.getExpandableListPosition(viewPosition);
                    if (ExpandableListView.getPackedPositionType(elp) == ExpandableListView.PACKED_POSITION_TYPE_NULL) {
                        if (viewPosition < listParent.getHeaderViewsCount()) {
                            opx = opx + "/ELH[" + viewPosition + "]/" + viewName + "[0]";
                            px = px + "/ELH[" + viewPosition + "]/" + viewName + "[0]";
                        } else {
                            int footerIndex = viewPosition - (listParent.getCount() - listParent.getFooterViewsCount());
                            opx = opx + "/ELF[" + footerIndex + "]/" + viewName + "[0]";
                            px = px + "/ELF[" + footerIndex + "]/" + viewName + "[0]";
                        }
                    } else {
                        int groupIdx = ExpandableListView.getPackedPositionGroup(elp);
                        int childIdx = ExpandableListView.getPackedPositionChild(elp);
                        if (childIdx != -1) {
                            listPos = childIdx;
                            px = opx + "/ELVG[" + groupIdx + "]/ELVC[-]/" + viewName + "[0]";
                            opx = opx + "/ELVG[" + groupIdx + "]/ELVC[" + childIdx + "]/" + viewName + "[0]";
                        } else {
                            listPos = groupIdx;
                            px = opx + "/ELVG[-]/" + viewName + "[0]";
                            opx = opx + "/ELVG[" + groupIdx + "]/" + viewName + "[0]";
                        }
                    }
                } else if (ClassExistHelper.isListView(parentView)) {
                    listPos = viewPosition;
                    px = opx + "/" + viewName + "[-]";
                    opx = opx + "/" + viewName + "[" + listPos + "]";
                } else if (ClassExistHelper.instanceofAndroidXSwipeRefreshLayout(parentView)
                        || ClassExistHelper.instanceOfSupportSwipeRefreshLayout(parentView)) {
                    opx = opx + "/" + viewName + "[0]";
                    px = px + "/" + viewName + "[0]";
                } else {
                    opx = opx + "/" + viewName + "[" + viewPosition + "]";
                    px = px + "/" + viewName + "[" + viewPosition + "]";
                }
                if (childView instanceof ViewGroup) {
                    parentView = (ViewGroup) childView;
                } else {
                    break;
                }
            }
        }

        ViewNode viewNode = new ViewNode(view, viewPosition, listPos, mHasListParent, prefix.equals(WindowHelper.getMainWindowPrefix()), true, mParentIdSettled,
                LinkedString.fromString(opx),
                LinkedString.fromString(px), prefix, viewTraveler);
        viewNode.mViewContent = ViewNode.getViewContent(view, bannerText);
        viewNode.mInheritableGrowingInfo = inheritableObjInfo;
        viewNode.mClickableParentXPath = LinkedString.fromString(px);
        viewNode.mBannerText = bannerText;

        return viewNode;
    }

    public static int getChildAdapterPositionInRecyclerView(View childView, ViewGroup parentView) {
        if (ClassExistHelper.instanceOfAndroidXRecyclerView(parentView)) {
            return ((androidx.recyclerview.widget.RecyclerView) parentView).getChildAdapterPosition(childView);
        } else if (ClassExistHelper.instanceOfSupportRecyclerView(parentView)) {
            // For low version RecyclerView
            try {
                return ((RecyclerView) parentView).getChildAdapterPosition(childView);
            } catch (Throwable e) {
                return ((RecyclerView) parentView).getChildPosition(childView);
            }

        } else if (ClassExistHelper.sHasCustomRecyclerView) {
            return ClassExistHelper.invokeCRVGetChildAdapterPositionMethod(parentView, childView);
        }
        return -1;
    }

    //https://assets.giocdn.com/sdk/hybrid/1.1/vds_hybrid_circle_plugin.min.js?sdkVer=autotrack-2.9.2-SNAPSHOT_e08f56f7&platform=Android
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void callJavaScript(View view, String methodName, Object... params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("try{(function(){");
        stringBuilder.append(methodName);
        stringBuilder.append("(");
        String separator = "";
        for (Object param : params) {
            stringBuilder.append(separator);
            separator = ",";
            if (param instanceof String) {
                stringBuilder.append("'");
                param = ((String) param).replace("'", "\'");
                StringBuilder builder = new StringBuilder();
                LinkedString.stringWithoutQuotation(builder, (String) param);
                param = builder.toString();
            }
            stringBuilder.append(param);
            if (param instanceof String) {
                stringBuilder.append("'");
            }
        }
        stringBuilder.append(");})()}catch(ex){console.log(ex);}");
        try {
            String jsCode = stringBuilder.toString();
            if (view instanceof WebView) {
                WebView webView = (WebView) view;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(jsCode, null);
                } else {
                    webView.loadUrl("javascript:" + jsCode);
                }
            }
        } catch (Exception e) {
        }
    }

    private static class ViewNodeTraveler extends ViewTraveler {
        private long currentTime;
        private ArrayList<ActionStruct> actionStructList = new ArrayList<ActionStruct>();

        public void resetActionStructList() {
            currentTime = System.currentTimeMillis();
            actionStructList.clear();
        }

        @Override
        public void traverseCallBack(ViewNode viewNode) {
            if (actionStructList != null) {
                ActionStruct struct = new ActionStruct();
                struct.xpath = viewNode.mParentXPath;
                struct.content = viewNode.mViewContent;
                struct.index = viewNode.mLastListPos;
                struct.time = currentTime;
                struct.obj = viewNode.mInheritableGrowingInfo;
                actionStructList.add(struct);
            }
        }
    }

    private static ViewNodeTraveler changeTraveler = new ViewNodeTraveler();
    private static ViewNodeTraveler sClickTraveler = new ViewNodeTraveler() {
        @Override
        public boolean needTraverse(ViewNode viewNode) {
            return super.needTraverse(viewNode) && !ClassExistHelper.isViewClickable(viewNode.mView);
        }
    };

}
