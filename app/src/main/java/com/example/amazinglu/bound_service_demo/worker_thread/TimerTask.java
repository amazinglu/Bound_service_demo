package com.example.amazinglu.bound_service_demo.worker_thread;

import android.content.Context;

import com.example.amazinglu.bound_service_demo.BoundService;

/**
 * use the bring the date for worker thread back to UI thread
 * 0. send the instance of TimerTask to worker thread
 * 1. set the current time stamp to the instance of TimerTask from the worker thread
 * 2. send instance of TimerTask to TimerManager (TimerTask.handleState)
 * 3. TimerManager send the instance of TimerTask the UI thread Handler
 * 3. now the instance of TimeTask is in UI thread, use the instance of BoundService in TimerTask to send
 * the cur time stamp back to BoundService
 * 4. then use broadcast to send the current time stamp back to main Activity
 *
 * in a word:
 * TimerTask contains the instance that can only be access in UI thread and also contains the data
 * we needed in worker thread
 * when the TimerTask return to UI thread via UI thread handler, we can use these instance to assign data to
 * the elements in UI thread
 * */
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
