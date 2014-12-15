RefreshableView
===============

可下拉刷新的ViewGroup



<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_a.png' height='500px'/>
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_b.png' height='500px'/>
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_c.png' height='500px'/>
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_d.png' 
height='500px'/>

### RefreshableView:
## main_xml:
      <com.wangjie.refreshableview.RefreshableView
                xmlns:rv="http://schemas.android.com/apk/res/com.wangjie.refreshableview"
                android:id="@+id/main_refresh_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                rv:interceptAllMoveEvents="false"
                >
            <com.wangjie.refreshableview.NestScrollView android:layout_width="match_parent" android:layout_height="wrap_content"
                    android:fillViewport="true"
                    >
                <TextView
                        android:id="@+id/main_tv"
                        android:layout_width="fill_parent"
                        android:layout_height="wrap_content"
                        android:gravity="center"
                        android:padding="20dp"
                        android:textSize="18sp"
                        android:text="Drop Down For Refresh\n\n\n\n\n\n\n\n\n\n\nDrop Down For Refresh\nDrop Down For Refresh\n\n\n\n\n\n\n\n\n\n\nDrop Down For Refresh\nDrop Down For Refresh\n\n\n\n\n\n\n\n\n\n\nDrop Down For Refresh\nDrop Down For Refresh\n\n\n\n\n\n\n\n\n\n\nDrop Down For Refresh"
                        />
            </com.wangjie.refreshableview.NestScrollView>
        </com.wangjie.refreshableview.RefreshableView>


## MainActivity:
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
                refreshableView.setRefreshableViewHelper(new RefreshableView.RefreshableViewHelper() {
        
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
                                                    Thread.sleep(3000l);
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
                        break;
                }
            }
        }

### RefreshableListView
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_e.png' height='500px'/>
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_f.png' height='500px'/>
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_g.png' height='500px'/>
<img src='https://raw.githubusercontent.com/wangjiegulu/RefreshableView/master/screenshot/refreshable_h.png' height='500px'/>

## main.xml
            <com.wangjie.refreshableview.RefreshableListView
                        android:id="@+id/refreshable_lv"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:cacheColorHint="@android:color/transparent"
                        />

## RefreshableListActivity
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
