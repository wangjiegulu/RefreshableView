package com.wangjie.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ScrollView;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/13/14.
 */
public class NestScrollView extends ScrollView {
    private static final String TAG = NestScrollView.class.getSimpleName();

    public NestScrollView(Context context) {
        super(context);
    }

    public NestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "___[dispatchTouchEvent]ev action: " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        Log.d(TAG, "___[onInterceptTouchEvent]ev action: " + ev.getAction());
        if (MotionEvent.ACTION_MOVE == ev.getAction()) {
            return true;
        }
        return false;
    }

    float lastDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastDownY = event.getY();
                parentRequestDisallowInterceptTouchEvent(true); // 保证事件可往下传递
                Log.d(TAG, "___Down");
                return true;
//                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "___move, this.getScrollY(): " + this.getScrollY());
                boolean isTop = event.getY() - lastDownY > 0 && this.getScrollY() == 0;
                if (isTop) { // 允许父控件拦截，即不允许父控件拦截设为false
                    parentRequestDisallowInterceptTouchEvent(false);
                    return false;
                } else { // 不允许父控件拦截，即不允许父控件拦截设为true
                    parentRequestDisallowInterceptTouchEvent(true);
                    return true;
                }
//                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "___up, this.getScrollY(): " + this.getScrollY());
                parentRequestDisallowInterceptTouchEvent(true); // 保证事件可往下传递
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "___cancel");
                break;
        }
        return false;
    }

    /**
     * 是否允许父控件拦截事件
     * @param disallowIntercept
     */
    private void parentRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        ViewParent vp = getParent();
        if (null == vp) {
            return;
        }
        vp.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

}
