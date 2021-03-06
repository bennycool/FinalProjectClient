package com.example.finalproject.vo;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;


public class ExampleClient extends WebSocketClient{
	public ExampleClient( URI serverUri , Draft draft ) {  
        super( serverUri, draft );  
}  

public ExampleClient( URI serverURI ) {  
        super( serverURI );  
}  

@Override  
public void onOpen( ServerHandshake handshakedata ) {  
        System.out.println( "opened connection" );  
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient  
}  

@Override  
public void onMessage( String message ) {  
        System.out.println( "received: " + message );  
}  

@Override  
public void onFragment( Framedata fragment ) {  
        System.out.println( "received fragment: " + new String( fragment.getPayloadData().array() ) );  
}  

@Override  
public void onClose( int code, String reason, boolean remote ) {  
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame  
        System.out.println( "Connection closed by " + ( remote ? "remote peer" : "us" ) );  
}  

@Override  
public void onError( Exception ex ) {  
        ex.printStackTrace();  
        // if the error is fatal then onClose will be called additionally  
}  


}
