/*
 * Copyright (c) 2015 Samsung Electronics Co., Ltd. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation and/or
 *       other materials provided with the distribution.
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.viewpager.EducationActivity;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.ConsumerService;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.R;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.RecordActivity;
import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.SensorData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsumerActivity extends Activity {
    private static TextView mTextView;
    private static MessageAdapter mMessageAdapter;
    private boolean mIsBound = false;
    private ListView mMessageListView;
    private ConsumerService mConsumerService = null;
    private LineChart chart;  //실시간 차트
    private ArrayList<Entry> values;

    private SensorData sensorData;
    private float[] recentRawAcc;

    private int time;
    private int delayMillis;
    private int testNum;
    private CprProcess cprProcess;
    private int fastNum;
    private int slowNum;
    private long nowTime;
    private long beforeTime;
    private int strongNum;
    private int weakNum;
    private float[] acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        mTextView = (TextView) findViewById(R.id.tvStatus);
        mMessageListView = (ListView) findViewById(R.id.lvMessage);
        chart = findViewById(R.id.linechart);
        testNum = 0;
        nowTime = 0;
        fastNum = 0;
        slowNum = 0;
        beforeTime = 0;
        strongNum = 0;
        weakNum = 0;
        mMessageAdapter = new MessageAdapter();
        mMessageListView.setAdapter(mMessageAdapter);
        // Bind service
        mIsBound = bindService(new Intent(ConsumerActivity.this, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);
        cprProcess = new CprProcess();
        values = new ArrayList<>();
        sensorData = new SensorData();
        recentRawAcc = new float[3];
        acc = new float[3];

        //cpr 수행 시간 전달받기
        time = getIntent().getIntExtra("time", 0);

        switch(time){
            case 1:{
                delayMillis = 60000;
                break;
            }
            case 2:{
                delayMillis = 90000;
                break;
            }
            case 3:{
                delayMillis = 120000;
                break;
            }
            default:{
                delayMillis = 30000;
                break;
            }
        }

        chart.setDrawGridBackground(true);
        chart.setBackgroundColor(Color.BLACK);
        chart.setGridBackgroundColor(Color.BLACK);

        // description text
        chart.getDescription().setEnabled(false);
//        chart.getDescription().setEnabled(true);
//        Description des = raw_Chart.getDescription();
//        des.setEnabled(true);
//        des.setText("Real-Time DATA");
//        des.setTextSize(15f);
//        des.setTextColor(Color.WHITE);

        // touch gestures (false-비활성화)
        chart.setTouchEnabled(false);

        // scaling and dragging (false-비활성화)
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        //auto scale
        chart.setAutoScaleMinMaxEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        //X축
//        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getXAxis().setTextSize(12f);

        //Legend
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setFormSize(10f); // set the size of the legend forms/shapes
        l.setTextSize(12f);
        l.setTextColor(Color.WHITE);

        //Y축
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setTextColor(Color.GREEN);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.GREEN);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(40);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        // don't forget to refresh the drawing
        chart.invalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 실행할 동작 코딩
                if (mIsBound == true && mConsumerService != null) {
                    mConsumerService.findPeers();
                }
            }
        }, 1000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                intent.putExtra("chart_data", values);
                intent.putExtra("fast", fastNum);
                intent.putExtra("slow", slowNum);
                intent.putExtra("strong", strongNum);
                intent.putExtra("weak", weakNum);
                startActivity(intent);
                finish();
            }
        },30000);
    }

    @Override
    protected void onDestroy() {
        // Clean up connections
        if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {
                updateTextView("Disconnected");
                mMessageAdapter.clear();
            }
        }
        // Un-bind service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        super.onDestroy();
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.buttonConnect: {
                if (mIsBound == true && mConsumerService != null) {
                    Log.d("test123", String.valueOf(mIsBound) + String.valueOf(mConsumerService));
                    mConsumerService.findPeers();
                }
                break;
            }
            case R.id.buttonDisconnect: {
                if (mIsBound == true && mConsumerService != null) {
                    if (mConsumerService.closeConnection() == false) {
                        updateTextView("Disconnected");
                        Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                        mMessageAdapter.clear();
                    }
                }
                break;
            }
            case R.id.buttonSend: {
                if (mIsBound == true && mConsumerService != null) {
                    if (mConsumerService.sendData("Hello Accessory!")) {
                        Toast.makeText(getApplicationContext(), "success~~", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case R.id.buttonRecord: {

                break;
            }
            default:
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
            Log.d("service123456",String.valueOf(mConsumerService));
            updateTextView("onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            Log.d("service1234567",String.valueOf(mConsumerService));
            mIsBound = false;
            updateTextView("onServiceDisconnected");
        }
    };

    public static void addMessage(String data) {
        mMessageAdapter.addMessage(new Message(data));
    }

    public static void updateTextView(final String str) {
    }

    private class MessageAdapter extends BaseAdapter {
        private static final int MAX_MESSAGES_TO_DISPLAY = 20;
        private List<Message> mMessages;

        public MessageAdapter() {
            mMessages = Collections.synchronizedList(new ArrayList<Message>());
        }

        void addMessage(final Message msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMessages.size() == MAX_MESSAGES_TO_DISPLAY) {
                        mMessages.remove(0);
                        mMessages.add(msg);
                    } else {
                        mMessages.add(msg);
                    }
                    notifyDataSetChanged();
                    mMessageListView.setSelection(getCount() - 1);

                    //@@@이 값을 실시간 차트로 그림
                    Log.d("strMsg 확인", msg.data);

                    //split
                    String msgData[] = msg.data.split(",");
                    Log.d("strMsg 확인 x값", msgData[0]);
                    Log.d("strMsg 확인 y값", msgData[1]);
                    Log.d("strMsg 확인 z값", msgData[2]);

                    recentRawAcc[0] = Float.parseFloat(msgData[0]);
                    recentRawAcc[1] = Float.parseFloat(msgData[1]);
                    recentRawAcc[2] = Float.parseFloat(msgData[2]);

                    //차트에 데이터값 넣기
                    Log.d("가공한 값 확인 :", String.valueOf(sensorData.getDepth(recentRawAcc)));

                    if(cprProcess.checkCount(recentRawAcc) == 1){
                        if (mIsBound == true && mConsumerService != null) {
                            if (mConsumerService.sendData("5")) {
                                beforeTime = nowTime;
                                nowTime = System.currentTimeMillis();

                                if(sensorData.getDepth(recentRawAcc) > 9){
                                    strongNum++;
                                }

                                if(sensorData.getDepth(recentRawAcc) < 2){
                                    weakNum++;
                                }

                                if(nowTime - beforeTime > 1400){
                                    if (mIsBound == true && mConsumerService != null) {
                                        if (mConsumerService.sendData("6")) {
                                            slowNum++;
                                        } else {
                                            Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else if(nowTime - beforeTime < 300){
                                    fastNum++;
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    addEntry(sensorData.getDepth(recentRawAcc));
                    values.add(new Entry(values.size(), sensorData.getDepth(recentRawAcc)));
                }
            });
        }

        void clear() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMessages.clear();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return mMessages.size();
        }

        @Override
        public Object getItem(int position) {
            return mMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View messageRecordView = null;
            if (inflator != null) {
                messageRecordView = inflator.inflate(R.layout.message, null);
                TextView tvData = (TextView) messageRecordView.findViewById(R.id.tvData);
                Message message = (Message) getItem(position);
                tvData.setText(message.data);
            }
            return messageRecordView;
        }
    }

    private static final class Message {
        String data;

        public Message(String data) {
            super();
            this.data = data;
        }
    }

    //차트
    private void addEntry(double num) {

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);
        // set.addEntry(...); // can be called as well

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
        }

        data.addEntry(new Entry((float) set.getEntryCount(), (float) num), 0);
        data.notifyDataChanged();

        // let the chart know it's data has changed
        chart.notifyDataSetChanged();

//        chart.setVisibleXRangeMaximum(150);
        chart.setVisibleXRangeMaximum(50f);
//        chart.setVisibleXRangeMaximum(30);

        // this automatically refreshes the chart (calls invalidate())
        chart.moveViewTo(data.getEntryCount(), 50f, YAxis.AxisDependency.LEFT);
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Real-time Line Data");
        set.setLineWidth(1f);
        set.setDrawValues(false);
        set.setValueTextColor(Color.WHITE);
        set.setColor(Color.WHITE);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawCircles(false);
        set.setHighLightColor(Color.rgb(190, 190, 190));

        return set;
    }
}
