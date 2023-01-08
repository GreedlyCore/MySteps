package com.example.mysteps;




import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SensorEventListener {


    SensorManager sensorManager;
    CircularProgressBar circularProgressBar;
    TextView tv_stepsTaken;
    TextView tv_stepsDailyLimit;
    Button btn_addStep;
    Button btn_changeStepsLimit;

    boolean running = false;
    float step = 200f;
    float totalSteps = 0f;
    float previousTotalSteps = 0f;

    private static final int CHANGE_LIMIT_STEPS_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        loadData();
        resetSteps();

        btn_addStep.setOnClickListener(v -> {
            totalSteps += Float.parseFloat(tv_stepsTaken.getText().toString())+ step ;
            tv_stepsTaken.setText(String.valueOf(totalSteps));
            circularProgressBar.setProgressWithAnimation(totalSteps, (long) 750);// =1s


        });

        btn_changeStepsLimit.setOnClickListener(v -> {
            Toast.makeText(this,"trying to open....", Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(this, SelectNewStepsLimitActivity.class);
            int previousMaxSteps = (int)circularProgressBar.getProgressMax();
            try {
                intent.putExtra("previousMaxStepValue",previousMaxSteps);
                this.startActivityForResult(intent,CHANGE_LIMIT_STEPS_ACTIVITY_REQUEST_CODE);

            } catch (ActivityNotFoundException e) {

                Log.d("ERROR", "CANT RUN INTENT!"+e);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        running = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null){
            Toast.makeText(this, "No sensor found on this phone", Toast.LENGTH_SHORT).show();
        }
        //else start register steps
        sensorManager.registerListener(this,stepSensor, SensorManager.SENSOR_DELAY_UI);
    }

        public void setupUI(){

        circularProgressBar = findViewById(R.id.circleProgressBar);
        circularProgressBar.setProgressWithAnimation(130f, (long) 1000);// =1s


        btn_addStep = findViewById(R.id.btn_plusStep);

        tv_stepsTaken = findViewById(R.id.tv_stepsTaken);
        tv_stepsDailyLimit = findViewById(R.id.tv_stepsDailyLimit);

        tv_stepsTaken.setText(String.valueOf(totalSteps));
        tv_stepsDailyLimit.setText(String.valueOf((int)circularProgressBar.getProgressMax()));

            btn_changeStepsLimit = findViewById(R.id.btn_changeMaxStepsLimit);

    }


    private void saveData(){
        SharedPreferences sp = getSharedPreferences("mySP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("key1",previousTotalSteps);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sp = getSharedPreferences("mySP", Context.MODE_PRIVATE);
        float savedNum = sp.getFloat("key1",0f);
        Log.d("MainACtivity", "New value loaded: "+ String.valueOf(savedNum));
        previousTotalSteps = savedNum;

    }

    private void resetSteps(){
        tv_stepsTaken.setOnClickListener(v -> {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show();
        });

        tv_stepsTaken.setOnLongClickListener(v -> {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show();
            previousTotalSteps = 0;
            totalSteps = 0;
            saveData();
            circularProgressBar.setProgressWithAnimation(previousTotalSteps, (long)900);
            tv_stepsTaken.setText(String.valueOf(previousTotalSteps));
            return true;
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    if(running){
        totalSteps = event.values[0];
        int currentSteps = (int)totalSteps - (int)previousTotalSteps;

        tv_stepsTaken.setText(String.valueOf(currentSteps));
        circularProgressBar.setProgressWithAnimation((float) currentSteps, (long) 1000);// =1s
    }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHANGE_LIMIT_STEPS_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                String returnedString = data.getStringExtra("NewValue");

                tv_stepsDailyLimit.setText(returnedString);
                circularProgressBar.setProgressMax(Float.parseFloat(returnedString));
                Toast.makeText(this,"Max steps has been changed", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
