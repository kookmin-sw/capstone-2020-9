package com.example.touchonscreen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class MainActivity extends Activity implements View.OnClickListener{
    Button btn[] = new Button[12];
    EditText userinput;

    //requirement for socket
    private Socket socket;
    private boolean isConnected = false;
    private String con = "Connected";
    private String rmsg = "";
    private String vpw = "";
    Thread sendThread = new Thread();
    Thread recvThread = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //버튼 등록
        btn[0] = (Button)findViewById(R.id.button1);
        btn[1] = (Button)findViewById(R.id.button2);
        btn[2] = (Button)findViewById(R.id.button3);
        btn[3] = (Button)findViewById(R.id.button4);
        btn[4] = (Button)findViewById(R.id.button5);
        btn[5] = (Button)findViewById(R.id.button6);
        btn[6] = (Button)findViewById(R.id.button7);
        btn[7] = (Button)findViewById(R.id.button8);
        btn[8] = (Button)findViewById(R.id.button9);
        btn[9] = (Button)findViewById(R.id.button0);
        //btn[10] = (Button)findViewById(R.id.deletebutton);
        btn[10] = (Button)findViewById(R.id.clearbutton);
        btn[11] = (Button)findViewById(R.id.sendbutton);



        //onClick 이벤트 등록
        for(int i=0; i<12;i++){
            btn[i].setOnClickListener(this);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //소켓 생성 후 서버에 연결
                    socket = new Socket("15.164.116.157", 8081);

                    Log.w("서버 연결됨", "서버 연결됨");
                    isConnected = true;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Server Connected", Toast.LENGTH_SHORT).show();
                        }
                    });

                }catch (IOException e1){
                    Log.w("서버 연결실패", "서버 연결실패");
                    e1.printStackTrace();
                }
                //서버에 연결되어 있으면 계속 메시지 수신
                /*while(isConnected){
                    try{
                        //서버로부터 수신한 메시지 string 으로 리턴
                        InputStream is = socket.getInputStream();//서버에서 받을거
                        byte[] byteAr = new byte[100];
                        int readByteCount = is.read(byteAr);
                        rmsg = new String(byteAr, 0, readByteCount, "UTF-8");
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, rmsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.w("서버에서 받은 값", "" + rmsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        }).start();
    }

    @Override
    //버튼 온클릭
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button1:
                addtoarray("1");
                break;
            case R.id.button2:
                addtoarray("2");
                break;
            case R.id.button3:
                addtoarray("3");
                break;
            case R.id.button4:
                addtoarray("4");
                break;
            case R.id.button5:
                addtoarray("5");
                break;
            case R.id.button6:
                addtoarray("6");
                break;
            case R.id.button7:
                addtoarray("7");
                break;
            case R.id.button8:
                addtoarray("8");
                break;
            case R.id.button9:
                addtoarray("9");
                break;
            case R.id.button0:
                addtoarray("0");
                break;
            /*case R.id.deletebutton:
                //입력한 번호의 길이
                int slength = userinput.length();
                if (slength > 0) {
                    //입력한 번호 마지막 숫자
                    String selection = userinput.getText().toString().substring(slength - 1, slength);
                    Log.e("Selection", selection);

                    String result = userinput.getText().toString().replace(selection, "");
                    Log.e("Result", result);

                    userinput.setText(result);
                    userinput.setSelection(userinput.getText().length());
                }
                break;*/
            case R.id.clearbutton:
                userinput = (EditText) findViewById(R.id.numberpadtext);
                userinput.setText("");
                break;
            case R.id.sendbutton:
                if(isConnected==true){
                    sendMsg();
                    try {
                        sendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    recvMsg();
                    try {
                        recvThread.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //클리어
                    userinput = (EditText) findViewById(R.id.numberpadtext);
                    userinput.setText("");
                    if (rmsg.equals(con)) {
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
                        //올바른 인증번호 다음 액티비티로 전달
                        Log.w("패스워드", "패스워드" + vpw);
                        Intent intent = new Intent(MainActivity.this, KyuhanActivity.class);
                        intent.putExtra("valid_pw", vpw);
                        startActivity(intent);
                    }else{
                        //Invalid Password일 때
                        Toast.makeText(MainActivity.this, "인증번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                        Log.w("서버 다음화면 못넘어감", "서버 다음화면 못넘어감");
                    }

                    break;
                }else{
                    userinput = (EditText) findViewById(R.id.numberpadtext);
                    userinput.setText("");
                    Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다", Toast.LENGTH_SHORT).show();
                    Log.w("서버 연결 안되서 전송 못함", "서버 연결 안되서 전송 못함");
                    break;
                }
        }
    }



    //메시지 송신
    public void sendMsg() {
        sendThread= new Thread(new Runnable() {
            @Override
            public void run() {
                String smsg = userinput.getText().toString();
                vpw = smsg;
                byte[] byteArr = new byte[100];
                try {
                    byteArr = smsg.getBytes("UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try{
                    OutputStream os = socket.getOutputStream();
                    os.write(byteArr);
                    os.flush();
                    Log.w("서버로 보냄", "서버로 보냄");

                } catch (IOException e){
                    e.printStackTrace();
                    Log.w("서버로 못보냄", "서버로 못보냄");
                }
            }
        });
        sendThread.start();
    }

    public void recvMsg(){
        recvThread = new Thread(new Runnable() {
            @Override
            public void run() {

                    try{
                        //서버로부터 수신한 메시지 string 으로 리턴
                        InputStream is = socket.getInputStream();//서버에서 받을거
                        byte[] byteAr = new byte[100];
                        int readByteCount = is.read(byteAr);
                        rmsg = new String(byteAr, 0, readByteCount, "UTF-8");
                        /*MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, rmsg, Toast.LENGTH_SHORT).show();
                            }
                        });*/
                        Log.w("서버에서 받은 값", "" + rmsg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        });
        recvThread.start();
    }


    protected void onStop() {
        super.onStop();
        try{
            isConnected = false;
            socket.close(); //소켓 닫는다
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addtoarray(String no) {
        //텍스트 박스 등록
        userinput = (EditText)findViewById(R.id.numberpadtext);
        userinput.append(no);
    }
}