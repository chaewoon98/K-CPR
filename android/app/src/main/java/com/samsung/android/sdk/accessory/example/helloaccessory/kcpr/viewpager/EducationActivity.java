package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.viewpager;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.ConsumerService;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.R;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.SetTimeActivity;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class EducationActivity extends Activity {

    private boolean mIsBound = false;
    private ConsumerService mConsumerService = null;

    private ViewPager2 viewPager;
    private CircleIndicator3 indicator;
    private ArrayList<ViewPagerData> list;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        // Bind service
        mIsBound = bindService(new Intent(getApplicationContext(), ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);

        viewPager = findViewById(R.id.educationViewPager);
        indicator = findViewById(R.id.indicator);
        button = findViewById(R.id.skip_button);

        //뷰페이저에 넣을 데이터 넣기
        initList();

        viewPager.setAdapter(new ViewPagerAdapter(list));
        indicator.setViewPager(viewPager);
        indicator.createIndicators(list.size(),0);

        //마지막장이면 Start버튼
        //아니면 Skip 버튼으로 텍스트 바꾸기
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("position: ", String.valueOf(position));

                if(position == list.size() - 1){
                    button.setText("OK");
                }else{
                    button.setText("Skip");
                }
            }
        });

        //스킵 / OK 버튼 누르면 시간 설정 페이지로 넘기기
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //타이젠 connect
                if (mIsBound == true && mConsumerService != null) {
                    mConsumerService.findPeers();
                    Toast.makeText(getApplicationContext(), "연결 됨!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "연결 안됨!", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), SetTimeActivity.class);
                startActivity(intent);
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("aboutToken", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("aboutToken", msg);
                        //Toast.makeText(EducationActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void initList(){
        list = new ArrayList<ViewPagerData>();

//        list.add(new ViewPagerData(R.drawable.education_check_vital,
//                "1. 의식확인\n\n환자의 어깨를 가볍게 두드리며\n" +
//                        "\"괜찮으세요?\"라고 물어보세요."));
//
//
//        list.add(new ViewPagerData(R.drawable.education_check_breathing,
//                "2. 호흡 확인\n\n환자의 호흡을 확인하고\n" +
//                        "고개를 젖혀 기도를 확보하세요."));
//
//        list.add(new ViewPagerData(R.drawable.education_call_119,
//                "3. 119신고\n\n" +
//                        "K-CPR의 신고기능을 통해 신고하거나\n" +
//                        "주변 사람들에게 신고 도움을 요청하세요."));
//
//        list.add(new ViewPagerData(R.drawable.education_start_cpr,
//                "4. CPR 시작\n\n" +
//                "시작 버튼을 누르면 스마트워치에서\nK-CPR이 작동 됩니다."));

        list.add(new ViewPagerData(R.drawable.education_check_vital,
                "1. Check consciousness\n\nTap the person on the shoulder\n" +
                        "and shout \"Are you OK?\" to ensure\n" +
                        "that the person needs help."));

        list.add(new ViewPagerData(R.drawable.education_check_breathing,
                "2. Check breathing\n\nListen carefully,\nfor no more than 10 seconds,\n" +
                        "for sounds of breathing\nand open the airway"));
        list.add(new ViewPagerData(R.drawable.education_call_119,
                "3. Call 911\n\n" +
                        "Call 911 through\nK-CPR's reporting function\n" +
                        "or ask people around you to help."));

        list.add(new ViewPagerData(R.drawable.education_start_cpr,
                "4. StartCPR\n\n" +
                        "Press the Start button to activate\nthe K-CPR on smart watch."));
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
