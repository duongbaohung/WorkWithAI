package com.example.workwithai.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.workwithai.R;

public class DemoLifeCycleActivity extends AppCompatActivity {
    private final String LOG_ACTIVITY = "LOG_ACTIVITY";
    Button btnAnotherActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_cycle_the_first);
        Log.i(LOG_ACTIVITY, "*** onCreate is running ***");

        btnAnotherActivity = findViewById(R.id.btnAnotherActivity);
        btnAnotherActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // di chuyen sang mot activity khac
                Intent otherActivity = new Intent(DemoLifeCycleActivity.this, DemoLifeCyleAnotherActivity.class);
                startActivity(otherActivity);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ham nay chay truoc khi man hinh hien thi
        Log.i(LOG_ACTIVITY, "*** onStart is running ***");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ham nay se dc chay ngay sau khi nguoi dung tuong tac voi activity
        Log.i(LOG_ACTIVITY, "*** onResume is running ***");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // ham nay se kich hoat khi co 1 activity chuan bi xuat hien
        Log.i(LOG_ACTIVITY, "*** onPause is running ***");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ham nay se kich hoat khi co 1 activity bi an di
        Log.i(LOG_ACTIVITY, "*** onStop is running ***");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //ham se dc kich hoat khi ma hien thi lai activity da tung bi an
        Log.i(LOG_ACTIVITY, "*** onRestart is running ***");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ham nay se dc kich hoat khi huy app
        Log.i(LOG_ACTIVITY, "*** onDestroy is running ***");
    }
}
