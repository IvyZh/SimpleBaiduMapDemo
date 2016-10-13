package com.ivy.simplebaidumap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BaiduMap";
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker marker;
    private PoiSearch mPoiSearch;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mPoiSearch = PoiSearch.newInstance();

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        initLocation();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    private void AddSearchPoint(LatLng location) {

        Log.v(TAG, location.toString());

        //定义Maker坐标点
//        LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        //构建MarkerOption，用于在地图上添加Marker
        MarkerOptions option = new MarkerOptions()//自v3.6.0版本起，SDK提供了给加载Marker增加动画的能力，加载maker时包含两种加载动画方式：从地上生长和从天上落下。
                .position(location)
                .draggable(false)//设置可拖拽：
                .alpha(1f)//自v3.6.0版本起，SDK提供了给Marker设置透明度的方法
                .icon(bitmap);


        // 生长动画
        option.animateType(MarkerOptions.MarkerAnimateType.grow);


        //在地图上添加Marker，并显示
        //将marker添加到地图上
        marker = (Marker) (mBaiduMap.addOverlay(option));


    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    boolean isAddMarker = false;

    public void addMarker(View v) {

        if (!isAddMarker) {
            //定义Maker坐标点
            LatLng point = new LatLng(39.963175, 116.400244);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
            //构建MarkerOption，用于在地图上添加Marker
            MarkerOptions option = new MarkerOptions()//自v3.6.0版本起，SDK提供了给加载Marker增加动画的能力，加载maker时包含两种加载动画方式：从地上生长和从天上落下。
                    .position(point)
                    .draggable(true)//设置可拖拽：
                    .alpha(1f)//自v3.6.0版本起，SDK提供了给Marker设置透明度的方法
                    .icon(bitmap);


            // 生长动画
            option.animateType(MarkerOptions.MarkerAnimateType.grow);


            //在地图上添加Marker，并显示
            //将marker添加到地图上
            marker = (Marker) (mBaiduMap.addOverlay(option));

            //调用BaiduMap对象的setOnMarkerDragListener方法设置marker拖拽的监听
            mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
                public void onMarkerDrag(Marker marker) {
                    //拖拽中
                }

                public void onMarkerDragEnd(Marker marker) {
                    //拖拽结束

                    Log.v("BaiduMap", marker.getPosition().latitude + "," + marker.getPosition().longitude);
                }

                public void onMarkerDragStart(Marker marker) {
                    //开始拖拽
                }
            });
        } else {
            marker.remove();
        }

        isAddMarker = !isAddMarker;
    }


    public void normalMap(View v) {
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
    }

    public void satelliteMap(View v) {
        //卫星地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
    }

    public void emptyMap(View v) {
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
    }


    boolean isTraffic = false;

    public void Traffic(View v) {
        isTraffic = !isTraffic;
        //开启交通图
        mBaiduMap.setTrafficEnabled(isTraffic);
    }


    boolean isHeatMap = false;

    public void HeatMap(View v) {
        isHeatMap = !isHeatMap;
        //开启交通图
        mBaiduMap.setBaiduHeatMapEnabled(isHeatMap);
    }

    public void Cluster(View v) {
        Toast.makeText(this, "功能未实现", Toast.LENGTH_LONG).show();
        // 初始化点聚合管理类
//        ClusterManager mClusterManager = new ClusterManager<>(this, mBaiduMap);
//        // 向点聚合管理类中添加Marker实例
//        LatLng llA = new LatLng(39.963175, 116.400244);
//        List<MyItem> items = new ArrayList<>();
//        items.add(new MyItem(llA));
//        mClusterManager.addItems(items);
    }

    boolean isShowMapPoi = false;

    public void showMapPoi(View v) {// 将底图标注设置为隐藏，方法如下：
        isShowMapPoi = !isShowMapPoi;
        mBaiduMap.showMapPoi(isShowMapPoi);
    }


    boolean isPolygonOptions = false;
    OverlayOptions polygonOption;

    public void PolygonOptions(View v) {

        if (isPolygonOptions) {

        } else {
            //定义多边形的五个顶点
            LatLng pt1 = new LatLng(39.93923, 116.357428);
            LatLng pt2 = new LatLng(39.91923, 116.327428);
            LatLng pt3 = new LatLng(39.89923, 116.347428);
            LatLng pt4 = new LatLng(39.89923, 116.367428);
            LatLng pt5 = new LatLng(39.91923, 116.387428);
            List<LatLng> pts = new ArrayList<LatLng>();
            pts.add(pt1);
            pts.add(pt2);
            pts.add(pt3);
            pts.add(pt4);
            pts.add(pt5);
            //构建用户绘制多边形的Option对象
            polygonOption = new PolygonOptions()
                    .points(pts)
                    .stroke(new Stroke(5, 0xAA00FF00))
                    .fillColor(0xAAFFFF00);
            //在地图上添加多边形Option，用于显示
            mBaiduMap.addOverlay(polygonOption);
        }

//        isPolygonOptions = !isPolygonOptions;
    }


    public void PolygonOptionsMuil(View v) {
        Toast.makeText(this, "功能未实现", Toast.LENGTH_LONG).show();
//        //构造纹理资源
//        BitmapDescriptor custom1 = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_road_red_arrow);
//        BitmapDescriptor custom2 = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_road_green_arrow);
//        BitmapDescriptor custom3 = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_road_blue_arrow);
//        // 定义点
//        LatLng pt1 = newLatLng(39.93923, 116.357428);
//        LatLng pt2 = newLatLng(39.91923, 116.327428);
//        LatLng pt3 = newLatLng(39.89923, 116.347428);
//        LatLng pt4 = newLatLng(39.89923, 116.367428);
//        LatLng pt5 = newLatLng(39.91923, 116.387428);
//
//        //构造纹理队列
//        List<BitmapDescriptor> customList = newArrayList < BitmapDescriptor > ();
//        customList.add(custom1);
//        customList.add(custom2);
//        customList.add(custom3);
//
//        List<LatLng> points = newArrayList < LatLng > ();
//        List<Integer> index = newArrayList < Integer > ();
//        points.add(pt1);//点元素
//        index.add(0);//设置该点的纹理索引
//        points.add(pt2);//点元素
//        index.add(0);//设置该点的纹理索引
//        points.add(pt3);//点元素
//        index.add(1);//设置该点的纹理索引
//        points.add(pt4);//点元素
//        index.add(2);//设置该点的纹理索引
//        points.add(pt5);//点元素
//        //构造对象
//        OverlayOptionsooPolyline = newPolylineOptions().width(15).color(0xAAFF0000).points(points).customTextureList(customList).textureIndex(index);
//        //添加到地图
//        mBaiduMap.addOverlay(ooPolyline);

    }

    public void textOption(View v) {
        //定义文字所显示的坐标点
        LatLng llText = new LatLng(39.86923, 116.397428);
        //构建文字Option对象，用于在地图上添加文字
        OverlayOptions textOption = new TextOptions()
                .bgColor(0xAAFFFF00)
                .fontSize(24)
                .fontColor(0xFFFF00FF)
                .text("百度地图SDK")
                .rotate(0)
                .position(llText);
        //在地图上添加该文字对象并显示
        mBaiduMap.addOverlay(textOption);
    }


    public void InfoWindow(View v) {
        //创建InfoWindow展示的view
        Button button = new Button(getApplicationContext());
        button.setBackgroundResource(R.drawable.popup);
        button.setText("InfoWindow");
        //定义用于显示该InfoWindow的坐标点
        LatLng pt = new LatLng(39.86923, 116.397428);//39.963175, 116.400244
        //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
        InfoWindow mInfoWindow = new InfoWindow(button, pt, +47);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    public void GroundOverlay(View v) {
        //定义Ground的显示地理范围
        LatLng southwest = new LatLng(39.92235, 116.380338);
        LatLng northeast = new LatLng(39.947246, 116.414977);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .build();
        //定义Ground显示的图片
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.ground_overlay);
        //定义Ground覆盖物选项
        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0.8f);
        //在地图中添加Ground覆盖物
        mBaiduMap.addOverlay(ooGround);
    }


    boolean isAddHeatMapFunc = false;
    private HeatMap heatmap;

    public void HeatMapFunc(View v) {

        if (isAddHeatMapFunc) {
            heatmap.removeHeatMap();
        } else {
            //设置渐变颜色值
            int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225, 0), Color.rgb(255, 0, 0)};
            //设置渐变颜色起始值
            float[] DEFAULT_GRADIENT_START_POINTS = {0.2f, 1f};
            //构造颜色渐变对象
            Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);


            //以下数据为随机生成地理位置点，开发者根据自己的实际业务，传入自有位置数据即可
            List<LatLng> randomList = new ArrayList<LatLng>();
            Random r = new Random();
            for (int i = 0; i < 500; i++) {
                // 116.220000,39.780000 116.570000,40.150000
                int rlat = r.nextInt(370000);
                int rlng = r.nextInt(370000);
                int lat = 39780000 + rlat;
                int lng = 116220000 + rlng;
                LatLng ll = new LatLng(lat / 1E6, lng / 1E6);
                randomList.add(ll);
            }

            //在大量热力图数据情况下，build过程相对较慢，建议放在新建线程实现
            heatmap = new HeatMap.Builder()
                    .data(randomList)
                    .gradient(gradient)
                    .build();
            //在地图上添加热力图
            mBaiduMap.addHeatMap(heatmap);
        }


        isAddHeatMapFunc = !isAddHeatMapFunc;
    }

    boolean isSearchInCity = false;


    public void PoiCitySearchOption(View v) {
        if (!isSearchInCity) {

            OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
                public void onGetPoiResult(PoiResult result) {
                    //获取POI检索结果
                    List<PoiInfo> allPoi = result.getAllPoi();
//                    List<PoiAddrInfo> addr = result.getAllAddr();
//                    for (int i = 0; i < addr.size(); i++) {
//                        String address = addr.get(i).address;
//                        Log.v(TAG, address);
//                    }

                    Log.v(TAG, "allPoi");
                    for (int i = 0; i < allPoi.size(); i++) {
                        LatLng location = allPoi.get(i).location;
                        AddSearchPoint(location);
                    }

                }

                public void onGetPoiDetailResult(PoiDetailResult result) {
                    //获取Place详情页检索结果
                }

                @Override
                public void onGetPoiIndoorResult(PoiIndoorResult result) {

                }
            };


            mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .city("北京")
                    .keyword("美食")
                    .pageNum(10));
        } else {
            mPoiSearch.destroy();
        }

        isSearchInCity = !isSearchInCity;

    }


    public void BusRoute(View v) {


        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            public void onGetPoiResult(PoiResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                //遍历所有POI，找到类型为公交线路的POI
                for (PoiInfo poi : result.getAllPoi()) {
                    Log.v(TAG, "poi:" + poi.address);

                    if (poi.type == PoiInfo.POITYPE.BUS_LINE || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                        //说明该条POI为公交信息，获取该条POI的UID
                        // busLineId = poi.uid;
                        Log.v(TAG, "uid-" + poi.uid);

                        searchByBusId(poi.uid);

                        break;
                    }
                }

            }

            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult result) {

            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        //以城市内检索为例，详细方法请参考POI检索部分的相关介绍
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city("北京")
                .keyword("717"));


    }

    private void searchByBusId(String uid) {
        BusLineSearch mBusLineSearch = BusLineSearch.newInstance();


        OnGetBusLineSearchResultListener listener = new OnGetBusLineSearchResultListener() {

            @Override
            public void onGetBusLineResult(BusLineResult result) {
                List<BusLineResult.BusStation> stations = result.getStations();
                for (BusLineResult.BusStation busStation : stations) {
                    Log.v(TAG, "bus station:" + busStation.getTitle());
                }
            }
        };

        mBusLineSearch.setOnGetBusLineSearchResultListener(listener);

        //如下代码为发起检索代码，定义监听者和设置监听器的方法与POI中的类似
        mBusLineSearch.searchBusLine((new BusLineSearchOption()
                .city("北京")
                .uid(uid)));
    }

    public void startLoc(View v) {
        Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        //mLocationClient.start();
    }
}
