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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.viewpager.EducationActivity;

public class MainActivity extends Activity {

//    private Button education_button; //임시로 에듀케이션 넘어가는 버튼
//    private Button record_button;
//    private Button ranking_button;
    private boolean mIsBound = false;
    private ConsumerService mConsumerService = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Bind service
        mIsBound = bindService(new Intent(this, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.d("mCon", String.valueOf(mConsumerService));
//        education_button = findViewById(R.id.education_button);
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.education_button: {  //임시로 에듀케이션 넘어가는 버튼

                //타이젠 connect
                if (mIsBound == true && mConsumerService != null) {
                    mConsumerService.findPeers();
                    Toast.makeText(getApplicationContext(), "연결 됨!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EducationActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "연결 안됨!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.record_button: {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.ranking_button: {
                Intent intent = new Intent(getApplicationContext(), ConsumerActivity.class);
                startActivity(intent);
                break;
            }

            default:
        }
    }

    //타이젠 연결
    @Override
    protected void onDestroy() {
        // Clean up connections
        if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {
            }
        }
        // Un-bind service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        super.onDestroy();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
            Log.d("service1234",String.valueOf(mConsumerService));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            mIsBound = false;
            Log.d("service12345",String.valueOf(mConsumerService));
        }
    };



}
