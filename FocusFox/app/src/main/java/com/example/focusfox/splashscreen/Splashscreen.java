package com.example.focusfox.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focusfox.mainscreen.MainActivity;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This launches the main activity from the splash screen when the 2 seconds is complete
        //The actual splash screen may be longer due the splashscreen covering a cold start
        new Handler().postDelayed(() -> {
            Intent splash = new Intent(Splashscreen.this, MainActivity.class);
            startActivity(splash);
            finish();
        }, 2000);
    }
}
