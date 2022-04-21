package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static boolean hasPerms = false;
    TextView is_perm_tv;
    Button perm_check_btn, reset_tasks_btn, disconnect_btn;
    Spinner sort_mode_sp;
    final int PERM_REQUEST_CODE = 1;

    /*final int NAME_AZ = 1, NAME_ZA = 2, DATE_NEAREST = 3, DATE_LATEST = 4;*/
    enum SortModes {NO_SELECTION, NAME_AZ, NAME_ZA, DATE_NEAREST, DATE_LATEST;

        public static SortModes valueOf(int value) {
            if (value == 1)
                return SortModes.NAME_AZ;
            else if (value == 2)
                return SortModes.NAME_ZA;
            else if (value == 3)
                return SortModes.DATE_NEAREST;
            else if (value == 4)
                return SortModes.DATE_LATEST;
            else
                return SortModes.NO_SELECTION;


        }
    };
    public static SortModes selection = SortModes.NO_SELECTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        is_perm_tv = findViewById(R.id.is_perms_tv);
        perm_check_btn = findViewById(R.id.perms_check_btn);
        sort_mode_sp = findViewById(R.id.sort_mode_sp);
        //reset_tasks_btn = findViewById(R.id.reset_tasks_btn);
        disconnect_btn = findViewById(R.id.disconnect_btn);


        is_perm_tv.setText(checkPermMessage());
        perm_check_btn.setOnClickListener(this);
        sort_mode_sp.setOnItemSelectedListener(this);
        //reset_tasks_btn.setOnClickListener(this);
        disconnect_btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        is_perm_tv.setText(checkPermMessage());
        sort_mode_sp.setSelection(selection.ordinal());
    }

    public String checkPermMessage(){
        if(hasPerms)
            return "Has permissions";
        else
            return "Don't have permissions";
    }

    @Override
    public void onClick(View v) {
        if (v == perm_check_btn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERM_REQUEST_CODE);
                }
            }
        }else if(v == reset_tasks_btn){
            Toast.makeText(this, "reset", Toast.LENGTH_SHORT).show();
            ServiceHandler.resetLocalTasks(this);
            ServiceHandler.setTaskToFirebase(null);
        }else if(v == disconnect_btn){
            ServiceHandler.writeToFileNonAppend("",SettingsActivity.this, "login.txt");
            Intent toLogin = new Intent(this, AccountManagerActivity.class);
            toLogin.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(toLogin, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERM_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SettingsActivity.hasPerms = true;
                    Log.d("Permission State", "Granted");
                    // in your app.
                }  else {
                    Toast.makeText(this, "Permission failed, the app wont work!", Toast.LENGTH_LONG).show();
                    SettingsActivity.hasPerms = false;
                }
                return;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selection = SettingsActivity.SortModes.valueOf(position);
        if(selection != SortModes.NO_SELECTION)
            ServiceHandler.sortList(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "never", Toast.LENGTH_SHORT).show();
    }
}