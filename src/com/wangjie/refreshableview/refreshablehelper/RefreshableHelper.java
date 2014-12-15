package com.wangjie.refreshableview.refreshablehelper;

import android.view.View;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/15/14.
 */
public interface RefreshableHelper {
    /**
     * 初始化刷新View
     *
     * @return
     */
    View onInitRefreshHeaderView();

    /**
     * 初始化各个尺寸高度
     *
     * @param originRefreshHeight
     * @return
     */
    boolean onInitRefreshHeight(int originRefreshHeight);

    /**
     * 刷新状态改变
     *
     * @param refreshView
     * @param refreshState
     */
    void onRefreshStateChanged(View refreshView, int refreshState);
}
