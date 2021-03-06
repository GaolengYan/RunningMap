package com.garry.runningmap;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private LocationClient mLocationClient;
    private MapView mMapView;
    private FloatingActionButton fab;
    private BaiduMap baiduMap;
    private GameActivity.MyLocationListener mMyLocationListener = new GameActivity.MyLocationListener();
    private LatLng ll;//当前地点
    private LatLng startLL;//起点
    private LatLng endLL;//终点
    private LatLngBounds latlngBounds;//包含了起点和终点的区域
    private List<LatLng> walkLine;
    private OverlayOptions walklineOverlay;
    private double walkDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initViews();
        requestLocation();
        addPoint(startLL, endLL);
        toMyLocation();
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
     * 自定义定位监听器
     */
    private class MyLocationListener implements BDLocationListener {
        @Override    //获取当前位置经纬度
        public void onReceiveLocation(BDLocation bdLocation) {
            //获取经纬度并封装到LatLng中
            ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            walkLine.add(ll);     //每次定位将当前位置加入行走路线列表中
            if (DistanceUtil.getDistance(ll, endLL) < 50){
                gameOver();
            }
        }
    }

    private void gameOver(){
        for (int i = 1; i < walkLine.size(); i++) {
            walkDistance += DistanceUtil.getDistance(walkLine.get(i-1), walkLine.get(i));
        }
        Toast.makeText(this, "您已完成比赛,一共走了"+walkDistance+"米", Toast.LENGTH_SHORT).show();
        walklineOverlay = new PolylineOptions().width(10).color(0xAAFF0000).points(walkLine);
        Polyline polyline = (Polyline) baiduMap.addOverlay(walklineOverlay);
    }


    /**
     * 跳转到当前位置
     */
    private void toMyLocation() {
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngBounds(latlngBounds);
        baiduMap.animateMapStatus(update);
    }

    /**
     * 添加起点和终点到地图
     */
    private void addPoint(LatLng startLL, LatLng endLL) {
        //构建Marker图标
        BitmapDescriptor startLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.start_marker);
        BitmapDescriptor endLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.end_marker);
        //创建OverlayOptions属性
        OverlayOptions startopeion = new MarkerOptions()
                .position(startLL)
                .icon(startLocation);
        OverlayOptions endoption = new MarkerOptions()
                .position(endLL)
                .icon(endLocation);

        //创建OverlayOptions的集合
        List<OverlayOptions> options = new ArrayList<>();
        //将OverlayOptions添加到list
        options.add(startopeion);
        options.add(endoption);
        //在地图上批量添加
        baiduMap.addOverlays(options);
    }

    /**
     * 地图单击事件回调函数
     */
    BaiduMap.OnMapClickListener clickListener = new BaiduMap.OnMapClickListener() {
        //地图单击事件回调函数
        public void onMapClick(LatLng point){
            Intent intent1 = new Intent();
            intent1.setClass(GameActivity.this, CompassActivity.class);
            startActivity(intent1);
        }

        //地图内 Poi 单击事件回调函数
        public boolean onMapPoiClick(MapPoi poi){
            Toast.makeText(GameActivity.this, poi.getName(), Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    //控件实例化
    private void initViews() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mMyLocationListener);
        SDKInitializer.initialize(getApplicationContext());                                 //地图初始化
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);                             //工具栏
        fab = (FloatingActionButton) findViewById(R.id.fab);                                //浮动按钮
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMyLocation();
            }
        });
        setSupportActionBar(toolbar);
        mMapView = (MapView) findViewById(R.id.mapView);                                    //地图控件
        baiduMap = mMapView.getMap();
        baiduMap.setMyLocationEnabled(true);                                                //打开定位图层
        mMapView.showZoomControls(false);                                                   //设置缩放按钮不显示
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.nothing);
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, false, descriptor));
        baiduMap.setCompassEnable(true);
        startLL = getIntent().getParcelableExtra("startLL");
        endLL = getIntent().getParcelableExtra("endLL");
        latlngBounds = new LatLngBounds.Builder().include(startLL).include(endLL).build();
        baiduMap.setOnMapClickListener(clickListener);                                      //绑定地图点击事件监听器
        walkLine = new ArrayList<>();
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
    }


}
