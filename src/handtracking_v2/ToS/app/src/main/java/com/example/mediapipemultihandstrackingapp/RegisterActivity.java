package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class RegisterActivity extends AppCompatActivity {

    private EditText rg_id, rg_pw, rg_name, rg_email;
    private Button rg_register_btn;
    private Socket socket;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean isConnected = false;
    private String rmsg = "";
    Thread sendThread = new Thread();
    Thread recvThread = new Thread();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rg_id = findViewById(R.id.id_edittext_r);
        rg_pw = findViewById(R.id.password_edittext_r);
        rg_name = findViewById(R.id.name_edittext_r);
        rg_email = findViewById(R.id.email_edittext_r);

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

        rg_register_btn = findViewById(R.id.register_button_r);
        rg_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = rg_id.getText().toString();
                String pw = rg_pw.getText().toString();
                String name = rg_name.getText().toString();
                String email = rg_email.getText().toString();
                if("".equals(id) || "".equals(pw) || "".equals(name) || "".equals(email)){
                    Toast.makeText(RegisterActivity.this, "입력하지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show();
                }else{
                    String signup = "";
                    try {
                        signup = convertJson(id, pw, name, email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendData("signup");
                    try {
                        sendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendData(signup);
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

                    if (rmsg.equals("ok")) {
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
                        Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Log.w("서버 회원가입 성공", "서버 회원가입 성공");
                    } else {
                        if (isConnected == true) {
                            Toast.makeText(RegisterActivity.this, "이미 등록된 회원입니다", Toast.LENGTH_SHORT).show();
                            Log.w("서버 회원가입 실패", "서버 회원가입 실패");
                        } else {
                            Toast.makeText(RegisterActivity.this, "서버에 연결되지 않았습니다", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

            }
        });
    }

    void sendData(final String sendmsg) {
        if (isConnected == true) {
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
        } else {
            Toast.makeText(RegisterActivity.this, "서버에 연결되지 않았습니다", Toast.LENGTH_SHORT).show();
            Log.w("서버 연결 안되서 전송 못함", sendmsg);
        }

    }

    public void recvData() {
        if (isConnected == true) {
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
        } else {
            Toast.makeText(RegisterActivity.this, "서버에 연결되지 않았습니다", Toast.LENGTH_SHORT).show();
            Log.w("서버 연결 안되서 수신 못함", rmsg);
        }

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

    public String convertJson(String id, String pw, String name, String email) throws Exception {

        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("pw", pw);
        data.put("name", name);
        data.put("email", email);
        String json = data.toString();
        return json;

    }
    public void onBackPressed() {
        super.onBackPressed();
        if (isConnected == true) {
            try {
                isConnected = false;
                socket.close(); //소켓 닫는다
                Log.w("서버 닫힘", "서버닫힘");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
