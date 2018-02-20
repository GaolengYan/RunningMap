package com.garry.runningmap;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
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
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.garry.runningmap.overlayutil.PoiOverlay;

public class MapActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private LocationClient mLocationClient;
    private MapView mMapView;
    private FloatingActionButton fab;
    private BaiduMap baiduMap;
    private PoiSearch mPoiSearch;
    private MapActivity.MyLocationListener mMyLocationListener = new MapActivity.MyLocationListener();
    private Boolean isFirst = true;
    private SearchView mSearchView;
    private LatLng ll;
    private String city;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initViews();//实例化控件
        ActionBar actionBar = getSupportActionBar();
        //添加返回按钮
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //浮动按钮事件监听器
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = true;
                toMyLocation(ll);
                Toast.makeText(MapActivity.this, ll.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        requestLocation();//启动定位器

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
     * 地图单击事件回调函数
     */
    BaiduMap.OnMapClickListener clickListener = new BaiduMap.OnMapClickListener() {
        //地图单击事件回调函数
        public void onMapClick(LatLng point){
            Toast.makeText(MapActivity.this, point.toString(), Toast.LENGTH_SHORT).show();
        }

        //地图内 Poi 单击事件回调函数
        public boolean onMapPoiClick(MapPoi poi){
            Toast.makeText(MapActivity.this, poi.getName(), Toast.LENGTH_SHORT).show();
            return true;
        }
    };


    /**
    * 点击左上角返回
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        /*
        mPoiSearch.searchInCity((new PoiCitySearchOption())          //关键字检索POI
                .city(city)                                          //获得的结果返回到OnGetPoiSearchResultListener类中
                .keyword(query)
                .pageNum(1));
        */
        mPoiSearch.searchNearby((new PoiNearbySearchOption())          //关键字检索POI
                .sortType(PoiSortType.distance_from_near_to_far)       //获得的结果返回到OnGetPoiSearchResultListener类中
                .location(ll)
                .keyword(query)
                .radius(30000)                                         //检索范围30公里
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
                PoiOverlay overlay = new MapActivity.MyPoiOverlay(baiduMap);
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
                Toast.makeText(MapActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {
                // 正常返回结果的时候，此处可以获得很多相关信息
                //点击overlay放大至屏幕中央
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(poiDetailResult.getLocation(), 30f);
                baiduMap.animateMapStatus(update);

                //点击overlay，屏幕下方弹出可交互提示栏，显示该POI的名字以及地址
                setPt(poiDetailResult);

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
     * 弹出交互框
     */
    private void setPt(final PoiDetailResult poiDetailResult){
        Snackbar.make(mMapView, poiDetailResult.getName() + ": "
                + poiDetailResult.getAddress(), Snackbar.LENGTH_LONG)
                .setAction("设置"+status, new View.OnClickListener() {
                    //当点击交互栏的“设置终点”按钮时，进行导航
                    @Override
                    public void onClick(View v) {
                        LatLng Pt = new LatLng(poiDetailResult.getLocation().latitude, poiDetailResult.getLocation().longitude);
                        Intent intent = new Intent();
                        intent.putExtra("result", Pt);
                        intent.putExtra("status", status);
                        setResult(1, intent);
                        finish();
                    }
                }).show();
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
        mMapView = (MapView) findViewById(R.id.mapView);                                    //地图控件
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);                                                //打开定位图层
        mMapView.showZoomControls(false);                                                   //设置缩放按钮不显示
        mPoiSearch = PoiSearch.newInstance();                                               //创建POI查询实例
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);                            //绑定POI检索监听者
        baiduMap.setOnMapClickListener(clickListener);                                      //绑定地图点击事件监听器
        status = getIntent().getStringExtra("status");
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
        //mMapView.onDestroy();
        mPoiSearch.destroy();
    }



}






