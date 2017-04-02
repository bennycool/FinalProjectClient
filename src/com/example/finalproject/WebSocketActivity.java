package com.example.finalproject;

import android.R.integer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.*;

import org.java_websocket.drafts.Draft_17;





import com.example.finalproject.vo.ExampleClient;
import com.example.finalproject.vo.MyClient;


import android.view.View;
import android.view.View.OnClickListener;


public class WebSocketActivity extends ActionBarActivity {

	private Button button;
	private ExampleClient c;
	private int i = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_socket);
		button = (Button) findViewById(R.id.testWebSocket);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Log.w("****************", "onclick");
				if(c==null){
					try {
						c = new ExampleClient( new URI( "ws://192.168.1.122:8080/GossipServer/testWebsocket" ), new Draft_17() );
						c.connectBlocking();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					c.send("hello "+i);
					i++;
				}
				
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_socket, menu);
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
