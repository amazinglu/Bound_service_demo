package com.example.amazinglu.bound_service_demo.worker_thread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.Toast;

import com.example.amazinglu.bound_service_demo.BoundService;

public class ServiceHandler extends Handler {

    public static final String KEY_CUR_TIMER = "key_cur_timer";
    public static final String STATE_NEW_TIMER = "new_timer";

    private Context context;
    private Chronometer chronometer;
    private TimerTask timerTask;

    public ServiceHandler(Looper looper, Context context, TimerTask timerTask) {
        super(looper);
        this.context = context;
        this.timerTask = timerTask;
        chronometer = new Chronometer(context);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    @Override
    public void handleMessage(Message msg) {
        String action = msg.getData().getString(BoundService.KEY_ACTION);
        if (action.equals(BoundService.ACTION_GET_TIMER)) {
            String curTimeStamp = getTimestamp();
            Toast.makeText(context, curTimeStamp, Toast.LENGTH_SHORT).show();

            timerTask.setCurTimeStamp(curTimeStamp);
            timerTask.handleState(STATE_NEW_TIMER);
        }
    }

    public String getTimestamp() {
        long elapsedMillis = SystemClock.elapsedRealtime()
                - chronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
        int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);


        return hours + ":" + minutes + ":" + seconds + ":" + millis;
    }
}
