package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class CreditsActivity extends AppCompatActivity {

    ScrollingTextView credits_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hides action bar

        credits_tv = findViewById(R.id.credits_tv);
        credits_tv.setSpeed((float) 2);
    }
}