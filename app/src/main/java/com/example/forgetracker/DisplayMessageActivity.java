package com.example.forgetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DisplayMessageActivity extends AppCompatActivity {
    private MyTimer myTimer;
    boolean stopped = true;
    TextView textView;
    Button but2;
    Button but3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        textView = findViewById(R.id.textView);
        textView.setText(message);
        but2 = findViewById(R.id.button2);
        but3 = findViewById(R.id.button3);
        myTimer = new MyTimer();
    }


    Timer timer = new Timer();
    private int temp1 = 0;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(!stopped) {
                textView.setText(format(System.currentTimeMillis()));
            }
        };
    };

    public void clickShowTime(View view) {
        if (stopped) {
            stopped = false;
            but2.setText("Pause");
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0, 100);//延时0秒后每隔1秒刷新一次。
        }
        else {
            stopped = true;
            but2.setText("Resume");
        }
    }

    public void clickStopTime(View view) {
        //stopped = true;
    }









    @SuppressLint("DefaultLocale")
    public String format(long elapsed) {
        int hour, minute, second;
        elapsed = elapsed / 1000;
        second = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        hour = (int) (elapsed % 60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

}
class Interval {
    private long start_time;
    private long end_time;
    private boolean is_gap;

    public Interval(long start, long end, boolean status){
        this.start_time = start;
        this.end_time = end;
        this.is_gap = status;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public boolean isGap() {
        return is_gap;
    }

    public void setIs_gap(boolean is_gap) {
        this.is_gap = is_gap;
    }
}

class MyTimer {
    private long start_global;
    private long end_global;
    ArrayList<Long> start_list;
    ArrayList<Long> pause_list;
    ArrayList<Interval> interval_list;

    public MyTimer() {
        start_list = new ArrayList<Long>();
        pause_list = new ArrayList<Long>();
        interval_list = new ArrayList<Interval>();
    }

    public long getStart_global() {
        return start_global;
    }
    public long getEnd_global() {
        return end_global;
    }
    public void setStart_global(long start_global) {
        this.start_global = start_global;
    }
    public void setEnd_global(long end_global) {
        this.end_global = end_global;
    }


    public void start() {
        this.start_global = System.currentTimeMillis();
    }

    public void pause() {
        this.pause_list.add(System.currentTimeMillis());
    }

    public void resume() {
        this.start_list.add(System.currentTimeMillis());
    }

    public void end() {
        this.end_global = System.currentTimeMillis();
    }
}