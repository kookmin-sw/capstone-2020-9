package com.example.touchonscreen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends Activity implements View.OnClickListener{

    Button btn[] = new Button[13];
    EditText userinput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);


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
                break;
            default:;

        }
    }

    private void addtoarray(String no) {
        //register TextBox
        userinput = (EditText)findViewById(R.id.numberpadtext);
        userinput.append(no);
    }
}