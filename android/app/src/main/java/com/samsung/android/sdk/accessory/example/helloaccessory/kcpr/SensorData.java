package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr;

import android.util.Log;

import java.util.Arrays;

public class SensorData {


    private float[] recentRawAcc = {0.0f,0.0f,0.0f};
    private float[] recentAcc = {0.0f,0.0f,0.0f};
    private float[] previousRawAcc = {0.0f,0.0f,0.0f};

    private float[] recentRawVel = {0.0f,0.0f,0.0f};
    private float[] recentVel = {0.0f,0.0f,0.0f};
    private float[] previousRawVel  = {0.0f,0.0f,0.0f};
    private float[] previousVel  = {0.0f,0.0f,0.0f};

    private float[] recentRawPos = {0.0f,0.0f,0.0f};
    private float[] recentPos = {0.0f,0.0f,0.0f};
    private float[] previousRawPos = {0.0f,0.0f,0.0f};
    private float[] previousPos = {0.0f,0.0f,0.0f};

    private float depth;


    public float getDepth(float[] recentRawAcc){
        this.recentRawAcc = recentRawAcc.clone();
        Log.d("sensor","recentRawAcc : "+ Arrays.toString(this.recentAcc));
        setRecentAcc();
        Log.d("sensor","recentAcc : "+ Arrays.toString(this.recentAcc));

        integrate(recentRawVel, previousVel, recentAcc,0.08f);
        Log.d("sensor","recentRawVel : "+ Arrays.toString(this.recentRawVel));
        runTCE(recentVel,recentRawVel,previousVel, previousRawVel);
//        previousRawVel = recentRawVel.clone();
//        previousVel = recentVel.clone();
        Log.d("sensor","recentVel : "+Arrays.toString(this.recentVel));
        integrate(recentRawPos, previousPos, recentVel,0.08f);
        Log.d("sensor","recentRawPos : "+ Arrays.toString(this.recentRawPos));


        runTCE(recentPos,recentRawPos,previousPos, previousRawPos);
        Log.d("sensor","recentPos : "+Arrays.toString(this.recentPos));

//        previousRawPos = recentRawPos.clone();
//        previousPos = recentPos.clone();
//
        depth = excuteDepth(recentPos);
        Log.d("sensor","depth : "+ depth);

        return depth;
    }


    private void setRecentAcc(){
        recentAcc[0] =  0.3f * previousRawAcc[0] + 0.7f * recentRawAcc[0];
        recentAcc[1] =  0.3f * previousRawAcc[1] + 0.7f * recentRawAcc[1];
        recentAcc[2] =  0.3f * previousRawAcc[2] + 0.7f * recentRawAcc[2];
        previousRawAcc = recentRawAcc.clone();
    }

    private void integrate(float[] recentIntegratingData, float[]  previousIntegratedData, float[] recentData, float range){
        recentIntegratingData[0] = previousIntegratedData[0] + (recentData[0]) * range;
        recentIntegratingData[1] = previousIntegratedData[1] + (recentData[1]) * range;
        recentIntegratingData[2] = previousIntegratedData[2] + (recentData[2]) * range;
    }

    private void runTCE(float[] recent, float[] recentRaw, float[] previous, float[] previousRaw){
        recent[0] = 0.95f * (recentRaw[0] + previous[0] - previousRaw[0]);
        recent[1] = 0.95f * (recentRaw[1] + previous[1] - previousRaw[1]);
        recent[2] = 0.95f * (recentRaw[2] + previous[2] - previousRaw[2]);
        previous = recent.clone();
        previousRaw = recentRaw.clone();
    }
    private float excuteDepth(float[] position){
        return (float) Math.sqrt(Math.pow(position[0],2) + Math.pow(position[0],2) + Math.pow(position[0],2)) * 100;
    }
}