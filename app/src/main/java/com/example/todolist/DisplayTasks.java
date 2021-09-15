package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.todolist.MainActivity.arr;

public class DisplayTasks extends AppCompatActivity {

    ListView tasks_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tasks);

        tasks_lv = findViewById(R.id.tasks_lv);

        ServiceHandler.readFromFileToArr("tasks.txt", this, arr);
        TaskAdapter taskAdapter = new TaskAdapter(this, 0, 0, arr);
        tasks_lv.setAdapter(taskAdapter);

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
        } else if(itemId == R.id.settings){
            Toast.makeText(this, "Yet To add a Settings menu", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
    }
}