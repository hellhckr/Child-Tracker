package com.codemazk.codboy.missing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.codemazk.codboy.missing.aadarentry.*;

public class MainHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        ImageView add=(ImageView)findViewById(R.id.add1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(MainHome.this, LoginActivity.class);

                startActivity(n);


            }
        });
        ImageView view=(ImageView)findViewById(R.id.view1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n = new Intent(MainHome.this, com.codemazk.codboy.missing.aadarentry.AddFinger.class);

                startActivity(n);
                finish();

            }
        });
    }
}
