package com.example.finalproject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.finalproject.BarrageRelativeLayout.BarrageTextItem;
import com.google.gson.Gson;

import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BarrageActivity extends ActionBarActivity {


	
	private Button sendButton;
	private EditText editText;
	private MyButtonClickeListener myButtonClickerListener = new MyButtonClickeListener();
	BarrageRelativeLayout mBarrageRelativeLayout;
	
	
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what==0) {
            	Bundle bundle = msg.getData();
            	String newBarrageString = bundle.getString("newBarrage");
            	if (newBarrageString!=null) {
					sendButton.setText(newBarrageString);
					mBarrageRelativeLayout.addBarrageText(newBarrageString);
				}
            	else{
            		sendButton.setText("Error");
            	}
            }


        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barrage);
		mBarrageRelativeLayout = (BarrageRelativeLayout) findViewById(R.id.barrageView);  
		sendButton = (Button) findViewById(R.id.sendMsg);
		editText = (EditText) findViewById(R.id.editText);
		sendButton.setOnClickListener(myButtonClickerListener);
		Button toWebSocketButton = (Button) findViewById(R.id.toWebsocketActivity);
		toWebSocketButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BarrageActivity.this,WebSocketActivity.class);
				startActivity(intent);
			}
		});
		
		  
        String[] itemText = {"zhangphil ... 5", "***zhangphil 6" };  
        LinkedList<String> texts=new LinkedList<String>();  
        for(int i=0;i<itemText.length;i++){  
            texts.add(itemText[i]);  
        }  
        mBarrageRelativeLayout.setBarrageTexts(texts);  
        
  
        mBarrageRelativeLayout.show(BarrageRelativeLayout.RANDOM_SHOW);  
        mBarrageRelativeLayout.addBarrageText("Rubin");
	}

	public class MyButtonClickeListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			sendBarrage();
		}
		
	}
	
	private void sendBarrage(){
	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String urlStr = "http://192.168.191.1:8080/GossipServer/barrage";
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
					String message = editText.getText().toString();
		            JSONObject json = new JSONObject();//创建json对象  
		            json.put("message", URLEncoder.encode(message, "UTF-8"));//把数据put进json对象中  
		            String jsonstr = json.toString();
		            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流  
		            bw.write(jsonstr);//把json字符串写入缓冲区中  
		            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要  
		            outputStream.close();  
		            bw.close();//使用完关闭 
									
			        int res = connection.getResponseCode();
			        Log.i("res", Integer.toString(res));
			        if(res==200){
	                    InputStream is = connection.getInputStream();
	            		BufferedReader br = new BufferedReader(new InputStreamReader(//使用字符流读取客户端发过来的数据  
	                            connection.getInputStream()));  
	                    String line = null;  
	                    StringBuffer s = new StringBuffer();  
	                    while ((line = br.readLine()) != null) {  
	                        s.append(line);  
	                    }  
	                    br.close(); 
	                    
	                    Message msg = Message.obtain();
	                    msg.what = 0;
	                    Bundle bundle = new Bundle();
	                    bundle.putString("newBarrage", s.toString());
	                    Log.i("ReturnBarrage", s.toString());
	                    msg.setData(bundle);
	                    mHandler.sendMessage(msg);
			        }

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.barrage, menu);
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
}
