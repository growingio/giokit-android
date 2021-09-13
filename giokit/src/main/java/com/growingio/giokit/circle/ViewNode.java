package com.growingio.giokit.circle;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.growingio.giokit.GioKitImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xyz on 16/1/14.
 */
public class ViewNode {
    private static final String TAG = "GIO.ViewNode";


    public static final String ANONYMOUS_CLASS_NAME = "Anonymous";

    public View mView;
    public int mLastListPos = -1;
    public boolean mFullScreen;
    private int mViewIndex;
    public boolean mHasListParent;
    public boolean mInClickableGroup;
    public int mViewPosition;
    public LinkedString mParentXPath;
    public LinkedString mOriginalParentXpath;
    public String mWindowPrefix;
    ViewTraveler mViewTraveler;


    public String mViewName;
    public String mBannerText;
    public String mViewContent;
    public String mInheritableGrowingInfo;
    public boolean mParentIdSettled = false;
    public LinkedString mClickableParentXPath;

    // h5 传递过来的elem中包含了isTrackingEditText属性, 必须有个地方进行保存, 作为临时变量
    // 约定hybrid 开头的变量， native元素不能使用
    // https://growingio.atlassian.net/browse/PI-14177
    public boolean hybridIsTrackingEditText = false;


    private int mHashCode = -1;

    public void setViewTraveler(ViewTraveler viewTraveler) {
        this.mViewTraveler = viewTraveler;
    }

    public ViewNode() {
    }

    /**
     * 在基本信息已经计算出来的基础上计算XPath,用于ViewHelper构建Xpath,和基于parentView构建Xpath
     */
    public ViewNode(View view, int viewIndex, int lastListPos, boolean hasListParent, boolean fullScreen,
                    boolean inClickableGroup, boolean parentIdSettled, LinkedString originalParentXPath, LinkedString parentXPath, String windowPrefix, ViewTraveler viewTraveler) {

        mView = view;
        mLastListPos = lastListPos;
        mFullScreen = fullScreen;
        mViewIndex = viewIndex;
        mHasListParent = hasListParent;
        mInClickableGroup = inClickableGroup;
        mParentIdSettled = parentIdSettled;
        mParentXPath = parentXPath;
        mOriginalParentXpath = originalParentXPath;
        mWindowPrefix = windowPrefix;

        mViewTraveler = viewTraveler;
    }


    public void traverseViewsRecur() {
        if (mViewTraveler != null && mViewTraveler.needTraverse(this)) {
            mViewName = mView.getClass().getSimpleName();
            viewPosition();
            calcXPath();
            viewContent();

            if (needTrack())
                mViewTraveler.traverseCallBack(this);
            traverseChildren();
        }
    }

    public void traverseChildren() {
        if (mView instanceof ViewGroup && !(mView instanceof Spinner)) {
            ViewGroup viewGroup = (ViewGroup) mView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                ViewNode childViewNode = new ViewNode(childView, i, mLastListPos,
                        mHasListParent || ClassExistHelper.isListView(mView), mFullScreen,
                        mInClickableGroup || ClassExistHelper.isViewClickable(mView), mParentIdSettled,
                        LinkedString.copy(mOriginalParentXpath),
                        LinkedString.copy(mParentXPath), mWindowPrefix, mViewTraveler);
                if (ClassExistHelper.isViewClickable(mView)) {
                    childViewNode.mClickableParentXPath = mParentXPath;
                } else {
                    childViewNode.mClickableParentXPath = mClickableParentXPath;
                }
                childViewNode.mBannerText = mBannerText;
                childViewNode.mInheritableGrowingInfo = mInheritableGrowingInfo;
                /**
                 * 在traverseViewsRecur之前childViewNode的XPath等信息还是parentView的，而不是childViewNode的；
                 */
                childViewNode.traverseViewsRecur();
            }
        }
    }


    @Override
    public int hashCode() {
        if (mHashCode == -1) {
            int result = 17;
            result = result * 31 + (mViewContent != null ? mViewContent.hashCode() : 0);
            result = result * 31 + (mParentXPath != null ? mParentXPath.hashCode() : 0);
            result = result * 31 + (mInheritableGrowingInfo != null ? mInheritableGrowingInfo.hashCode() : 0);
            result = result * 31 + mLastListPos;
            mHashCode = result;
        }
        return mHashCode;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ViewNode && object.hashCode() == this.hashCode();
    }

    public ViewNode copyWithoutView() {
        return new ViewNode(null, mViewIndex, mLastListPos, mHasListParent,
                mFullScreen, mInClickableGroup, mParentIdSettled,
                LinkedString.fromString(mOriginalParentXpath.toStringValue()),
                LinkedString.fromString(mParentXPath.toStringValue()), mWindowPrefix, null);
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public boolean isNeedTrack() {
        return ViewHelper.isViewSelfVisible(mView);
    }


    private void viewContent() {
        mViewContent = getViewContent(mView, mBannerText);
    }

    private void viewPosition() {
        int idx = mViewIndex;
        if (mView.getParent() != null && (mView.getParent() instanceof ViewGroup)) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (ClassExistHelper.instanceOfAndroidXViewPager(parent)) {
                idx = ((androidx.viewpager.widget.ViewPager) parent).getCurrentItem();
            } else if (ClassExistHelper.instanceOfSupportViewPager(parent)) {
                idx = ((ViewPager) parent).getCurrentItem();
            } else if (parent instanceof AdapterView) {
                AdapterView listView = (AdapterView) parent;
                idx = listView.getFirstVisiblePosition() + mViewIndex;
            } else if (ClassExistHelper.instanceOfRecyclerView(parent)) {
                int adapterPosition = ViewHelper.getChildAdapterPositionInRecyclerView(mView, parent);
                if (adapterPosition >= 0) {
                    idx = adapterPosition;
                }
            }
        }
        mViewPosition = idx;
    }

    private void calcXPath() {
        Object parentObject = mView.getParent();
        if (parentObject == null || (WindowHelper.isDecorView(mView) && !(parentObject instanceof View))) {
            return;
        }
        if (parentObject instanceof View) {
            View parent = (View) parentObject;

            if (parent instanceof ExpandableListView) {
                // 处理ExpandableListView
                ExpandableListView listParent = (ExpandableListView) parent;
                long elp = ((ExpandableListView) mView.getParent()).getExpandableListPosition(mViewPosition);
                if (ExpandableListView.getPackedPositionType(elp) == ExpandableListView.PACKED_POSITION_TYPE_NULL) {
                    mHasListParent = false;
                    if (mViewPosition < listParent.getHeaderViewsCount()) {
                        mOriginalParentXpath.append("/ELH[").append(mViewPosition).append("]/").append(mViewName).append("[0]");
                        mParentXPath.append("/ELH[").append(mViewPosition).append("]/").append(mViewName).append("[0]");
                    } else {
                        int footerIndex = mViewPosition - (listParent.getCount() - listParent.getFooterViewsCount());
                        mOriginalParentXpath.append("/ELF[").append(footerIndex).append("]/").append(mViewName).append("[0]");
                        mParentXPath.append("/ELF[").append(footerIndex).append("]/").append(mViewName).append("[0]");
                    }
                } else {
                    int groupIdx = ExpandableListView.getPackedPositionGroup(elp);
                    int childIdx = ExpandableListView.getPackedPositionChild(elp);
                    if (childIdx != -1) {
                        mLastListPos = childIdx;
                        mParentXPath = LinkedString.fromString(mOriginalParentXpath.toStringValue()).append("/ELVG[").append(groupIdx).append("]/ELVC[-]/").append(mViewName).append("[0]");
                        mOriginalParentXpath.append("/ELVG[").append(groupIdx).append("]/ELVC[").append(childIdx).append("]/").append(mViewName).append("[0]");
                    } else {
                        mLastListPos = groupIdx;
                        mParentXPath = LinkedString.fromString(mOriginalParentXpath.toStringValue()).append("/ELVG[-]/").append(mViewName).append("[0]");
                        mOriginalParentXpath.append("/ELVG[").append(groupIdx).append("]/").append(mViewName).append("[0]");
                    }
                }
            } else if (ClassExistHelper.isListView(parent)) {
                // 处理有特殊的position的元素
                mLastListPos = mViewPosition;
                mParentXPath = LinkedString.fromString(mOriginalParentXpath.toStringValue()).append("/").append(mViewName).append("[-]");
                mOriginalParentXpath.append("/").append(mViewName).append("[").append(mLastListPos).append("]");
            } else if (ClassExistHelper.instanceofAndroidXSwipeRefreshLayout(parentObject)
                    || ClassExistHelper.instanceOfSupportSwipeRefreshLayout(parentObject)) {
                mOriginalParentXpath.append("/").append(mViewName).append("[0]");
                mParentXPath.append("/").append(mViewName).append("[0]");
            } else {
                mOriginalParentXpath.append("/").append(mViewName).append("[").append(mViewPosition).append("]");
                mParentXPath.append("/").append(mViewName).append("[").append(mViewPosition).append("]");
            }
        } else {
            mOriginalParentXpath.append("/").append(mViewName).append("[").append(mViewPosition).append("]");
            mParentXPath.append("/").append(mViewName).append("[").append(mViewPosition).append("]");
        }
    }

    private boolean needTrack() {
        ViewParent parent = mView.getParent();
        if (parent != null) {
            if (mView.isClickable()
                    || mView instanceof TextView
                    || mView instanceof ImageView
                    || mView instanceof WebView
                    || parent instanceof AdapterView
                    || parent instanceof RadioGroup
                    || mView instanceof Spinner
                    || mView instanceof RatingBar
                    || mView instanceof SeekBar) {
                return true;
            }
        }
        return false;
    }

    public static String getViewContent(View view, String bannerText) {
        String value = "";
        if (view instanceof EditText) {
            if (!ClassExistHelper.isPasswordInputType(((EditText) view).getInputType())) {
                CharSequence sequence = getEditTextText((EditText) view);
                value = sequence == null ? "" : sequence.toString();
            }
        } else if (view instanceof RatingBar) {
            value = String.valueOf(((RatingBar) view).getRating());
        } else if (view instanceof Spinner) {
            Object item = ((Spinner) view).getSelectedItem();
            if (item instanceof String) {
                value = (String) item;
            } else {
                View selected = ((Spinner) view).getSelectedView();
                if (selected instanceof TextView && ((TextView) selected).getText() != null) {
                    value = ((TextView) selected).getText().toString();
                }
            }
        } else if (view instanceof SeekBar) {
            value = String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof RadioGroup) {
            RadioGroup group = (RadioGroup) view;
            View selected = group.findViewById(group.getCheckedRadioButtonId());
            if (selected instanceof RadioButton && ((RadioButton) selected).getText() != null) {
                value = ((RadioButton) selected).getText().toString();
            }
        } else if (view instanceof TextView) {
            if (((TextView) view).getText() != null) {
                value = ((TextView) view).getText().toString();
            }
        } else if (view instanceof ImageView) {
            if (!TextUtils.isEmpty(bannerText)) {
                value = bannerText;
            }
        } else if (view instanceof WebView) {
            // 后台获取imp时， getUrl必须在主线程
            String url = ((WebView) view).getUrl();
            if (url != null) {
                value = url;
            }
        }
        if (TextUtils.isEmpty(value)) {
            if (bannerText != null) {
                value = bannerText;
            } else if (view.getContentDescription() != null) {
                value = view.getContentDescription().toString();
            }
        }

        return truncateViewContent(value);
    }

    public static String truncateViewContent(String value) {
        if (value == null) return "";
        if (!TextUtils.isEmpty(value)) {
            if (value.length() > 100) {
                value = value.substring(0, 100);
            }
        }
        return value;
    }

    public static CharSequence getEditTextText(TextView textView) {
        try {
            Field mText = TextView.class.getDeclaredField("mText");
            mText.setAccessible(true);
            return (CharSequence) mText.get(textView);
        } catch (Throwable e) {
        }
        return null;
    }


    public void getVisibleRect(View view, Rect rect, boolean fullscreen) {
        if (fullscreen) {
            view.getGlobalVisibleRect(rect);
        } else {
            int[] offset = new int[2];
            view.getLocationOnScreen(offset);
            view.getLocalVisibleRect(rect);
            rect.offset(offset[0], offset[1]);
        }
    }

    public static class WebElementInfo {
        public String mHost;
        public String mPath;
        public String mQuery;
        public String mHref;
        public String mNodeType;
    }

    public static List<ViewNode> getWebNodesFromEvent(JSONObject object) throws JSONException {
        JSONArray elements = object.getJSONArray("e");
        int elemSize = elements.length();
        List<ViewNode> nodes = new ArrayList<ViewNode>(elemSize);
        if (GioKitImpl.webView == null) return nodes;
        View targetView = GioKitImpl.webView.get();
        String host = object.getString("d");
        String path = object.getString("p");
        String query = object.optString("q", null);
        boolean globalIsTrackingEditText = object.optBoolean("isTrackingEditText", false);
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        Rect visibleRect = new Rect();
        targetView.getGlobalVisibleRect(visibleRect);
        for (int i = 0; i < elemSize; i++) {
            JSONObject elem = elements.getJSONObject(i);
            ViewNode webNode = new ViewNode();
            ViewNode.WebElementInfo info = new ViewNode.WebElementInfo();
            info.mHost = host;
            info.mPath = path;
            info.mQuery = query;
            info.mHref = elem.optString("h", null);
            info.mNodeType = elem.optString("nodeType", null);
            //webNode.mWebElementInfo = info;
            webNode.mViewName = info.mHref == null ? host : info.mHref;
            webNode.mView = targetView;
            if (elem.has("isTrackingEditText")) {
                webNode.hybridIsTrackingEditText = elem.optBoolean("isTrackingEditText");
            } else {
                webNode.hybridIsTrackingEditText = globalIsTrackingEditText;
            }
            if (elem.opt("idx") != null) {
                webNode.mHasListParent = true;
                webNode.mLastListPos = elem.getInt("idx");
                webNode.mParentXPath = LinkedString.fromString(webNode.mOriginalParentXpath.toStringValue())
                        .append("::")
                        .append(elem.getString("x"));
            } else {
                webNode.mParentXPath = new LinkedString().append(elem.getString("x"));
            }
            webNode.mViewPosition = 0;
            webNode.mViewContent = elem.optString("v", "");
            int cx = (int) elem.getDouble("ex");
            int cy = (int) elem.getDouble("ey");
            int cw = (int) elem.getDouble("ew");
            int ch = (int) elem.getDouble("eh");
            nodes.add(webNode);
        }
        return nodes;
    }
}
