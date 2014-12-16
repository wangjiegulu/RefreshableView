package com.wangjie.refreshableview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.wangjie.refreshableview.refreshablehelper.RefreshableHelper;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/15/14.
 */
public class RefreshableListView extends ListView {
    private static final String TAG = RefreshableListView.class.getSimpleName();

    public RefreshableListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 刷新状态
     */
    public static final int STATE_REFRESH_NORMAL = 0x21;
    public static final int STATE_REFRESH_NOT_ARRIVED = 0x22;
    public static final int STATE_REFRESH_ARRIVED = 0x23;
    public static final int STATE_REFRESHING = 0x24;
    private int refreshState;
    // 刷新状态监听
    private RefreshableHelper refreshableHelper;
    /**
     * 刷新的view
     */
    private View refreshHeaderView;
    /**
     * 刷新的view的真实高度
     */
    private int originRefreshHeight;
    /**
     * 有效下拉刷新需要达到的高度
     */
    private int refreshArrivedStateHeight;
    /**
     * 刷新时显示的高度
     */
    private int refreshingHeight;
    /**
     * 正常未刷新高度
     */
    private int refreshNormalHeight;

    public void setRefreshableHelper(RefreshableHelper refreshableHelper) {
        this.refreshableHelper = refreshableHelper;
    }

    private Context context;

    private void init(Context context) {
        this.context = context;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "[onSizeChanged]w: " + w + ", h: " + h);
        Log.d(TAG, "[onSizeChanged]oldw: " + oldw + ", oldh: " + oldh);
        Log.d(TAG, "[onSizeChanged]child counts: " + this.getChildCount());

    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (null == refreshHeaderView) {
            addHeaderView();
        }
        super.setAdapter(adapter);
    }

    private void addHeaderView() {
        if (null != refreshableHelper) {
            refreshHeaderView = refreshableHelper.onInitRefreshHeaderView();
        }
        if (null == refreshHeaderView) {
            Log.e(TAG, "refreshHeaderView is null!");
            return;
        }
        this.addHeaderView(refreshHeaderView);
        // 计算refreshHeadView尺寸
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        refreshHeaderView.measure(width, expandSpec);

        originRefreshHeight = refreshHeaderView.getMeasuredHeight();

        boolean isUseDefault = true;
        if (null != refreshableHelper) {
            isUseDefault = refreshableHelper.onInitRefreshHeight(originRefreshHeight);
        }

        // 初始化各个高度
        if (isUseDefault) {
            refreshArrivedStateHeight = originRefreshHeight;
            refreshingHeight = originRefreshHeight;
            refreshNormalHeight = 0;
        }
        Log.d(TAG, "[onSizeChanged]refreshHeaderView origin height: " + originRefreshHeight);
        changeViewHeight(refreshHeaderView, refreshNormalHeight);

        // 初始化为正常状态
        setRefreshState(STATE_REFRESH_NORMAL);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
//        if(MotionEvent.ACTION_MOVE == ev.getAction()){
//            return true;
//        }
//        return false;
        return true;
    }

    private float lastDownY = Float.MAX_VALUE;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        super.onTouchEvent(ev);
        Log.d(TAG, "[onTouchEvent]ev action: " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                super.onTouchEvent(ev);
                lastDownY = ev.getY();
                Log.d(TAG, "Down --> lastDownY: " + lastDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                float curY = ev.getY();
                float deltaY = curY - lastDownY;
                Log.d(TAG, "Move --> deltaY(curY - downY): " + deltaY);

                int curHeight = refreshHeaderView.getMeasuredHeight();
                int exceptHeight = curHeight + (int) (deltaY / 2);

//                boolean isTop = this.getScrollY() == 0;
                boolean isTop = 0 == this.getChildAt(0).getTop();
                boolean isDropDown = curY - lastDownY > 0;
                Log.d(TAG, "isTop: " + isTop + ", getTop(): " + this.getChildAt(0).getTop());
                if (isTop) {
                    if (refreshState == STATE_REFRESH_NORMAL && !isDropDown) { // 正常状态，手指往上（列表往下滚动）
                        super.onTouchEvent(ev);
                    } else {
                        // 如果当前没有处在正在刷新状态，则更新刷新状态
                        if (STATE_REFRESHING != refreshState) {
                            if (curHeight >= refreshArrivedStateHeight) { // 达到可刷新状态
                                setRefreshState(STATE_REFRESH_ARRIVED);
                            } else if (curHeight == refreshNormalHeight) { // 正常状态
                                setRefreshState(STATE_REFRESH_NORMAL);
                            } else { // 未达到可刷新状态
                                setRefreshState(STATE_REFRESH_NOT_ARRIVED);
                            }
                            changeViewHeight(refreshHeaderView, Math.max(refreshNormalHeight, exceptHeight));
                        } else {
                            super.onTouchEvent(ev);
                        }

                    }

                } else {
                    super.onTouchEvent(ev);
                }
                lastDownY = curY;
                break;
            case MotionEvent.ACTION_UP:
                super.onTouchEvent(ev);
                lastDownY = Float.MAX_VALUE;
                Log.d(TAG, "Up --> lastDownY: " + lastDownY);
                // 如果是达到刷新状态，则设置正在刷新状态的高度
                if (STATE_REFRESH_ARRIVED == refreshState) { // 达到了刷新的状态
                    startHeightAnimation(refreshHeaderView, refreshHeaderView.getMeasuredHeight(), refreshingHeight);
                    setRefreshState(STATE_REFRESHING);
                } else if (STATE_REFRESHING == refreshState) { // 正在刷新的状态
                    startHeightAnimation(refreshHeaderView, refreshHeaderView.getMeasuredHeight(), refreshingHeight);
                } else {
                    // 执行动画后回归正常状态
                    startHeightAnimation(refreshHeaderView, refreshHeaderView.getMeasuredHeight(), refreshNormalHeight, normalAnimatorListener);
                }
                break;
        }
        return true;
    }


    /**
     * 刷新完毕后调用此方法
     */
    public void onCompleteRefresh() {
        if (STATE_REFRESHING == refreshState) {
            setRefreshState(STATE_REFRESH_NORMAL);
            startHeightAnimation(refreshHeaderView, refreshHeaderView.getMeasuredHeight(), refreshNormalHeight);
        }
    }

    /**
     * 修改当前的刷新状态
     *
     * @param expectRefreshState
     */
    private void setRefreshState(int expectRefreshState) {
        if (expectRefreshState != refreshState) {
            refreshState = expectRefreshState;
            if (null != refreshableHelper) {
                refreshableHelper.onRefreshStateChanged(refreshHeaderView, refreshState);
            }
        }
    }


    /**
     * 改变某控件的高度
     *
     * @param view
     * @param height
     */
    private void changeViewHeight(View view, int height) {
        Log.d(TAG, "[changeViewHeight]change Height: " + height);
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (null == lp) {
            lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        lp.height = height;
        view.setLayoutParams(lp);
    }

    /**
     * 改变某控件的高度动画
     *
     * @param view
     * @param fromHeight
     * @param toHeight
     */
    private void startHeightAnimation(final View view, int fromHeight, int toHeight) {
        startHeightAnimation(view, fromHeight, toHeight, null);
    }

    private void startHeightAnimation(final View view, int fromHeight, int toHeight, Animator.AnimatorListener animatorListener) {
        if (toHeight == view.getMeasuredHeight()) {
            return;
        }
        ValueAnimator heightAnimator = ValueAnimator.ofInt(fromHeight, toHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                if (null == value) return;
                changeViewHeight(view, value);
            }
        });
        if (null != animatorListener) {
            heightAnimator.addListener(animatorListener);
        }
        heightAnimator.setInterpolator(new LinearInterpolator());
        heightAnimator.setDuration(300/*ms*/);
        heightAnimator.start();
    }

    AnimatorListenerAdapter normalAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            setRefreshState(STATE_REFRESH_NORMAL); // 回归正常状态
        }
    };

    public void setRefreshArrivedStateHeight(int refreshArrivedStateHeight) {
        this.refreshArrivedStateHeight = refreshArrivedStateHeight;
    }

    public void setRefreshingHeight(int refreshingHeight) {
        this.refreshingHeight = refreshingHeight;
    }

    public void setRefreshNormalHeight(int refreshNormalHeight) {
        this.refreshNormalHeight = refreshNormalHeight;
    }

    public int getOriginRefreshHeight() {
        return originRefreshHeight;
    }

}



