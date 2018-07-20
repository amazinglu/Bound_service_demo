package com.example.amazinglu.bound_service_demo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.get_timer) Button getTimer;
    @BindView(R.id.stop_service) Button stopService;
    @BindView(R.id.start_service) Button startService;
    @BindView(R.id.current_timer) TextView curTimer;

    public static final String ACTION_STOP_SERVICE = "action_stop_service";

    private boolean isBound;
    private BoundService mService;
    private TimerReceiver timerReceiver;

    class TimerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String curTimeStamp = intent.getStringExtra(BoundService.KEY_TIME_STAMP);
            if (curTimeStamp != null) {
                curTimer.setText(curTimeStamp);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    mService.getTimestamp();
                }
            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                /**
                 * terminate the worker thread first
                 * */
                mService.stopWorkerThread();
                /**
                 * unbind the service
                 * */
                if (isBound) {
                    unbindService(serviceConnection);
                    isBound = false;
                }
                /**
                 * stop the service
                 * */
                Intent stopIntent = new Intent(MainActivity.this, BoundService.class);
                stopService(stopIntent);

                curTimer.setText(getResources().getString(R.string.begin_counter));
            }
        });

        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BoundService.class);
                /**
                 * here we start service first and then bind
                 * this service can be bind by different activities, but will not auto delete itself when
                 * all the activities unbind, we need to stop it manually
                 * */
                startService(intent);
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerTimerReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(timerReceiver);
    }

    public void registerTimerReceiver() {
        timerReceiver = new TimerReceiver();
        IntentFilter timerReceiverIntentFilter = new IntentFilter();
        timerReceiverIntentFilter.addAction(BoundService.ACTION_NEW_TIMESTAMP);
        registerReceiver(timerReceiver, timerReceiverIntentFilter);
    }

    /**
     * needed if we want to bind service
     * can get the instance of the service here
     * */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /**
             * get the instance of the service
             * so that we can use the public method of the service
             * */
            BoundService.MyBinder binder = (BoundService.MyBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
