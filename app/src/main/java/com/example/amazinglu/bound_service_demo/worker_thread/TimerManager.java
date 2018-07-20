package com.example.amazinglu.bound_service_demo.worker_thread;

import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.logging.Handler;

public class TimerManager {

    public static final int NEW_TIME_STAMP = 1;

    private android.os.Handler mainThreadHandler;
    private static TimerManager sInstance = null;

    public static TimerManager getInstance() {
        return sInstance;
    }

    static {
        sInstance = new TimerManager();
    }

    private TimerManager() {
        mainThreadHandler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // now the timer task is in UI thread
                TimerTask timerTask = (TimerTask) msg.obj;
//                Toast.makeText(timerTask.getContext(), timerTask.getCurTimeStamp(), Toast.LENGTH_SHORT).show();
                timerTask.sendTimeStampToBoundService();
            }
        };
    }

    public void handleState(int state, TimerTask timerTask) {
        switch (state) {
            case NEW_TIME_STAMP:
                Message message = mainThreadHandler.obtainMessage(state, timerTask);
                message.sendToTarget();
                break;
        }
    }

}
