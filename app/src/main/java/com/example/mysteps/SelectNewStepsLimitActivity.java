package com.example.mysteps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webianks.library.scroll_choice.ScrollChoice;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SelectNewStepsLimitActivity extends AppCompatActivity {

    TextView message_text;
    List<String> SuggestedValues = new ArrayList<>();
    ScrollChoice scrollChoice;
    Button btnSelectNewValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_new_steps_limit);

        int defaultMaxStepValue;
        if (savedInstanceState ==null){
            Bundle extras = getIntent().getExtras();
            if (extras==null){
                defaultMaxStepValue =-1; // null
            }else{
                defaultMaxStepValue = extras.getInt("previousMaxStepValue");
            }
        }else{
            defaultMaxStepValue= (int) savedInstanceState.getSerializable("previousMaxStepValue");
        }


        
        
        initViews();
        loadData(defaultMaxStepValue);

        scrollChoice.addItems(SuggestedValues,2);
        scrollChoice.setOnItemSelectedListener((scrollChoice, position, name) -> {
            Toast.makeText(this,"Selected item is " +name,Toast.LENGTH_SHORT).show();

            btnSelectNewValue.setOnClickListener(v -> {

                Toast.makeText(this,"SENDING!", Toast.LENGTH_SHORT).show();
                // Put the String to pass back into an Intent and close this activity
                Intent intentOut = new Intent();
                intentOut.putExtra("NewValue", name);
                setResult(RESULT_OK, intentOut);
                finish();

            });

        });

        




    }

    private void initViews() {

        btnSelectNewValue = findViewById(R.id.btn_select_new_value);
        scrollChoice = findViewById(R.id.scrool_choice);
    }

    private void loadData(int defaultMaxStepValue) {
        for (int i = 1; i <= 10; i++) {
            if(i >= 5){
                SuggestedValues.add(String.valueOf(defaultMaxStepValue + (i*200)));
            }else{
                SuggestedValues.add(String.valueOf(defaultMaxStepValue - (i*200)));
            }

        }

    }
}
