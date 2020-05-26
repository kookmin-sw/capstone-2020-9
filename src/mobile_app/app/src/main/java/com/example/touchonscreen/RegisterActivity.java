package com.example.touchonscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText rg_id, rg_pw, rg_name, rg_email;
    private Button rg_register_btn;

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
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
