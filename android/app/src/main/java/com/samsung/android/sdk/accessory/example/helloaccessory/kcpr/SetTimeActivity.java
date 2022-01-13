package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SetTimeActivity extends Activity {

    private boolean mIsBound = false;
    private ConsumerService mConsumerService = null;

    private NumberPicker numberPicker;
    private Button start_button;

    private int time = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind service
        mIsBound = bindService(new Intent(getApplicationContext(), ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_set_time);
        numberPicker = findViewById(R.id.numberPicker);
        start_button = findViewById(R.id.start_button);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(3);
        numberPicker.setDisplayedValues(new String[]{"30 s", "60 s", "90 s", "120 s"});
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // 시작 버튼 누르면
        //타이젠 연결, 시간 전송하기
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = numberPicker.getValue();
                Log.d("time 확인 : ", String.valueOf(time));

//                //타이젠 connect
//                if (mIsBound == true && mConsumerService != null) {
//                    mConsumerService.findPeers();
//                    Toast.makeText(getApplicationContext(), "연결 됨!", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(getApplicationContext(), "연결 안됨!", Toast.LENGTH_SHORT).show();
//                }

                //타이젠 send
                if (mIsBound == true && mConsumerService != null) {
                    if (mConsumerService.sendData(String.valueOf(time))) {
                        Toast.makeText(getApplicationContext(), "send data success!!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                    }
                }

                Intent intent3 = new Intent(getApplicationContext(), ConsumerActivity.class);
                intent3.putExtra("time", time);
                startActivity(intent3);
            }
        });
    }

    //타이젠 연결
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            mIsBound = false;
        }
    };
}
