package com.google.mediapipe.apps.handtrackinggpu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class NextActivity extends AppCompatActivity {
    private Socket socket_2;
    private String number = "";
    private String msg;
    private OutputStream os;
    Button sendCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        sendCoord = (Button)findViewById(R.id.coordsendBtn);
        sendCoord.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCoord(0.5f, 0.5f);
                Log.w("좌표전송 테스트", "좌표전송");
            }
        });
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
    //서버로 메시지 전송
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
                    os = socket_2.getOutputStream();//서버로 보낼거
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
    //서버로 좌표 전송
    public void sendCoord(float x, float y){
        String sx = Float.toString(x);
        String sy = Float.toString(y);
        String coord = sx + ", " + sy;
        //    x, y     로 전송
        send(coord);
    }
    //서버에서 메시지 수신
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
