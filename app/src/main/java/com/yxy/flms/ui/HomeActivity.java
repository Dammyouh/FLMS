package com.yxy.flms.ui;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.yxy.flms.R;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button getGrades;
    private Button getCourse;
    private Button logOut;
    String stuUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getGrades = (Button) findViewById(R.id.grades_btn);
        getCourse = (Button) findViewById(R.id.home_btn);
        logOut = (Button) findViewById(R.id.login_out_btn);
        stuUrl ="http://newjwxt.bjfu.edu.cn/";
        getGrades.setOnClickListener(this);
        getCourse.setOnClickListener(this);
        logOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.grades_btn:
                Uri uri = Uri.parse(stuUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.home_btn:
                break;
            case R.id.login_out_btn:
                finish();
        };
    }
}
