package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr;

import android.util.Log;

import java.util.ArrayList;

public class CprProcess {

    private static final int SVMDataListSize = 5;
    private static final int peakListSize = 5;
    private static final int threshold = 25;
    private float recentSVM;
    private ArrayList<Float> SVMdataList;
    private float peak;
    private float peakSecond;
    private float peakToPeakSecond;
    private ArrayList<Float> position;

    public CprProcess(){
        SVMdataList = new ArrayList<Float>();
        peak = 0;
        peakSecond = 0;
        peakToPeakSecond = 0;
        position = new ArrayList<Float>();
    }

    public int checkCount(float[] acc){
        recentSVM = getSVM(acc);
        addSVMData(SVMdataList, recentSVM);
        if(findPeakSVM(SVMdataList) > 0) return 1;
        else return -1;
    }

    private float getSVM(float[] sensorData) {
        return (float) Math.sqrt(Math.pow(sensorData[0], 2) + Math.pow(sensorData[1], 2) + Math.pow(sensorData[2], 2));
    }

    private void addSVMData(ArrayList<Float> SVMdataList, float recentSVM) {
        SVMdataList.add(recentSVM);

        if (SVMdataList.size() > SVMDataListSize) {
            SVMdataList.remove(0);
        }
    }

    private float findPeakSVM(ArrayList<Float> SVMDataList) {

        int investigatedIndex = SVMDataList.size() / 2;

        if (SVMDataList.get(investigatedIndex) < threshold) return -1; //피크없음 1-1

        for (int i = 1; i < SVMDataList.size() / 2; i++) {
            if (SVMDataList.get(investigatedIndex - i) > SVMDataList.get(investigatedIndex - i + 1)) {
                return -1;
            }

            if (SVMDataList.get(investigatedIndex + i - 1) < SVMDataList.get(investigatedIndex + i)) {
                return -1;
            }
        }

        return SVMDataList.get(investigatedIndex);

    }
}