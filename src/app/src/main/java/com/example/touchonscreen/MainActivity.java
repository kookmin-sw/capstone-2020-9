package com.example.touchonscreen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    Button btn[] = new Button[13];
    EditText userinput;
    //requirement for socket
    private Handler mHandler;
    private Socket socket;
    private String aa = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        //register the buttons
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
        btn[10] = (Button)findViewById(R.id.deletebutton);
        btn[11] = (Button)findViewById(R.id.clearbutton);
        btn[12] = (Button)findViewById(R.id.sendbutton);


        //register onClick event
        for(int i=0; i<13;i++){
            btn[i].setOnClickListener(this);
        }
        mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //소켓 생성
                    socket = new Socket("15.164.116.157", 8081);

                    Log.w("서버 연결됨", "서버 연결됨");
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

                //연결되있는 동안 계속 서버로부터 메시지 수신
                while(true){
                    try{
                        //서버로부터 수신한 메시지 string 으로 리턴
                        InputStream is = socket.getInputStream();//서버에서 받을거
                        byte[] byteAr = new byte[100];
                        int readByteCount = is.read(byteAr);
                        aa = new String(byteAr, 0, readByteCount, "UTF-8");
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, aa, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.w("서버에서 받은 값", "" + aa);

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
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
            case R.id.deletebutton:
                //get the lenth of input
                int slength = userinput.length();
                if(slength>0){

                    //get the last character of the input
                    String selection = userinput.getText().toString().substring(slength-1, slength);
                    Log.e("Selection", selection);

                    String result = userinput.getText().toString().replace(selection, "");
                    Log.e("Result", result);

                    userinput.setText(result);
                    userinput.setSelection(userinput.getText().length());
                }
                break;
            case R.id.clearbutton:
                userinput = (EditText)findViewById(R.id.numberpadtext);
                userinput.setText("");
                break;
            case R.id.sendbutton:
                sendMsg();
                //비밀번호 틀렸을 때
                /*if (aa=="Connected") {

                    // 비밀번호 입력한 거 클리어
                    // 다음액티비티로 전환
                    //startActivity(new Intent(this, kyuhanActivity.class));
                    break;
                }else{
                    userinput = (EditText)findViewById(R.id.numberpadtext);
                    userinput.setText("");
                    break;
                }*/
                break;
        }
    }

    public void sendMsg(){
         //서버와 연결되지 않았다면 전송 불가

        new Thread(new Runnable() {
            @Override
            public void run() {
                String data_2= userinput.getText().toString();
                byte[] byteArr = new byte[100];
                try {
                    byteArr = data_2.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try{
                    OutputStream os = socket.getOutputStream();//서버로 보낼거
                    os.write(byteArr);
                    os.flush();
                    Log.w("서버로 보냄", "서버로 보냄");
                } catch (IOException e){
                    e.printStackTrace();
                    Log.w("서버로 못보냄", "서버로 못보냄");
                }
            }
        }).start();
    }

    protected void onStop() {
        super.onStop();
        try{
            socket.close(); //소켓 닫는다
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addtoarray(String no) {
        //register TextBox
        userinput = (EditText)findViewById(R.id.numberpadtext);
        userinput.append(no);
    }
}