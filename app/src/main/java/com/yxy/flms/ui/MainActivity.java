package com.yxy.flms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.yxy.flms.R;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private EditText user;
    private EditText password;
    private String user_login;
    private String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button)findViewById(R.id.login_btn);
        user = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);

        user_login = user.getText().toString();
        user_password = password.getText().toString();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_login.equals("yangxiaoyu") && user_password.equals("111111")) {
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
