package com.garry.runningmap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.garry.runningmap.overlayutil.PoiOverlay;
import com.garry.runningmap.usermodule.LoginRegisterActivity;
import com.garry.runningmap.utils.PermissionUtil;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private LocationClient mLocationClient;
    private DrawerLayout mDrawerLayout;
    private MapView mMapView;
    private FloatingActionButton fab;
    private BaiduMap baiduMap;
    private PoiSearch mPoiSearch;
    private MyLocationListener mMyLocationListener = new MyLocationListener();
    private Boolean isFirst = true;
    private SearchView mSearchView;
    private LatLng ll;
    private String city;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();//实例化控件
        //验证权限
        if (!PermissionUtil.isPermissionAllowed(MainActivity.this).isEmpty()) {
            String[] permissions = PermissionUtil.isPermissionAllowed(MainActivity.this)
                    .toArray(new String[PermissionUtil.isPermissionAllowed(MainActivity.this).size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }

        //获取到工具栏，将默认的返回按钮显示并改变样式
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }


        //浮动按钮事件监听器
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = true;
                toMyLocation(ll);
                Toast.makeText(MainActivity.this, ll.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        //侧滑栏监听器，当发生点击事件时，将被点击的item返回到onNavigationItemSelected函数
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //点击“新建比赛”
                    case R.id.nav_start:
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, InitiateActivity.class);
                        intent.putExtra("now_loc", ll);
                        startActivity(intent);
                        break;
                    case R.id.my_game:

                        break;
                    //点击“设置”
                    case R.id.nav_option:

                        break;
                    //点击“关于”
                    case R.id.nav_about:

                        break;
                    default:
                }
                return true;
            }
        });
    }

    /**
     * 启动定位器
     */
    private void requestLocation() {
        //初始化定位器
        initLocation();
        //打开定位器
        mLocationClient.start();
    }


    /**
     * 跳转到当前位置
     */
    public void toMyLocation(LatLng ll) {
        //设置地图更新定位到当前经纬度，放大比例为16f
        if (isFirst) {
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(ll, 16f);
            baiduMap.animateMapStatus(update);
            isFirst = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(ll.latitude);
        locationBuilder.longitude(ll.longitude);
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    /**
     * 自定义定位监听器
     */
    private class MyLocationListener implements BDLocationListener {
        @Override    //获取当前位置经纬度
        public void onReceiveLocation(BDLocation bdLocation) {
            //获取经纬度并封装到LatLng中
            ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            toMyLocation(ll);
            city = bdLocation.getCity();
        }
    }

    /**
     * 工具栏点击事件，将工具栏中被点击的Item返回到onOptionsItemSelected函数
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //改变默认的返回按钮功能为唤出侧滑栏
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }


    /**
     * ---------------------------------------------------------
     * 以下为搜索相关函数
     * 在活动创建时载入工具栏（搜索按钮）
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        //1.查找指定的MenuItem
        MenuItem search = menu.findItem(R.id.search);
        //2.设置SearchView v7包方式
        View view = MenuItemCompat.getActionView(search);
        if (view != null) {
            mSearchView = (SearchView) view;
            //4.设置SearchView 的查询回调接口
            mSearchView.setOnQueryTextListener(this);

            //在搜索输入框没有显示的时候 点击Action ,回调这个接口，并且显示输入框
            //mSearchView.setOnSearchClickListener();
            //当自动补全的内容被选中的时候回调接口
            //mSearchView.setOnSuggestionListener();
            //可以设置搜索的自动补全，或者实现搜索历史
            //mSearchView.setSuggestionsAdapter();

        }
        return true;
    }

    /**
     * 当点击提交搜索时调用该方法
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (city==null){
            Toast.makeText(this, "查无结果", Toast.LENGTH_SHORT).show();
            return false;
        }
        mPoiSearch.searchInCity((new PoiCitySearchOption())          //关键字检索POI
                .city(city)                                          //获得的结果返回到OnGetPoiSearchResultListener类中
                .keyword(query)
                .pageNum(5));

        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    /**
     * 以上为搜索相关函数
     * -------------------------------------------
     * 以下为POI相关函数
     * POI检索结果回调监听器
     */
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        @Override
        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                return;
            }

            if (result.error == SearchResult.ERRORNO.NO_ERROR) {

                baiduMap.clear();

                //创建PoiOverlay

                PoiOverlay overlay = new MyPoiOverlay(baiduMap);

                //设置overlay可以处理标注点击事件

                baiduMap.setOnMarkerClickListener(overlay);

                //设置PoiOverlay数据

                overlay.setData(result);

                //添加PoiOverlay到地图中
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetPoiDetailResult(final PoiDetailResult poiDetailResult) {
            //获取Place详情页检索结果
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {
                // 正常返回结果的时候，此处可以获得很多相关信息
                //点击overlay，屏幕下方弹出可交互提示栏，显示该POI的名字以及地址
                Snackbar.make(mMapView, poiDetailResult.getName() + ": "
                        + poiDetailResult.getAddress(), Snackbar.LENGTH_LONG)
                        .setAction("设置终点", new View.OnClickListener() {
                            //当点击交互栏的“设置终点”按钮时，进行导航
                            @Override
                            public void onClick(View v) {
                                LatLng endPt = new LatLng(poiDetailResult.getLocation().latitude, poiDetailResult.getLocation().longitude);
                                //终点endPt

                            }
                        }).show();
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            //poi 室内检索结果回调
        }
    };

    //自定义Poi覆盖指示物PoiOverlay
    private class MyPoiOverlay extends PoiOverlay {

        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        //重写POI点击事件
        public boolean onPoiClick(int index) {
            // 当对POI进行点击时，检索当前poi详细信息,将结果返回到onGetPoiDetailResult()函数
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                    .poiUid(getPoiResult().getAllPoi().get(index).uid));
            return true;
        }
    }


    /**
     * 以上为各种POI方法
     * -------------------------------------
     * 以下为各种初始化方法
     */

    //控件实例化
    private void initViews() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mMyLocationListener);
        SDKInitializer.initialize(getApplicationContext());                                 //地图初始化
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                             //工具栏
        fab = (FloatingActionButton) findViewById(R.id.fab);                                //浮动按钮
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);                    //侧滑栏
        navigationView = (NavigationView) findViewById(R.id.nav_view);                      //侧滑栏内容
        mMapView = (MapView) findViewById(R.id.mapView);                                    //地图控件
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);                                                //打开定位图层
        mMapView.showZoomControls(false);                                                   //设置缩放按钮不显示
        mPoiSearch = PoiSearch.newInstance();                                               //创建POI查询实例
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);                            //绑定POI检索监听者
        View headerView = navigationView.getHeaderView(0);
        CircleImageView imageView = (CircleImageView) headerView.findViewById(R.id.user_image);

        //登陆/注册
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    //初始化定位设置

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();                           //定位SDK设置
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setScanSpan(5000);           //设置更新位置时间为5秒
        option.setIsNeedAddress(true);      //设置允许获得详细地址
        mLocationClient.setLocOption(option);

    }

    //申请权限反馈
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须统一所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    //管理地图的生命周期
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mPoiSearch.destroy();
    }



}
