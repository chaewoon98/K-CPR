package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr;

import java.util.ArrayList;

public class PositionDataMemory {

    private static PositionDataMemory instance = null;
    private ArrayList<Float> position = new ArrayList<Float>();
    private float peak = 0f;
    private float second = 0f;
    private float peakSecond = 0f;
    private float peakToPeakSecond = 0f;
    private static final int listSize = 5;

    private PositionDataMemory(){
    }

    public static PositionDataMemory getInstance(){
        if(instance == null){
            instance = new PositionDataMemory();
            return  instance;
        }

        return  instance;
    }

    public int count(){
        return position.size();
    }

    public void push(float recentDepth,float second){
        position.add(recentDepth);
        this.second += second;

        if(count() > listSize){
            position.remove(0);
        }
    }

    public float findPeak(){

        if(count() < listSize) return -1f;

        if(isDescentPattern(listSize/2) && isRisingPattern(listSize/2)){
            //Log.d("이윤환", "peak : " + peak + "position.get(listSize/2) : " + position.get(listSize/2));
            if(peak != position.get(listSize/2)){
                peak = position.get(listSize/2);
                peakToPeakSecond = second - peakSecond;
                peakSecond = second;
                //Log.d("이윤환", "peakToPeakSecond : " + peakToPeakSecond);
                return peakToPeakSecond;
            }
        }
        return -1f;
    }

    private boolean isRisingPattern(int toNum){

        for(int counter = 0; counter < toNum; counter++){
            if(position.get(counter) > position.get(counter + 1)){
                return false;
            }
        }

        return true;
    }

    private boolean isDescentPattern(int toNum){

        for(int counter = toNum; counter < count() - 1; counter++){
            if(position.get(counter) < position.get(counter + 1)){
                return false;
            }
        }

        return true;
    }
}