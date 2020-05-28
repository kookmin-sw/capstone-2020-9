package com.example.touchonscreen;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class SocketService extends Service {
    final int Time_out = 5000;
    final int STATUS_DISCONNECTED = 0;
    final int STATUS_CONNECTED = 1;

    private int status = STATUS_DISCONNECTED;
    private Socket socket = null;
    private SocketAddress socketAddress = null;
    private OutputStream oss = null;
    private InputStream iss = null;
    private int port = 8081;
    private String rmsgs = "";


    IConnectionService.Stub binder = new IConnectionService.Stub() {
        @Override
        public int getStatus() throws RemoteException {
            return status;
        }

        @Override
        public void setSocket(String ip) throws RemoteException {
            mySetSocket(ip);
        }

        @Override
        public void connect() throws RemoteException {
            myConnect();
        }

        @Override
        public void disconnect() throws RemoteException {
            myDisconnect();
        }

        @Override
        public void send(String smsg) throws RemoteException {
            mySend(smsg);
        }

        @Override
        public String receive() throws RemoteException {
            return myReceive();
        }

        public void con_send(String ss){
            myConnect();
            mySend(ss);
        }
    };
    public SocketService(){}

    public void OnCreate(){
        super.onCreate();
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    void mySetSocket(String ip){
        socketAddress = new InetSocketAddress(ip, port);
    }
    void myConnect(){
        socket = new Socket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket.connect(socketAddress);
                    oss = socket.getOutputStream();
                    iss = socket.getInputStream();
                    status = STATUS_CONNECTED;
                    Log.w("서버 연결됨", "서버 연결됨");

                } catch (IOException e) {
                    Log.w("서버 연결실패", "서버 연결실패");
                    e.printStackTrace();
                }

            }
        }).start();
    }

    void myDisconnect(){
        try{
            status = STATUS_DISCONNECTED;
            oss.close();
            iss.close();
            socket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void mySend(final String sendmsg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] byteArr = new byte[100];
                try {
                    byteArr = sendmsg.getBytes("UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try{

                    oss.write(byteArr);
                    oss.flush();
                    Log.w("서버로 보냄", "서버로 보냄");

                } catch (IOException e){
                    e.printStackTrace();
                    Log.w("서버로 못보냄", "서버로 못보냄");
                }
            }
        }).start();
    }
    String myReceive(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    //서버로부터 수신한 메시지 string 으로 리턴
                    byte[] byteAr = new byte[100];
                    int readByteCount = iss.read(byteAr);
                    rmsgs = new String(byteAr, 0, readByteCount, "UTF-8");

                    Log.w("서버에서 받은 값", "" + rmsgs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        return rmsgs;
    }
}
