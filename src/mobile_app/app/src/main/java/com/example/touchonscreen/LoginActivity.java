package com.example.touchonscreen;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class LoginActivity extends AppCompatActivity {

    private EditText mIdView, mPasswordView;
    private Button mLogInButton, mRegisterButton, mNonmemberButton;
    private Socket socket;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean isConnected = false;
    private String rmsg = "";
    Thread sendThread = new Thread();
    Thread recvThread = new Thread();


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
                String id = mIdView.getText().toString();
                String pw = mPasswordView.getText().toString();
                String idpw = "";
                try {
                    idpw = convertJson_l(id, pw);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isConnected == true) {
                    sendData("login");
                    try {
                        sendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendData(idpw);
                    try {
                        sendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    recvData();
                    try {
                        recvThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mIdView.setText("");
                    mPasswordView.setText("");
                    if (rmsg.equals("fail")) {
                        Toast.makeText(LoginActivity.this, "등록되지 않은 회원이거나, 틀린 비밀번호입니다", Toast.LENGTH_SHORT).show();
                        Log.w("서버 로그인 실패", "서버 로그인 실패");


                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    isConnected = false;
                                    socket.close();
                                    Log.w("서버 닫힘", "서버닫힘");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.w("서버 안닫힘", "서버 안닫힘");
                                }
                            }
                        }).start();
                        Intent intent = new Intent(LoginActivity.this, DevicelistActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("idpw", idpw);
                        startActivity(intent);
                        Log.w("서버 로그인 성공", "서버 로그인 성공");
                    }

                } else {
                    mIdView.setText("");
                    mPasswordView.setText("");
                    Toast.makeText(LoginActivity.this, "서버에 연결되지 않았습니다", Toast.LENGTH_SHORT).show();
                    Log.w("서버 연결 안되서 전송 못함", "서버 연결 안되서 전송 못함");
                }


            }
        });
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected == true) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                isConnected = false;
                                socket.close();
                                Log.w("서버 닫힘", "서버닫힘");
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.w("서버 안닫힘", "서버 안닫힘");
                            }
                        }
                    }).start();
                }
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
        mNonmemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected == true) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                isConnected = false;
                                socket.close();
                                Log.w("서버 닫힘", "서버닫힘");
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.w("서버 안닫힘", "서버 안닫힘");
                            }
                        }
                    }).start();
                }
                Intent intetnt = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intetnt);
            }
        });
    }

    void sendData(final String sendmsg) {
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] byteArr = new byte[100];
                try {
                    byteArr = sendmsg.getBytes("UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {

                    os.write(byteArr);
                    os.flush();
                    Log.w("서버로 보냄", sendmsg);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("서버로 못보냄", sendmsg);
                }
            }
        });
        sendThread.start();
    }

    public void recvData() {
        recvThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //서버로부터 수신한 메시지 string 으로 리턴

                    byte[] byteAr = new byte[100];
                    int readByteCount = is.read(byteAr);
                    rmsg = new String(byteAr, 0, readByteCount, "UTF-8");

                    Log.w("서버에서 받은 값", "" + rmsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        recvThread.start();
    }

    public String convertJson_l(String id, String pw) throws Exception {

        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("pw", pw);

        String json = data.toString();
        return json;

    }

    protected void onStop() {
        super.onStop();
        if (isConnected == true) {
            try {
                isConnected = false;
                socket.close(); //소켓 닫는다
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isConnected==false){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //소켓 생성 후 서버에 연결
                        socket = new Socket("3.226.243.223", 8081);
                        os = socket.getOutputStream();
                        is = socket.getInputStream();

                        Log.w("서버 연결됨", "서버 연결됨");
                        isConnected = true;
                    } catch (IOException e1) {
                        Log.w("서버 연결실패", "서버 연결실패");
                        e1.printStackTrace();
                    }

                }
            }).start();
        }
    }
}

