package com.yxy.flms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yxy.flms.R;

public class MainActivity extends CheckPermissionsActivity implements View.OnClickListener {

    private Button login;
    private EditText user;
    private EditText password;
    private TextView face_login;
    private static String user_login;
    private static String user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button)findViewById(R.id.login_btn);
        user = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        face_login = (TextView) findViewById(R.id.face_tv);
        login.setOnClickListener(this);
        face_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                user_login = user.getText().toString();
                user_password = password.getText().toString();
                if (user_login.equals("yangxiaoyu") && user_password.equals("111111")) {
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.face_tv:
                Intent intent = new Intent(MainActivity.this,RecognitionActivity.class);
                startActivity(intent);
                break;
        }
    }
}
