package com.example.touchonscreen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class LoadingActivity extends Activity {

    private Socket socket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private String ip = "127.0.0.1";
    private  int port = 8081;



    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        try{
            socket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut = new PrintWriter(socket.getOutputStream(), true);
            Log.w("Server Coneected", "Server Connected");
        }catch (IOException e1){
            Log.w("Fail", "Fail");
            e1.printStackTrace();
        }
        startLoading();
    }



    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}

