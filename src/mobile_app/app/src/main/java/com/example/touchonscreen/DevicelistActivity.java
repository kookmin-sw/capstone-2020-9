package com.example.touchonscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class DevicelistActivity extends AppCompatActivity {
    private Button Load;
    private Button btn[] = new Button[10];
    private LinearLayout linear1;
    private Socket socket;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean isConnected = false;
    private String rmsg = "";
    String id;
    String dvList[];
    String iddid;
    String vpw;
    Thread sendThread = new Thread();
    Thread recvThread = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);
        final String idpw = getIntent().getStringExtra("idpw");
        id = getIntent().getStringExtra("id");
        linear1 = (LinearLayout) findViewById(R.id.linear1);


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
                    sendData("login");
                    sendThread.join();
                    sendData(idpw);
                    sendThread.join();
                    recvData();
                    recvThread.join();

                    dvList = rmsg.split(",");
                    for (int i = 0; i < dvList.length; i++) {
                        Log.w("서버에서 받은 기기 리스트", dvList[i]);
                    }
                } catch (Exception e1) {
                    Log.w("서버 연결실패", "서버 연결실패");
                    e1.printStackTrace();
                }


            }
        }).start();


        Load = (Button) findViewById(R.id.load_button);
        Load.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rmsg.equals("empty")){
                    Toast.makeText(DevicelistActivity.this, "연결된 PC가 없습니다.", Toast.LENGTH_LONG).show();
                }else{
                    setBtn();
                }
            }


        });
    }

    void setBtn() {
        for (int i = 0; i < dvList.length; i++) {
            btn[i] = new Button(this);
            btn[i].setText(dvList[i]);
            btn[i].setTextSize(11);
            btn[i].setId(i);
            linear1.addView(btn[i]);
            final int finalI = i;
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        iddid = convertJson_d(id, dvList[finalI]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendData(iddid);
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
                    vpw = rmsg;
                    sendData(rmsg);
                    try {
                        sendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isConnected=false;
                    try {
                        socket.close();
                        Log.w("서버 닫힘", "서버닫힘");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.w("패스워드", "패스워드" + vpw);
                    Intent intent = new Intent(DevicelistActivity.this, KyuhanActivity.class);
                    intent.putExtra("valid_pw", vpw);
                    startActivity(intent);

                }
            });
        }
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
                    Log.w("서버로 못보냄", "서버로 못보냄");
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

    public String convertJson_d(String id, String did) throws Exception {

        JSONObject data = new JSONObject();
        data.put("id", id);
        data.put("did", did);

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
}
