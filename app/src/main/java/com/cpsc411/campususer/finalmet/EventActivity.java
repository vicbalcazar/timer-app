package com.cpsc411.campususer.finalmet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class EventActivity extends Activity {
    private String metTime;

    // deleted resultcode
    int RESULT_DELETED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //get references to widgets
        final TimePicker metTimePicker = findViewById(R.id.metTimePicker);
        final EditText metTextView = findViewById(R.id.metTextView);
        final EditText descriptionTextView = findViewById(R.id.editText);

        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        //get the intent
        Intent intent = getIntent();
        //get data from the intent
        metTime = intent.getStringExtra("met");
        final String description =
                intent.getStringExtra("description").replace('\n', ' ');

        //Set textviews
        metTextView.setText(metTime);
        descriptionTextView.setText(description);

        //Set timepicker to 24hour mode
        metTimePicker.setIs24HourView(true);

        //set listeners
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //return Intent with the new data

                Intent returnIntent = new Intent();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR, metTimePicker.getHour());
                cal.set(Calendar.MINUTE, metTimePicker.getMinute());
                long offset = cal.getTimeInMillis() + System.currentTimeMillis();
                metTime = Long.toString(offset);

                returnIntent.putExtra("hour", metTimePicker.getHour());
                returnIntent.putExtra("minute", metTimePicker.getMinute());
                returnIntent.putExtra("goal", metTime);
                returnIntent.putExtra("start", System.currentTimeMillis());
                returnIntent.putExtra("description",
                        descriptionTextView.getText().toString());
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do nothing and exit activity
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do nothing and exit activity
                //3 is result code for discard. custom
                setResult(RESULT_DELETED);
                finish();
            }
        });
    }
}
