package com.sc.myapplication;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import org.json.JSONArray;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;
import static io.dcloud.common.util.ReflectUtils.getApplicationContext;

/**
 * 此类用于与前端js进行交互
 */
public class BaiduMapFeature extends StandardFeature {

    public static final String TAG = "BaiduMapFeature";
    public LocationClient locationClient = null;

    /**
     * 异步调用原生的方法
     *
     * @param webview 调用这个原生方法的js所在的webview
     * @param json    js调用时传入的参数列表
     */
    public void getLocation(IWebview webview, JSONArray json) {
        try {
            Log.e(TAG, "开始定位");
           //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
            locationClient = new LocationClient(getApplicationContext());
            //声明LocationClient类实例并配置定位参数
            LocationClientOption locationOption = new LocationClientOption();

            String callbackId = json.getString(0);
            MyLocationListener myLocationListener = new MyLocationListener(webview, callbackId);

            //注册监听函数
            locationClient.registerLocationListener(myLocationListener);
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            locationOption.setCoorType("bd09ll");
            //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            locationOption.setScanSpan(1000);
            //可选，设置是否需要地址信息，默认不需要
            locationOption.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            locationOption.setIsNeedLocationDescribe(true);
            //可选，设置是否需要设备方向结果
            locationOption.setNeedDeviceDirect(true);
            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            locationOption.setLocationNotify(true);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            locationOption.setIgnoreKillProcess(true);
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            locationOption.setIsNeedLocationDescribe(true);
            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            locationOption.setIsNeedLocationPoiList(true);
            //可选，默认false，设置是否收集CRASH信息，默认收集
            locationOption.SetIgnoreCacheException(false);
            //可选，默认false，设置是否开启Gps定位
            locationOption.setOpenGps(true);
            //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            locationOption.setIsNeedAltitude(false);
            //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
            locationOption.setOpenAutoNotifyMode();
            //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
            locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
            locationClient.setLocOption(locationOption);
            //开始定位
            locationClient.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 异步调用原生的方法
     *
     * @param webview 调用这个原生方法的js所在的webview
     * @param json    js调用时传入的参数列表
     */
    public void stopGetLocation(IWebview webview, JSONArray json) {
        try {
            locationClient.stop();
            String callbackId = json.getString(0);
            JSUtil.execCallback(webview, callbackId,"stop success", JSUtil.OK, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        private IWebview iWebview;
        private String callbackId;
        public MyLocationListener(IWebview iWebview, String callbackId){
            this.iWebview = iWebview;
            this.callbackId = callbackId;
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            float direction = location.getDirection();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
            Log.e(TAG, "获取位置成功" + "latitude: " +  latitude  + ", longitude: " +  longitude + "deretion: " + location.getDirection());

            JSUtil.execCallback(iWebview, callbackId, "{latitude:" +  latitude  + ",longitude:" +  longitude + "}" , JSUtil.OK, true);
        }
    }


}
