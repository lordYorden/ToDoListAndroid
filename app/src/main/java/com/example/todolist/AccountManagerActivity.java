package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);

        //activity components
        to_tasks_b = findViewById(R.id.to_tasks_b);
        to_tasks_b.setOnClickListener(this);

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
                    Toast.makeText(AccountManagerActivity.this, "your email: "+login_username_et.getText().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(AccountManagerActivity.this, "your password: "+login_password_et.getText().toString(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AccountManagerActivity.this, "your email: "+signup_username_et.getText().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(AccountManagerActivity.this, "your password: "+signup_password_et.getText().toString(), Toast.LENGTH_SHORT).show();
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
                if(!isStart) {
                    dialog.cancel();
                    ((ViewGroup) signup_v.getParent()).removeView(signup_v);
                }else
                    isStart = false;
                dialogBuilder.setView(signup_v);
                dialog = dialogBuilder.create();
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
                dialog.show();
            }
        });

        isStart = true;
        firebaseHandler = new FirebaseHandler(AccountManagerActivity.this);
        isAlreadyLogin();
        dialogBuilder.setView(login_v);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {
            return;
        }

        Toast.makeText(this, "toLogin", Toast.LENGTH_SHORT).show();
        disconnect();

        dialog.cancel();
        ((ViewGroup)login_v.getParent()).removeView(login_v);
        dialogBuilder.setView(login_v);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(stayLoggedIn) {
            ServiceHandler.writeToFileNonAppend(String.format("%s=%s", firebaseHandler.user.getUsername(), firebaseHandler.getUser().getPassword()), this, "login.txt");
            Toast.makeText(this, "write login", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v == to_tasks_b) {
            Intent to_tasks = new Intent(AccountManagerActivity.this, DisplayTasksActivity.class);
            startActivity(to_tasks);
        }
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
}