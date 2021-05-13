package com.example.focusfox.mainscreen;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.focusfox.R;
import com.example.focusfox.database.DBManager;
import com.example.focusfox.journal.Journal;
import com.example.focusfox.planner.Planner;
import com.example.focusfox.settings.Settings;
import com.example.focusfox.timer.Timer;

public class MainActivity extends AppCompatActivity{

    //create an instance of DBManager
    private DBManager dbManager;

    //Declaring the variables to be used for the activity
    TextView quotes;
    private SimpleCursorAdapter quoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting XML file for MainActivity
        setContentView(R.layout.activity_main);

        //SharedPreferences are used to set app in light or night mode depending on option chosen in settings.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = sharedPreferences.getBoolean(getString(R.string.nightMode), false);
        if (nightMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        //Open the database connection
        dbManager = new DBManager(this);
        dbManager.open();

        //Get the number of entries within the quote table
        int numQuote = dbManager.quote_count();

        //Initialising the quotes TextView for displaying a random quote
        quotes = (TextView)findViewById(R.id.quoteView);

        //if there are no quotes available, guide user to add their own in the settings
        if (numQuote == 0){
            quotes.setText("There are currently no quotes to display, add your own in the settings above.");
        }
        //grab a random quote and set the quote TextView to the random quote
        else{
            quotes.setText(dbManager.getRandomData());
        }
        //close the database connection
        dbManager.close();
    }

    //Create the menu with settings options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    //This function responds to the Planner button being clicked and opens the Planner activity
    public void openPlanner(View view){
        Intent plannerIntent = new Intent(this, Planner.class);
        plannerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(plannerIntent);
    }

    //This function responds to the Journal button being clicked and opens the Journal activity
    public void openJournal(View view){
        Intent journalIntent = new Intent(this, Journal.class);
        journalIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(journalIntent);
    }
    //This function responds to the Timer button being clicked and opens the Timer activity
    public void openTimer(View view){
        Intent timerIntent = new Intent(this, Timer.class);
        timerIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(timerIntent);
    }
    //This function responds to the Settings option being clicked and opens the settings fragment
    public void openSettings(MenuItem item) {
        Intent settingsIntent = new Intent(this, Settings.class);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(settingsIntent);
    }
} //end of programme