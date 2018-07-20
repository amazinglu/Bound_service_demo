package com.example.amazinglu.bound_service_demo.worker_thread;

import android.content.Context;

import com.example.amazinglu.bound_service_demo.BoundService;

public class TimerTask {

    private String curTimeStamp;
    private int curCounter;
    private int counter;
    private Context context;
    private BoundService boundService;

    private TimerManager timerManager = TimerManager.getInstance();

    public TimerTask(Context context, BoundService boundService) {
        curCounter = 0;
        counter = 0;
        this.context = context;
        this.boundService = boundService;
    }

    public void setCurTimeStamp(String curTimeStamp) {
        this.curTimeStamp = curTimeStamp;
    }

    public void handleState(String state) {
        if (state.equals(ServiceHandler.STATE_NEW_TIMER)) {
            counter++;
        }
        // if the counter != curCounter, update the state of TimerManager
        if (counter != curCounter) {
            timerManager.handleState(TimerManager.NEW_TIME_STAMP, this);
            curCounter = counter;
        }
    }

    public Context getContext() {
        return context;
    }

    public String getCurTimeStamp() {
        return curTimeStamp;
    }

    public void sendTimeStampToBoundService() {
        boundService.showCurTimeStamp(curTimeStamp);
    }
}
