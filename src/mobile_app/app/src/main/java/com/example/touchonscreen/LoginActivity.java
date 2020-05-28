package com.example.touchonscreen;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private EditText mIdView, mPasswordView;
    private Button mLogInButton, mRegisterButton, mNonmemberButton;

    final int STATUS_DISCONNECTED = 0;
    final int STATUS_CONNECTED = 1;
    String ip = "3.226.243.223";
    String fromServer = "";
    SocketManager manager = null;
    Thread c_thread = new Thread();
    Thread s_thread = new Thread();
    Thread r_thread = new Thread();



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mIdView = (EditText) findViewById((R.id.id_edittext));
        mPasswordView = (EditText) findViewById((R.id.password_edittext));

        mLogInButton = (Button) findViewById(R.id.login_button);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mNonmemberButton = (Button) findViewById(R.id.non_members_button);




        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "가입하지 않은 아이디이거나, 잘못된 비밀번호입니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, DevicelistActivity.class);
                startActivity(intent);
                //서버 연결
                /*try {
                    connectToServer();
                    sendData("hello");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
            }
        });
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                
            }
        });
        mNonmemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intetnt = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intetnt);
            }
        });
    }

    protected void onResume() {

        super.onResume();
        Log.i("LoginActivity", "onResume()");

        manager = SocketManager.getInstance();
    }
    protected void onPause() { super.onPause(); }

    public void connectToServer() throws RemoteException {
        try {
            manager.setSocket(ip);
            manager.connect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void sendData(final String s) throws RemoteException {
        if(manager.getStatus()==STATUS_CONNECTED){
            manager.send(s);
        }else{
            Toast.makeText(LoginActivity.this, "not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    public void receiveData() throws RemoteException {
        if(manager.getStatus()==STATUS_CONNECTED){
            fromServer = manager.receive();
        }else{
            Toast.makeText(this, "not connected to server", Toast.LENGTH_SHORT).show();
        }
    }


}

