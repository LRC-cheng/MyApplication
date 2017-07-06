package cn.edu.gzhu.dp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.gzhu.dp.R;

/**
 * Created by LRC on 2016/11/18.
 */
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final Intent intent = new Intent(this, MainActivity.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        };
        timer.schedule(task, 2000);
    }
}
