package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.PatternSyntaxException;

public class AccountManagerActivity extends AppCompatActivity implements View.OnClickListener {

    View login_v, signup_v;
    Button login_b, signup_b, to_tasks_b;
    EditText login_username_et, login_password_et, signup_username_et, signup_password_et;
    TextView to_sighup_tv,  to_login_tv;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    CheckBox login_cb;
    boolean isStart;
    boolean stayLoggedIn;
    public static FirebaseHandler firebaseHandler;
    InternetConnectionReceiver internetConnectionReceiver = new InternetConnectionReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        Objects.requireNonNull(getSupportActionBar()).hide(); //hides action bar
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetConnectionReceiver, intentFilter);

        /*//activity components
        to_tasks_b = findViewById(R.id.to_tasks_b);
        to_tasks_b.setOnClickListener(this);
*/
        //views and builder
        dialogBuilder = new AlertDialog.Builder(AccountManagerActivity.this);
        login_v = getLayoutInflater().inflate(R.layout.login_layout,null);
        signup_v = getLayoutInflater().inflate(R.layout.signup_layout,null);

        //login components
        login_username_et = login_v.findViewById(R.id.login_username_et);
        login_password_et = login_v.findViewById(R.id.login_password_et);
        login_b = login_v.findViewById(R.id.login_b);
        to_sighup_tv = login_v.findViewById(R.id.to_signup_tv);
        login_cb = login_v.findViewById(R.id.login_cb);

        //signup components
        signup_username_et = signup_v.findViewById(R.id.signup_username_et);
        signup_password_et = signup_v.findViewById(R.id.signup_password_et);
        signup_b = signup_v.findViewById(R.id.signup_b);
        to_login_tv = signup_v.findViewById(R.id.to_login_tv);

        login_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = false;
                if(!login_username_et.getText().toString().isEmpty() && !login_password_et.getText().toString().isEmpty())
                {
                    /*Toast.makeText(AccountManagerActivity.this, "login successful", Toast.LENGTH_SHORT).show();*/
                    stayLoggedIn = login_cb.isChecked();
                    /*Toast.makeText(AccountManagerActivity.this, "your email: "+login_username_et.getText().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(AccountManagerActivity.this, "your password: "+login_password_et.getText().toString(), Toast.LENGTH_SHORT).show();*/
                    firebaseHandler.Login(login_username_et.getText().toString(), login_password_et.getText().toString());
                }
                else
                {
                    Toast.makeText(AccountManagerActivity.this, "Please don't live empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signup_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = false;
                if(!signup_username_et.getText().toString().isEmpty() && !signup_password_et.getText().toString().isEmpty())
                {
                    /*Toast.makeText(AccountManagerActivity.this, "your email: "+signup_username_et.getText().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(AccountManagerActivity.this, "your password: "+signup_password_et.getText().toString(), Toast.LENGTH_SHORT).show();*/
                    firebaseHandler.signup(signup_username_et.getText().toString(), signup_password_et.getText().toString());
                }
                else
                {
                    Toast.makeText(AccountManagerActivity.this, "Please don't live empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        to_sighup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signup_v.getParent() != null) {
                    dialog.cancel();
                    ((ViewGroup) signup_v.getParent()).removeView(signup_v);
                }
                if (dialog != null && dialog.isShowing())
                    dialog.cancel();
                dialogBuilder.setView(signup_v);
                dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        to_login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                ((ViewGroup)login_v.getParent()).removeView(login_v);
                dialogBuilder.setView(login_v);
                dialog = dialogBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        isStart = true;
        firebaseHandler = new FirebaseHandler(AccountManagerActivity.this);
        isAlreadyLogin();
        dialogBuilder.setView(login_v);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {
            return;
        }

        Log.d("State: ","Moving to login Screen");
        disconnect();

        dialog.cancel();
        ((ViewGroup)login_v.getParent()).removeView(login_v);
        dialogBuilder.setView(login_v);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SaveLoginDetails();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(internetConnectionReceiver);
        Log.d("State: ", "App Destroyed");
    }

    public void SaveLoginDetails(){
        if(stayLoggedIn && firebaseHandler.user != null) {
            ServiceHandler.writeToFileNonAppend(String.format("%s=%s", firebaseHandler.user.getUsername(), firebaseHandler.getUser().getPassword()), this, "login.txt");
            Log.d("Debug", "Details Have been saved in the system");
        }
    }

    @Override
    public void onClick(View v) {
        /*if(v == to_tasks_b) {
            *//*Intent to_tasks = new Intent(AccountManagerActivity.this, DisplayTasksActivity.class);
            startActivity(to_tasks);*//*
            createNotificationChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "69")
                    .setSmallIcon(R.drawable.example)
                    .setContentTitle("Due soon")
                    .setContentText("<task name>, Is due soon")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(42, builder.build());
        }*/
    }

    public void isAlreadyLogin(){
        String[] loginDetails;
        String fileLoginDetails = ServiceHandler.readFromFile("login.txt", this);
        if(fileLoginDetails.equals(""))
            return;

        try {
            loginDetails = fileLoginDetails.split("=");
        }
        catch (PatternSyntaxException e){
            Log.e("Login details", e.getMessage());
            return;
        }

        firebaseHandler.Login(loginDetails[0], loginDetails[1]);
    }

    public void disconnect(){
        stayLoggedIn = false;
        firebaseHandler.disconnect();
        ServiceHandler.resetLocalTasks(this);
        ServiceHandler.writeToFileNonAppend("",this, "login.txt");
        isStart = true;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "test";
            String description = "this is just a test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("69", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}