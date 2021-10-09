package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.todolist.MainActivity.arr;

public class DisplayTasks extends AppCompatActivity {

    ListView tasks_lv;
    public static TaskAdapter taskAdapter;
    Boolean isResume;
    final int PERM_REQUEST_CODE = 1;
    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tasks);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(DisplayTasks.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(DisplayTasks.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_REQUEST_CODE);
            }
        }

        tasks_lv = findViewById(R.id.tasks_lv);
        isResume = false;

        ServiceHandler.readFromFileToArr("tasks.txt", this, arr);
        taskAdapter = new TaskAdapter(this, 0, 0, arr);
        tasks_lv.setAdapter(taskAdapter);

        Account account = new Account("yarden", "1234", arr);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("users");
        myRef.push().setValue(account);


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
                    Toast.makeText(this, "Permission failed go to settings to toggle again, or else the app wont work!", Toast.LENGTH_LONG).show();
                    Settings.hasPerms = false;
                }
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isResume)
            return;

        Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();
        taskAdapter.clear();
        arr.clear();
        ArrayList<Task> temp = new ArrayList<Task>();
        ServiceHandler.readFromFileToArr("tasks.txt", this, temp);
        taskAdapter.addAll(temp);
        ServiceHandler.sortList(this);
        isResume = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.testmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId =  item.getItemId();
        if(itemId == R.id.editor){
           Intent toEditor = new Intent(this, Editor.class);
           startActivity(toEditor);
           isResume = true;
        } else if(itemId == R.id.settings){
            /*Toast.makeText(this, "Yet To add a Settings menu", Toast.LENGTH_SHORT).show();*/
            Intent toSettings = new Intent(this, Settings.class);
            startActivity(toSettings);
            isResume = true;
        } else {
            return false;
        }
        return true;
    }

}