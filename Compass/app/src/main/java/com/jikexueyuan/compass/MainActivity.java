package com.jikexueyuan.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView mImageView;
    private SensorManager mSensorManager;
    //The accelerometer sensor
    private Sensor mAccelerometerSensor;
    //The mageticField sensor
    private Sensor mMagneticFieldSensor;
    //The data accelerometer sensor accelerate
    float[] accelerometerValues = new float[3];
    //The data magnetic sensor accelerate
    float[] magneticFieldValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init data
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Get compass sensor
        getCompassSensor();
    }

    /**
     * Get compass sensor
     */
    private void getCompassSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometerSensor);
    }

    /**
     * Init data
     */
    private void initData() {
        mImageView = (ImageView) findViewById(R.id.imageView);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = new float[3];
        float[] R = new float[9];

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticFieldValues = event.values;
                break;
        }

        mSensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        mSensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);
        mImageView.animate().rotation(values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
