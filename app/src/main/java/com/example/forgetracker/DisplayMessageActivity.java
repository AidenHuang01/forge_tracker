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
    private CountingThread thread;
    TextView textView;
    TextView textView2;
    Button but2;
    Button but3;
    Button but4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        but2 = findViewById(R.id.button2);
        but3 = findViewById(R.id.button3);
        but3.setVisibility(View.INVISIBLE);
        but4 = findViewById(R.id.button4);
        but4.setVisibility(View.INVISIBLE);
        thread = new CountingThread();
        thread.start();
    }


    public void clickShowTime(View view) {
        if(but2.getText().equals("Start") && thread.stopped) {
            long currentTime = System.currentTimeMillis();
            myTimer = new MyTimer(currentTime);
            thread.stopped = false;
            but2.setText("Pause");
            but3.setVisibility(View.VISIBLE);
            but4.setVisibility(View.INVISIBLE);
        }
        else if(but2.getText().equals("Pause") && !thread.stopped) {
            long currentTime = System.currentTimeMillis();
            myTimer.setPause_start(currentTime);
            thread.stopped = true;
            but2.setText("Resume");
            myTimer.whenPause(currentTime);
            updatePool();
        }
        else if(but2.getText().equals("Resume") && thread.stopped) {
            long currentTime = System.currentTimeMillis();
            long pause_interval = currentTime - myTimer.getPause_start();
            myTimer.addPause_time(pause_interval);
            thread.stopped = false;
            but2.setText("Pause");
            myTimer.whenResume(currentTime);
        }

    }

    public void clickStopTime(View view) {
        thread.stopped = true;
        textView.setText(format(0));
        but2.setText("Start");
        but3.setVisibility(View.VISIBLE);
        myTimer.whenStop(System.currentTimeMillis());
        if(!thread.stopped) {
            updatePool();
        }
        but4.setVisibility(View.VISIBLE);
    }

    public void clear(View view) {
        textView2.setText("");
    }

    private class CountingThread extends Thread {
        public boolean stopped = true;

        private CountingThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                if (!stopped) {
                    String str = format(myTimer.get_elapsed());
                    textView.setText(str);
                }

                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }


    @SuppressLint("DefaultLocale")
    private String format(long elapsed) {
        int hour, minute, second, milli;
        milli = (int) (elapsed % 1000);
        elapsed = elapsed / 1000;
        second = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        hour = (int) (elapsed % 60);
        return String.format("%02d:%02d:%02d %03d", hour, minute, second, milli);
    }

    @SuppressLint("DefaultLocale")
    private String formatSec(long elapsed) {
        int hour, minute, second;
        elapsed = elapsed / 1000;
        second = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;
        hour = (int) (elapsed % 60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private void updatePool() {
        ArrayList<Interval> intervalList = myTimer.getInterval_list();
        String startTime = formatSec(intervalList.get(intervalList.size()-1).getStart_time() - myTimer.getStart_global());
        String endTime = formatSec(intervalList.get(intervalList.size()-1).getEnd_time() - myTimer.getStart_global());
        String newStr = startTime + " -- " + endTime + "\n";
        String origin = textView2.getText().toString();
        textView2.setText(origin+newStr);
    }

}
class Interval {
    private long start_time;
    private long end_time;
    private boolean active;

    public Interval(long start, long end, boolean status){
        this.start_time = start;
        this.end_time = end;
        this.active = status;
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

    public boolean isActive() { return active; }

    public void setActive(boolean is_gap) {
        this.active = is_gap;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "start_time=" + start_time +
                ", end_time=" + end_time +
                ", active=" + active +
                '}';
    }
}

class MyTimer {
    private long start_global;
    private long end_global;
    private long pause_time;
    private long pause_start;
    ArrayList<Long> start_list;
    ArrayList<Long> pause_list;
    ArrayList<Interval> interval_list;

    public MyTimer(long currentTime) {
        start_list = new ArrayList<Long>();
        pause_list = new ArrayList<Long>();
        interval_list = new ArrayList<Interval>();
        this.start_global = currentTime;
        this.pause_time = 0;
        this.pause_start = start_global;
    }

    public long getStart_global() {
        return start_global;
    }
    public long getEnd_global() {
        return end_global;
    }

    public long getPause_start() {
        return pause_start;
    }

    public long getPause_time() { return pause_time; }

    public void setStart_global(long start_global) {
        this.start_global = start_global;
    }
    public void setEnd_global(long end_global) {
        this.end_global = end_global;
    }

    public void setPause_start(long pause_start) {
        this.pause_start = pause_start;
    }
    public void addPause_time (long interval) {
        this.pause_time += interval;
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

    public ArrayList<Interval> getInterval_list() { return interval_list; }

    public long get_elapsed() {
        return System.currentTimeMillis() - this.start_global -this.pause_time;
    }

    public void whenStart() {
        return;
    }

    public void whenPause(long currentTime) {
        this.pause_list.add(currentTime);
        if(this.start_list.size() == 0) {
            Interval newInterval = new Interval(this.start_global, currentTime, true);
            interval_list.add(newInterval);
        }
        else {
            int index = pause_list.size() - 2;
            Interval newInterval = new Interval(start_list.get(index), currentTime, true);
            interval_list.add(newInterval);
        }
    }

    public void whenResume(long currentTime) {
        this.start_list.add(currentTime);
        int index = start_list.size() - 1;
        Interval newInterval = new Interval(pause_list.get(index), currentTime, false);
        interval_list.add(newInterval);

    }

    public void whenStop(long currentTime) {
        this.pause_list.add(currentTime);
        if(this.start_list.size() == 0) {
            Interval newInterval = new Interval(this.start_global, currentTime, true);
            interval_list.add(newInterval);
        }
        else {
            int index = start_list.size() - 1;
            Interval newInterval = new Interval(start_list.get(index), currentTime, true);
            interval_list.add(newInterval);
        }
        for(int i=0; i<interval_list.size(); i++) {
            System.out.println(interval_list.get(i));
        }
    }

}