package com.wangjie.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.wangjie.refreshableview.R;
import com.wangjie.refreshableview.RefreshableView;
import com.wangjie.refreshableview.refreshablehelper.RefreshableHelper;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RefreshableView refreshableView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.main_tv).setOnClickListener(this);
        refreshableView = (RefreshableView) findViewById(R.id.main_refresh_view);
        refreshableView.setRefreshableHelper(new RefreshableHelper() {

            @Override
            public View onInitRefreshHeaderView() {
                return LayoutInflater.from(MainActivity.this).inflate(R.layout.refresh_head, null);
            }

            @Override
            public boolean onInitRefreshHeight(int originRefreshHeight) {
                refreshableView.setRefreshNormalHeight(refreshableView.getOriginRefreshHeight() / 3);
                refreshableView.setRefreshingHeight(refreshableView.getOriginRefreshHeight());
                refreshableView.setRefreshArrivedStateHeight(refreshableView.getOriginRefreshHeight());
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
                                            Thread.sleep(1000l);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    refreshableView.onCompleteRefresh();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tv:
                Log.d(TAG, "content clicked");
                startActivity(new Intent(this, RefreshableListActivity.class));
                break;
        }
    }
}
