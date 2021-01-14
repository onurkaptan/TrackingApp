package com.onurkaptan.trackingapp;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class ShakeDetector implements SensorEventListener {

    /*
     * The gForce that is necessary to register as shake.
     * Must be greater than 1G (one earth gravity unit).
     * You can install "G-Force", by Blake La Pierre
     * from the Google Play Store and run it to see how
     *  many G's it takes to register a shake
     */
    private static final float SHAKE_THRESHOLD_GRAVITY = 1F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    boolean lowerTresholdPassed = false;
    boolean upperTresholdPassed = false;
    long mlPreviousTime;
    int i = 0;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count,double gForce);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mListener != null) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long startTime;

            double gX = x / SensorManager.GRAVITY_EARTH;
            double gY = y / SensorManager.GRAVITY_EARTH;
            double gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);
            mlPreviousTime = System.currentTimeMillis();
            if (gForce <= 1.0) {
                lowerTresholdPassed = true;
            }
            if (lowerTresholdPassed) {
                i++;
                if (gForce >= 3) {
                    long llCurrentTime = System.currentTimeMillis();
                    long llTimeDiff = llCurrentTime - mlPreviousTime;
                    if (llTimeDiff <= 10) {
                        upperTresholdPassed = true;
                    }
                }
            }
            if (upperTresholdPassed && lowerTresholdPassed) {
                //FALL!!
                i = 0;
                upperTresholdPassed = false;
                lowerTresholdPassed = false;
                mListener.onShake(mShakeCount, gForce);
            }
            if (i > 5) {
                i = 0;
                upperTresholdPassed = false;
                lowerTresholdPassed = false;
            }


      /*      if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                mListener.onShake(mShakeCount, gForce);
            }*/
        }
    }
}