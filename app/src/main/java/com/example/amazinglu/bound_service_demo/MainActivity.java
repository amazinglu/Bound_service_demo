package com.example.amazinglu.bound_service_demo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
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
    @BindView(R.id.current_timer) TextView curTimer;

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
            @Override
            public void onClick(View v) {
                if (isBound) {
                    unbindService(serviceConnection);
                    isBound = false;
                }
                Intent intent = new Intent(MainActivity.this, BoundService.class);
                stopService(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, BoundService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        registerTimerReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        isBound = false;
        unregisterReceiver(timerReceiver);
    }

    public void registerTimerReceiver() {
        timerReceiver = new TimerReceiver();
        IntentFilter timerReceiverIntentFilter = new IntentFilter();
        timerReceiverIntentFilter.addAction(BoundService.ACTION_NEW_TIMESTAMP);
        registerReceiver(timerReceiver, timerReceiverIntentFilter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
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
