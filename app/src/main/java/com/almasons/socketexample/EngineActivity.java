package com.almasons.socketexample;

import static io.socket.engineio.client.Socket.setDefaultOkHttpWebSocketFactory;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.almasons.socketexample.databinding.ActivityMainBinding;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.engineio.client.transports.WebSocket;

import java.net.URISyntaxException;

import okhttp3.OkHttpClient;

public class EngineActivity extends AppCompatActivity  {
    ActivityMainBinding binding;
    private com.github.nkzawa.socketio.client.Socket mSocket;
    String t_arr[] = {"websocket", "polling"};
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setContentView(binding.getRoot());
        try {

           Socket.Options opts = new Socket.Options();
            opts.transports = new String[] {WebSocket.NAME};
//            opts.path ="http://127.0.0.1:24500/";
            socket = new Socket("http://127.0.0.1:24500/",opts);

        socket.on(Socket.EVENT_OPEN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socket.send("hi");
                socket.close();
            }
        });
        socket.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data = (String)args[0];
                Log.e("messg",data);
            }
        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Exception err = (Exception)args[0];
                err.printStackTrace();
                Log.e("errrr",err.getMessage());
            }
        });
    }
}
