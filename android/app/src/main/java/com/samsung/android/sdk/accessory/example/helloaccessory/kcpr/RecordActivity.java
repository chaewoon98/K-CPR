package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class RecordActivity extends Activity {

    private LineChart chart;  //실시간 차트
    private ArrayList<Entry> values;
    private int fastNum;
    private int slowNum;
    private int strongNum;
    private int weakNum;
    private String fastString;
    private String slowString;
    private TextView fastTextView;
    private TextView slowTextView;
    private TextView strongTextView;
    private TextView weakTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        fastTextView = findViewById(R.id.record_fast);
        slowTextView = findViewById(R.id.record_slow);
        strongTextView = findViewById(R.id.record_strong);
        weakTextView = findViewById(R.id.record_weak);

        chart = findViewById(R.id.record_linechart);


        //values = new ArrayList<>();

        ArrayList<Entry> values = (ArrayList<Entry>)getIntent().getSerializableExtra("chart_data");
        fastNum = getIntent().getIntExtra("fast", 0);
        slowNum = getIntent().getIntExtra("slow", 0);
        strongNum = getIntent().getIntExtra("strong", 0);
        weakNum = getIntent().getIntExtra("weak", 0);
        fastTextView.setText("Fast : "+String.valueOf(fastNum));
        slowTextView.setText("Slow : "+String.valueOf(slowNum));
        strongTextView.setText("Strong : "+ String.valueOf(strongNum));
        weakTextView.setText("Weak : " + String.valueOf(weakNum));
//        for (int i = 0; i < 30; i++) {
//
//            float val = (float) (Math.random() * 10);
//            values.add(new Entry(i, val));
//        }

        LineDataSet set1;
        set1 = new LineDataSet(values, "DataSet 1");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // black lines and points
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setDrawCircles(false);

        // set data
        chart.setData(data);


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
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        //auto scale
        chart.setAutoScaleMinMaxEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);
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

    }
}
