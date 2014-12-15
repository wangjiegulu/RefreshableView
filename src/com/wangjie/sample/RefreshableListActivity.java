package com.wangjie.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.wangjie.refreshableview.R;
import com.wangjie.refreshableview.RefreshableListView;
import com.wangjie.refreshableview.RefreshableView;
import com.wangjie.refreshableview.refreshablehelper.RefreshableHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/15/14.
 */
public class RefreshableListActivity extends Activity {
    private static final String TAG = RefreshableListActivity.class.getSimpleName();
    RefreshableListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refreshable_list);
        lv = (RefreshableListView) findViewById(R.id.refreshable_lv);
        lv.setRefreshableHelper(new RefreshableHelper() {

            @Override
            public View onInitRefreshHeaderView() {
                return LayoutInflater.from(RefreshableListActivity.this).inflate(R.layout.refresh_head, null);
            }

            @Override
            public boolean onInitRefreshHeight(int originRefreshHeight) {
                lv.setRefreshNormalHeight(lv.getOriginRefreshHeight() / 3);
                lv.setRefreshingHeight(lv.getOriginRefreshHeight());
                lv.setRefreshArrivedStateHeight(lv.getOriginRefreshHeight());
                return false;
            }

            @Override
            public void onRefreshStateChanged(View refreshView, int refreshState) {
                TextView tv = (TextView) refreshView.findViewById(R.id.refresh_head_tv);
                switch (refreshState) {
                    case RefreshableView.STATE_REFRESH_NORMAL:
                        tv.setText("正常状态");
                        break;
                    case RefreshableView.STATE_REFRESH_NOT_ARRIVED:
                        tv.setText("往下拉可以刷新");
                        break;
                    case RefreshableView.STATE_REFRESH_ARRIVED:
                        tv.setText("放手可以刷新");
                        break;
                    case RefreshableView.STATE_REFRESHING:
                        tv.setText("正在刷新");
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(3000l);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    lv.onCompleteRefresh();
                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            Log.e(TAG, "_", e);
                                        }
                                    }
                                }
                        ).start();
                        break;

                }
            }
        });

        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            data.add("item_" + i);
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


}
