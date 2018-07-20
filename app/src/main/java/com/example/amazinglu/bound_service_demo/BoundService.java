package com.example.amazinglu.bound_service_demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amazinglu.bound_service_demo.worker_thread.ServiceHandler;
import com.example.amazinglu.bound_service_demo.worker_thread.TimerTask;

public class BoundService extends Service {

    private final IBinder myBinder = new MyBinder();

    public static final String KEY_ACTION = "key_action";
    public static final String ACTION_GET_TIMER = "get_timer";

    public static final String KEY_TIME_STAMP = "key_time_stamp";

    public static final String ACTION_NEW_TIMESTAMP = "com.example.amazinglu.bound_service_demo_new_timestamp";

    private Looper looper;
    private ServiceHandler servicehandler;
    private TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
        timerTask = new TimerTask(BoundService.this, this);
        HandlerThread thread = new HandlerThread("bound_service_thread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        looper = thread.getLooper();
        servicehandler = new ServiceHandler(looper, BoundService.this, timerTask);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void getTimestamp() {
        // ask the worker thread to update the time stamp
        Message message = servicehandler.obtainMessage();
        Bundle args = new Bundle();
        args.putString(KEY_ACTION, ACTION_GET_TIMER);
        message.setData(args);
        servicehandler.sendMessage(message);
    }

    public void showCurTimeStamp(String curTimeStamp) {
        Toast.makeText(BoundService.this, curTimeStamp, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction(ACTION_NEW_TIMESTAMP);
        intent.putExtra(KEY_TIME_STAMP, curTimeStamp);
        sendBroadcast(intent);
    }

    public class MyBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }
}
