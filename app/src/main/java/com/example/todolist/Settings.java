package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    public static boolean hasPerms = false;
    TextView is_perm_tv;
    Button perm_check_btn;
    final int PERM_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        is_perm_tv = findViewById(R.id.is_perms_tv);
        perm_check_btn = findViewById(R.id.perms_check_btn);
        is_perm_tv.setText(checkPermMessage());
        perm_check_btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        is_perm_tv.setText(checkPermMessage());
    }

    public String checkPermMessage(){
        if(hasPerms)
            return "Has permissions";
        else
            return "Don't have permissions";
    }

    @Override
    public void onClick(View v) {
        if (v == perm_check_btn)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission( Settings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_REQUEST_CODE);
                }
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERM_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Settings.hasPerms = true;
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    // in your app.
                }  else {
                    Toast.makeText(this, "Permission failed, the app wont work!", Toast.LENGTH_LONG).show();
                    Settings.hasPerms = false;
                }
                return;
        }
    }
}