package com.growingio.giokit.circle;

/**
 * Created by xyz on 16/1/16.
 */
public abstract class ViewTraveler {
    public boolean needTraverse(ViewNode viewNode) {
        return viewNode.isNeedTrack();
    }

    //        void traverseCallBack(View view, String xpath, int viewIndex, int lastListPos, int pressedState, boolean fullscreenWindow);
    public abstract void traverseCallBack(ViewNode viewNode);
}
