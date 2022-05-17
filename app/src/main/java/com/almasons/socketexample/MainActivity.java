package com.almasons.socketexample;


import static android.content.ContentValues.TAG;

import static com.github.nkzawa.socketio.client.Manager.EVENT_CONNECT_ERROR;
import static com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT;
import static com.github.nkzawa.socketio.client.Socket.EVENT_DISCONNECT;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.almasons.socketexample.databinding.ActivityMainBinding;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.engineio.client.transports.PollingXHR;
import com.github.nkzawa.engineio.client.transports.WebSocket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements Emitter.Listener {
    ActivityMainBinding binding;
    private com.github.nkzawa.socketio.client.Socket mSocket;
    String t_arr[] = {"websocket", "polling"};
    Handler handler;

//    {
//        try {
//
//        mSocket = IO.socket("http://127.0.0.1:24500/");
//    }
//        catch(
//    URISyntaxException e)
//
//    {
//        e.printStackTrace();
//    }

//}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        handler = new Handler();
        try {
            IO.Options options = new IO.Options();
//            options.path= "/test";
//                   options.setForceNew(false)
//                    .setPath("/test")
                   options.transports=new String[]{Polling.NAME, PollingXHR.NAME, WebSocket.NAME};
//                    options.transports= new String[]{WebSocket.NAME};
                    options.secure = false;
                  options.forceNew= false;
//            mSocket = IO.socket(URI.create("http://192.168.29.84:24500"), options);
//            mSocket = IO.socket("http://0.0.0.0:24500", options);
//            mSocket = IO.socket("http:///192.168.29.84:24500/test");

        mSocket = IO.socket(URI.create("http://127.0.0.1:24500"),options);
            mSocket.on(EVENT_CONNECT,this);
            mSocket.on(EVENT_DISCONNECT,this);
            mSocket.on(EVENT_CONNECT_ERROR, this);
            mSocket.connect();
            mSocket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport) args[0];
                    // Adding headers when EVENT_REQUEST_HEADERS is called
                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                            Map<String, List<String>> mHeaders = (Map<String, List<String>>)args[0];
                            mHeaders.put("Authorization", Arrays.asList("Basic bXl1c2VyOm15cGFzczEyMw=="));
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.e("Connected",mSocket.connected() +" " +mSocket.connect());
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
        setContentView(binding.getRoot());
    }


    private void sendMsg() {
        try {
            String message = binding.etMsg.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                binding.layMsg.setError("Enter Msg");

            }
            else {

//                binding.etMsg.setText("");
                JSONObject obj =new JSONObject();
                obj.put("data", message);
                obj.put("receive_count", message);
                mSocket.emit("my_response",obj) ;
                mSocket.emit("my_response","") ;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void call(Object... args) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = (String) args[0];
                    // get the extra data from the fired event and display a toast
                    Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();

//                Log.e("Object",""+args.toString()+"----"+new JSONObject(Arrays.toString(args)));

//                JSONObject data = (JSONObject) args[0];
//                String username;
//                String message;
//
////                    username = data.getString("username");
//                    message = data.getString("message");
//
//
//                // add the message to view
////                addMessage(username, message);
//                binding.etMsg.setText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", this);
    }

}