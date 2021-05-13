package com.example.focusfox.timer;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.focusfox.R;

import java.util.Locale;

public class Timer extends AppCompatActivity implements View.OnClickListener {

    //Declaring the variables to be used for the activity
    private NumberPicker minuteTimerPicker;
    private NumberPicker secondTimerPicker;
    private TextView countdownView;
    private Button startPause;
    private Button reset;
    private Button set;
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    int minuteInput;
    int secondInput;
    private long startTimeMillis;
    private long timeLeftMillis;
    private long endTime;

    //Declaring the variables to be used for the notification
    public static final String channelId = "timer" ;
    private final static String defaultChannelId = "timer" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting XML file for Timer Activity
        setContentView(R.layout.activity_timer);
        //Setting title in title bar to Set a Study Duration
        setTitle("Set a Study Duration");

        //Assigning values to the variables
        countdownView = findViewById(R.id.text_view_countdown);
        minuteTimerPicker = findViewById(R.id.minuteTimerPicker);
        secondTimerPicker = findViewById(R.id.secondTimerPicker);
        startPause = findViewById(R.id.button_start_pause);
        reset = findViewById(R.id.button_reset);
        set = findViewById(R.id.button_set);

        //initialise onClickListeners for the start, pause, reset and set buttons
        startPause.setOnClickListener(this);
        reset.setOnClickListener(this);
        set.setOnClickListener(this);

        //Setting the minimum and maximum values for the minutes number picker (0-60 minutes).
        minuteTimerPicker.setMaxValue(60);
        minuteTimerPicker.setMinValue(0);

        //Setting the minimum and maximum values for the seconds number picker (0-59 seconds).
        secondTimerPicker.setMaxValue(59);
        secondTimerPicker.setMinValue(0);

        //Setting the values of the picker input to 0.
        minuteInput = 0;
        secondInput = 0;

        //Setting the listener for the minute number picker
        //when the value is changed in the picker, change the minute picker input to the value
        minuteTimerPicker.setOnValueChangedListener((picker, oldVal, newVal) -> minuteInput = newVal);

        //setting the listener for the second number picker
        //when the value is changed in the picker, change the seconds picker input to the value
        secondTimerPicker.setOnValueChangedListener((picker, oldVal, newVal) -> secondInput = newVal);
    }

    //Function to set alarmManager to schedule notification for timer running out
    private void scheduleNotification (Notification notification, int delay){
        Intent notificationIntent = new Intent(this, TimerNotif.class);
        notificationIntent.putExtra(TimerNotif.NOTIFICATION_ID, 1 );
        notificationIntent.putExtra(TimerNotif.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock. elapsedRealtime () + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    //Function containing the information to be displayed to user in notification.
    private Notification getNotification (String content) {
        //Intent for user to open study timer from notification
        Intent alarmIntent = new Intent(this, Timer.class);
        PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //building the notification with the required information
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, defaultChannelId )
                .setContentTitle("Time's Up!")
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setSmallIcon(R.drawable.focusfox_transparent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setChannelId(channelId)
                .setContentIntent(alarmPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        return builder.build();
    }

    //When timer is reset, alarm is cancelled
    public void stopAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, TimerNotif.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarmManager.cancel(alarmPendingIntent);
    }

    //Setting the onClick functions for each button
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //if the user clicks the start/pause button
            case R.id.button_start_pause:
                if (timerRunning) {
                    //if the timer is running, run the pauseTimer function
                    pauseTimer();
                } else {
                    //else, start the timer
                    startTimer();
                }
                break;
            //if the reset button is clicked
            case R.id.button_reset:
                //reset the timer
                resetTimer();
                break;
            //if the set button is clicked
            case R.id.button_set:
                //set the timer to the minute and seconds from the number picker
                long millisInput = ((minuteInput) * 60000) + ((secondInput) * 1000);
                setTime(millisInput);
        }
    }

    //Set time function to set the timer.
    private void setTime(long millisInput) {
        //set the timer to the sum of the minuteInput and secondInputs added
        startTimeMillis = millisInput;
        resetTimer();
    }

    //Start Timer function.
    private void startTimer() {
        //Calculate the exact end time of the timer
        endTime = System.currentTimeMillis() + timeLeftMillis;
        //schedule the notification to fire when the alarm is complete
        scheduleNotification(getNotification("Time to take a break! Come back when you're ready to study again :)") , Math.toIntExact(timeLeftMillis));
        //set the countdown timer to the requested time and to count down each second
        countDownTimer = new CountDownTimer(timeLeftMillis, 1000) {
            //update the time left each second
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateCountDownText();
            }
            //when the timer is finished, stop the timer and wait for user to reset/start timer again
            @Override
            public void onFinish() {
                timerRunning = false;
                updateWatchInterface();
            }
        }.start();
        timerRunning = true;
        updateWatchInterface();
    }

    //Pause timer function
    private void pauseTimer() {
        //Stop counting down each second and freeze the timer text view
        countDownTimer.cancel();
        timerRunning = false;
        updateWatchInterface();
    }

    //Reset timer function
    private void resetTimer() {
        //stop the countdown and set the timer to the starting time
        timeLeftMillis = startTimeMillis;
        updateCountDownText();
        updateWatchInterface();
        stopAlarm(this);
    }

    //updateCountDownText function
    private void updateCountDownText() {
        //each second the countdown text is calculated and refreshed
        int hours = (int) (timeLeftMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftMillis / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        countdownView.setText(timeLeftFormatted);
    }

    //This function allows the timer to continue if the user leaves the app
    @Override
    protected void onStop() {
        super.onStop();
        //The time is stored within shared preferences to be compared to the next time the app is opened
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", startTimeMillis);
        editor.putLong("millisLeft", timeLeftMillis);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putLong("endTime", endTime);
        editor.apply();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //the time that was stored in shared preferences is retrieved
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        startTimeMillis = prefs.getLong("startTimeInMillis", 600000);
        timeLeftMillis = prefs.getLong("millisLeft", startTimeMillis);
        timerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateWatchInterface();
        //check to see if the timer is still going to be running
        if (timerRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftMillis = endTime - System.currentTimeMillis();
            if (timeLeftMillis < 0) {
                //if the timer has finished while the user was away, stop the timer
                timeLeftMillis = 0;
                timerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                //continue the timer from the current countdown
                startTimer();
            }
        }
    }

    //This function is used to hide and show buttons and number pickers when required
    private void updateWatchInterface() {
        if (timerRunning) {
            //when timer is running:
            //set numberpickers, set and reset button to invisible and set start/pause button to pause
            minuteTimerPicker.setVisibility(View.INVISIBLE);
            secondTimerPicker.setVisibility(View.INVISIBLE);
            set.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
            startPause.setText("Pause");
        } else {
            //when timer is not running
            //set numberpickers, set button and set start/pause button to start
            minuteTimerPicker.setVisibility(View.VISIBLE);
            secondTimerPicker.setVisibility(View.VISIBLE);
            set.setVisibility(View.VISIBLE);
            startPause.setText("Start");
            //if the timer is at 00:00, the start/pause button has been hidden
            if (timeLeftMillis < 1000) {
                startPause.setVisibility(View.INVISIBLE);
            } else {
                //once the timer has been set, the button is visible
                startPause.setVisibility(View.VISIBLE);
            }
            if (timeLeftMillis < startTimeMillis) {
                //When timer is paused, show reset
                reset.setVisibility(View.VISIBLE);
            } else {
                //when timer is running, hide reset
                reset.setVisibility(View.INVISIBLE);
            }
        }
    }
}