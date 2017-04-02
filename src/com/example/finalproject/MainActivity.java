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
        //����LocationClient��
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
		            JSONObject json = new JSONObject();//����json����  
		            String name = "Rubin";
		            String password = "123456";
		            json.put("name", URLEncoder.encode(name, "UTF-8"));//ʹ��URLEncoder.encode������Ͳ��ɼ��ַ����б���  
		            json.put("password", URLEncoder.encode(password, "UTF-8"));//������put��json������  
		            String jsonstr = json.toString();
		            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));//�����ַ��������ø�Ч��������װ����������ߵ�Ч��,���͵����ַ����Ƽ����ַ������������ݾ����ֽ���  
		            bw.write(jsonstr);//��json�ַ���д�뻺������  
		            bw.flush();//ˢ�»������������ݷ��ͳ�ȥ���ⲽ����Ҫ  
		            outputStream.close();  
		            bw.close();//ʹ����ر� 
									
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
     
            // map view ���ٺ��ڴ����½��յ�λ��
            if (location == null || mapView == null) {
                return;
            }
            currentLocation = location;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
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
        	
            //��ȡ��λ���
            StringBuffer sb = new StringBuffer(256);
     
            sb.append("time : ");
            sb.append(location.getTime());    //��ȡ��λʱ��
     
            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //��ȡ��������
     
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //��ȡγ����Ϣ
     
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //��ȡ������Ϣ
     
            sb.append("\nradius : ");
            sb.append(location.getRadius());    //��ȡ��λ��׼��
     
            if (location.getLocType() == BDLocation.TypeGpsLocation){
     
                // GPS��λ���
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // ��λ������ÿСʱ
     
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //��ȡ������
     
                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //��ȡ���θ߶���Ϣ����λ��
     
                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //��ȡ������Ϣ����λ��
     
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //��ȡ��ַ��Ϣ
     
                sb.append("\ndescribe : ");
                sb.append("gps��λ�ɹ�");
     
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
     
                // ���綨λ���
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //��ȡ��ַ��Ϣ
     
                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //��ȡ��Ӫ����Ϣ
     
                sb.append("\ndescribe : ");
                sb.append("���綨λ�ɹ�");
     
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
     
                // ���߶�λ���
                sb.append("\ndescribe : ");
                sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
     
            } else if (location.getLocType() == BDLocation.TypeServerError) {
     
                sb.append("\ndescribe : ");
                sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
     
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
     
                sb.append("\ndescribe : ");
                sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
     
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
     
                sb.append("\ndescribe : ");
                sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
     
            }
     
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //λ�����廯��Ϣ
     
            List<Poi> list = location.getPoiList();    // POI����
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
        //��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
     
        option.setCoorType("bd09ll");
        //��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
     
        int span=1000;
        option.setScanSpan(span);
        //��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
     
        option.setIsNeedAddress(true);
        //��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
     
        option.setOpenGps(true);
        //��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
     
        option.setLocationNotify(true);
        //��ѡ��Ĭ��false�������Ƿ�GPS��Чʱ����1S/1��Ƶ�����GPS���
     
        option.setIsNeedLocationDescribe(true);
        //��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
     
        option.setIsNeedLocationPoiList(true);
        //��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
     
        option.setIgnoreKillProcess(false);
        //��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��  
     
        option.SetIgnoreCacheException(false);
        //��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
     
        option.setEnableSimulateGps(false);
        //��ѡ��Ĭ��false�������Ƿ���Ҫ����GPS��������Ĭ����Ҫ
     
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
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mapView.onPause();  
        } 
}
