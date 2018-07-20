package com.example.amazinglu.bound_service_demo;

import android.os.Looper;
import android.os.Message;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TimeManager {

    private static TimeManager instance;
    private android.os.Handler mHandler;

    public static final int STATE_GET_TIMER = 1;

    static {
        instance = new TimeManager();
    }

    private TimeManager() {
        mHandler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String timer = (String) msg.obj;
            }
        };
    }

    public static TimeManager getInstance() {
        return instance;
    }

    public void handleState(String curTime, int state) {
        /**
         * public final Message obtainMessage (int what, Object obj)
         * what	int: Value to assign to the returned Message.what field.
         * obj	Object: Value to assign to the returned Message.obj field.
         * */
        Message timerMessage = mHandler.obtainMessage(state, curTime);
        timerMessage.sendToTarget();
    }
}
