package com.example.touchonscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class KyuhanActivity extends AppCompatActivity {
    private Socket socket_2;
    private String number = "";
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyuhan);
        //매인액티비티에서 받은 올바른 인증번호
        final String pw = getIntent().getStringExtra("valid_pw");
        number = pw;
        Log.w("새로 받은 패스워드", "새로 받은 패스워드" + pw);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //소켓 생성 후 서버에 올바른 인증번호 송신
                    socket_2 = new Socket("15.164.116.157", 8081);
                    Log.w("새로 서버 연결됨", "새로 서버 연결됨");
                    send(pw);
                    receiveMsg();
                } catch (IOException e1) {
                    Log.w("서버 연결실패", "서버 연결실패");
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void send(final String cd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] byteArr = new byte[100];
                try {
                    byteArr = cd.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    OutputStream os = socket_2.getOutputStream();//서버로 보낼거
                    os.write(byteArr);
                    os.flush();
                    Log.w("새로 서버로 보냄", "새로 서버로 보냄");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("서버로 못보냄", "서버로 못보냄");
                }
            }
        }).start();
    }

    public void receiveMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //서버로부터 수신한 메시지 string 으로 리턴
                    InputStream is = socket_2.getInputStream();
                    byte[] byteAr = new byte[100];
                    int readByteCount = is.read(byteAr);
                    msg = new String(byteAr, 0, readByteCount, "UTF-8");
                    Log.w("새로 서버에서 받은 값", "" + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
