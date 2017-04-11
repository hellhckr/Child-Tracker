package com.codemazk.codboy.missing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLogin extends AppCompatActivity {
    EditText name,pass;
    Button sign;
    String n,p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        name=(EditText)findViewById(R.id.username);
        pass=(EditText)findViewById(R.id.passowrd);
        sign=(Button)findViewById(R.id.Login);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n=name.getText().toString().trim();
                p=pass.getText().toString().trim();
                if(n.contains("admin")&&p.contains("a")){
                    Intent n=new Intent(AdminLogin.this,VolRegistration.class);
                    startActivity(n);
                }else{
                    Toast.makeText(getApplicationContext(), "Incorrect data", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
