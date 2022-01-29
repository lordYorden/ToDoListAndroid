package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.todolist.MainActivity.arr;
import static com.example.todolist.AccountManagerActivity.firebaseHandler;

public class DisplayTasksActivity extends AppCompatActivity {

    ListView tasks_lv;
    View task_information_display;
    public static TaskAdapter taskAdapter;
    Boolean isResume;
    final int PERM_REQUEST_CODE = 1;
    FirebaseDatabase database;
    DatabaseReference myRef;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    //dialog
    TextView title_tv, date_tv, description_tv;
    ImageView task_iv;
    ImageButton delete_btn;
    boolean isFirst = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tasks);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(DisplayTasksActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(DisplayTasksActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_REQUEST_CODE);
            }
        }

        dialogBuilder = new AlertDialog.Builder(DisplayTasksActivity.this);
        task_information_display = getLayoutInflater().inflate(R.layout.task_information_display, null);
        title_tv = task_information_display.findViewById(R.id.title_tv);
        date_tv = task_information_display.findViewById(R.id.date_tv);
        description_tv = task_information_display.findViewById(R.id.description_tv);
        task_iv = task_information_display.findViewById(R.id.task_iv);
        delete_btn = task_information_display.findViewById(R.id.delete_btn);

        tasks_lv = findViewById(R.id.tasks_lv);
/*      tasks_lv.setClickable(true);
        tasks_lv.setItemsCanFocus(true);*/
        isResume = false;

        ArrayList<Task> tasks = firebaseHandler.user.getTasks();
        if(tasks == null)
            tasks = new ArrayList<Task>();
        arr = tasks;

        ServiceHandler.resetLocalTasks(this);
        ServiceHandler.addTasksFromArray(arr, this);
/*         ServiceHandler.readFromFileToArr("tasks.txt", this, arr);*/

        taskAdapter = new TaskAdapter(this, 0, 0, arr);
        tasks_lv.setAdapter(taskAdapter);
        tasks_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(DisplayTasksActivity.this, "position "+position, Toast.LENGTH_SHORT).show();
                Task curr = arr.get(position);
                /*Toast.makeText(DisplayTasksActivity.this, String.format("Name: %s, date: %s", curr.task, ServiceHandler.format.format(curr.doDate)), Toast.LENGTH_SHORT).show();*/
                Picasso.get().load(curr.pic).into(task_iv);

                date_tv.setText(ServiceHandler.format.format(curr.doDate));
                title_tv.setText(curr.task);
                description_tv.setText(curr.description);

                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(DisplayTasksActivity.this)
                                .setTitle("Delete Task")
                                .setMessage("Are you sure you want to delete this task?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(DisplayTasksActivity.this, String.format("task %s was deleted", curr.task), Toast.LENGTH_SHORT).show();

                                        firebaseHandler.user.removeTask(position);
                                        //taskAdapter.clear();
                                        //arr.clear();
                                        /*ArrayList<Task> tasks = firebaseHandler.user.getTasks();
                                        if(tasks == null)
                                            tasks = new ArrayList<Task>();
                                        taskAdapter.addAll(tasks);*/
                                        ServiceHandler.resetLocalTasks(DisplayTasksActivity.this);
                                        ServiceHandler.addTasksFromArray(arr, DisplayTasksActivity.this);
                                        ServiceHandler.setTaskToFirebase(arr);
                                        ServiceHandler.sortList(DisplayTasksActivity.this);
                                        //alertDialog.cancel();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                //.setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

                if(!isFirst) {
                    alertDialog.cancel();
                    ((ViewGroup) task_information_display.getParent()).removeView(task_information_display);
                }else
                    isFirst = false;
                dialogBuilder.setView(task_information_display);
                alertDialog = dialogBuilder.create();
                alertDialog.show();


            }
        });

    }

/*    protected void onDestroy() {
        super.onDestroy();
        ServiceHandler.setTaskToFirebase(arr);
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERM_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SettingsActivity.hasPerms = true;
                    Log.d("Permission State", "Granted");
                }  else {
                    Toast.makeText(this, "Permission failed go to settings to toggle again, or else the app wont work!", Toast.LENGTH_LONG).show();
                    SettingsActivity.hasPerms = false;
                }
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isResume)
            return;

        Log.d("App State", "resume");
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
           Intent toEditor = new Intent(this, EditorActivity.class);
           startActivity(toEditor);
           isResume = true;
        } else if(itemId == R.id.settings){
            /*Toast.makeText(this, "Yet To add a Settings menu", Toast.LENGTH_SHORT).show();*/
            Intent toSettings = new Intent(this, SettingsActivity.class);
            startActivity(toSettings);
            isResume = true;
        } else {
            return false;
        }
        return true;
    }

}