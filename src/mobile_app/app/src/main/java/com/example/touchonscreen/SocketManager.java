package com.example.touchonscreen;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class SocketManager extends Application {
    private static final SocketManager instance = new SocketManager();
    private static Context context = null;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = IConnectionService.Stub.asInterface(service);
            instance.setBinder(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IConnectionService binder = null;

    public SocketManager(){

    }
    public void onCreate() {

        super.onCreate();

        context = getApplicationContext();

        Intent intent = new Intent(context, SocketService.class);
        context.bindService(intent, connection, BIND_AUTO_CREATE);
    }

    public static SocketManager getInstance() {
        return instance;
    }
    private void setBinder(IConnectionService binder) {
        this.binder = binder;
    }
    int getStatus() throws RemoteException {
        return binder.getStatus();
    }
    String recvMsg() throws RemoteException {
        return binder.recvMsg();
    }
    void setSocket(String ip) throws RemoteException {
        binder.setSocket(ip);
    }
    void connect() throws RemoteException {
        binder.connect();
    }
    void disconnect() throws RemoteException {
        binder.disconnect();
    }
    void send(String smsg) throws RemoteException {
        binder.send(smsg);
    }
    void receive() throws RemoteException {
        binder.receive();
    }
    void con_send(String host, String ss) throws RemoteException {
        binder.con_send(host, ss);
    }
    void con_send_signup(String host, String ss) throws RemoteException {
        binder.con_send_signup(host, ss);
    }
    void send_recv(String msg) throws RemoteException {
        binder.send_recv(msg);
    }

}
