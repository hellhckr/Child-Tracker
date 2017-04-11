package com.codemazk.codboy.missing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class UserHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        ImageView view=(ImageView)findViewById(R.id.view1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(UserHome.this, ViewChild.class);

                startActivity(n);

            }
        });
        Button cal=(Button)findViewById(R.id.button6);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(UserHome.this, volentdetails.class);

                startActivity(n);
            }
        });
    }
}
