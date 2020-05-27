package com.example.touchonscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText rg_id, rg_pw, rg_name, rg_email;
    private Button rg_register_btn;

    final int STATUS_DISCONNECTED = 0;
    final int STATUS_CONNECTED = 1;
    String ip = "3.226.243.223";
    String fromServer = "";
    SocketManager manager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rg_id = findViewById(R.id.id_edittext_r);
        rg_pw = findViewById(R.id.password_edittext_r);
        rg_name = findViewById(R.id.name_edittext_r);
        rg_email = findViewById(R.id.email_edittext_r);

        rg_register_btn = findViewById(R.id.register_button_r);
        rg_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    sendData("Bye");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }*/
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void onResume() {

        super.onResume();
        Log.i("LoginActivity", "onResume()");

        manager = SocketManager.getInstance();
    }
    protected void onPause() { super.onPause(); }
    public void sendData(String s) throws RemoteException {
        if(manager.getStatus()==STATUS_CONNECTED){
            manager.send(s);
        }else{
            Toast.makeText(this, "not connected to server", Toast.LENGTH_SHORT).show();
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
