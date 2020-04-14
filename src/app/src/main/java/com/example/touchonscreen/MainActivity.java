package com.example.touchonscreen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends Activity implements View.OnClickListener{

    Button btn[] = new Button[13];
    EditText userinput;

    //requirement for socket
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private String ip = "127.0.0.1";
    private  int port = 8081;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //소켓 생성
                    socket = new Socket(ip, port);
                    //서버랑 주고받을 메 메시지 통로
                    is = new DataInputStream(socket.getInputStream());
                    os = new DataOutputStream(socket.getOutputStream());
                    Log.w("Server Coneected", "Server Connected");
                }catch (IOException e1){
                    Log.w("Fail", "Fail");
                    e1.printStackTrace();
                }
            }
        }).start();




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

                break;
            default:;

        }
    }
    public void sendMsg(){
        if(os==null) return; //서버와 연결되지 않았다면 전송 불가

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = userinput.getText().toString();
                try{
                    os.writeUTF(msg);
                    os.flush();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addtoarray(String no) {
        //register TextBox
        userinput = (EditText)findViewById(R.id.numberpadtext);
        userinput.append(no);
    }


}