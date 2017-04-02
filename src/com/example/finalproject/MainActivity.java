package com.example.finalproject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;

import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMuxer.OutputFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

	private LocationClient mLocationClient;
	private BDLocationListener myListener = new MyLocationListener();
	private MyButtonClickListener myButtonClickListener = new MyButtonClickListener();
	private MapView mapView;
	private BaiduMap mBaiduMap;
	private boolean isFirstLoc = true; 
	private BDLocation currentLocation;
	private Button button;
	private Button barrageButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.bmapView);
        button = (Button) findViewById(R.id.button);
        barrageButton = (Button) findViewById(R.id.barrageButton);
        
        mBaiduMap = mapView.getMap();
        mLocationClient = new LocationClient(getApplicationContext());     
        //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener( myListener ); 
        mBaiduMap.setMyLocationEnabled(true);
        //mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                //com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL, true, null));
        
        button.setOnClickListener(myButtonClickListener);
        barrageButton.setOnClickListener(myButtonClickListener);
        
        mLocationClient.start();
        
    }
    
    public class MyButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button:
				sendInfo();
				break;

			default:
				Intent intent = new Intent(MainActivity.this, BarrageActivity.class);
				startActivity(intent);
				break;
			}
		}	
		
		

    }
    
    
    
	public void sendInfo() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String urlStr = "http://192.168.191.1:8080/GossipServer/test";
				try {
					URL url = new URL(urlStr);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
					//connection.setRequestProperty("Content-type", "application/x-java-serialized-object"); 					
					//connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
					connection.setUseCaches(false); 
					connection.setRequestMethod("POST");
					connection.connect();
					OutputStream outputStream = connection.getOutputStream();
					Gson gson = new Gson();
					String gsonString = gson.toJson(currentLocation);
		            JSONObject json = new JSONObject();//创建json对象  
		            String name = "Rubin";
		            String password = "123456";
		            json.put("name", URLEncoder.encode(name, "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码  
		            json.put("password", URLEncoder.encode(password, "UTF-8"));//把数据put进json对象中  
		            String jsonstr = json.toString();
		            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流  
		            bw.write(jsonstr);//把json字符串写入缓冲区中  
		            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要  
		            outputStream.close();  
		            bw.close();//使用完关闭 
									
			        int res = connection.getResponseCode();

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	
	
    public class MyLocationListener implements BDLocationListener {
    	 
        @Override
        public void onReceiveLocation(BDLocation location) {
     
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            currentLocation = location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        	
            //获取定位结果
            StringBuffer sb = new StringBuffer(256);
     
            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间
     
            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型
     
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息
     
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息
     
            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度
     
            if (location.getLocType() == BDLocation.TypeGpsLocation){
     
                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时
     
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数
     
                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米
     
                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度
     
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
     
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
     
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
     
                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
     
                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息
     
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
     
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
     
                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
     
            } else if (location.getLocType() == BDLocation.TypeServerError) {
     
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
     
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
     
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
     
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
     
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
     
            }
     
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息
     
            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
     
           // Log.i("BaiduLocationApiDem", sb.toString());
        }
    }
    

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
     
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
     
        int span=1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
     
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
     
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
     
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
     
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
     
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
     
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
     
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
     
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
     
        mLocationClient.setLocOption(option);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mapView.onPause();  
        } 
}
