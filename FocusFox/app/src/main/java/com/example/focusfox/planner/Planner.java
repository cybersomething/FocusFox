package com.example.focusfox.planner;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focusfox.R;

public class Planner extends AppCompatActivity implements View.OnClickListener {

    //Declaring the variables to be used for the activity
    private Button addToCalendar;
    private EditText titleEditText;
    private EditText detailsEditText;
    private EditText locationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add New Event");
        setContentView(R.layout.activity_add_event);

        //Assigning values to the variables
        titleEditText = (EditText) findViewById(R.id.eventTitle);
        detailsEditText = (EditText) findViewById(R.id.eventDetails);
        locationEditText = (EditText) findViewById(R.id.eventLocation);
        addToCalendar = (Button) findViewById(R.id.addToCalendar);

        //initialise onClickListeners for the addToCalendar button
        addToCalendar.setOnClickListener(this);
    }

    //When the addToCalendar button is clicked, send the user and entered information to google calendars
    @Override
    public void onClick(View view) {
        //Using an intent to send the entered information to the calendar app with the user
        Intent intent = new Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, titleEditText.getText().toString())
            .putExtra(CalendarContract.Events.DESCRIPTION, detailsEditText.getText().toString())
            .putExtra(CalendarContract.Events.EVENT_LOCATION, locationEditText.getText().toString())
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        startActivity(intent);
    }
}