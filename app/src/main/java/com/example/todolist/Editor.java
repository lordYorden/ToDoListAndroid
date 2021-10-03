package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class Editor extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    ImageButton display_selected;
    Button add_btn;
    TextView display_info, date_selector_tv;
   /* Button resetTasks_btn;*/
    String imagePath;
    EditText task_et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        getSupportActionBar().hide();

        display_selected = findViewById(R.id.display_selected);
        /*display_info = findViewById(R.id.display_info);*/
        add_btn = findViewById(R.id.addToFile_btn);
        task_et = findViewById(R.id.task_et);
        date_selector_tv = findViewById(R.id.date_selector_tv);
        /*resetTasks_btn = findViewById(R.id.reset_tasks_btn);*/

        add_btn.setOnClickListener(this);
        display_selected.setOnClickListener(this);
        /*resetTasks_btn.setOnClickListener(this);*/
        date_selector_tv.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                /*display_info.setText(getRealPathFromURI(imageUri));*/
                imagePath = getRealPathFromURI(imageUri);
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                display_selected.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onClick(View v) {
        if(v == display_selected)
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 0);
        }
        else if (v == add_btn)
        {
            if(date_selector_tv.getText() == ""){
                Toast.makeText(this, "Select date first!", Toast.LENGTH_SHORT).show();
                return;
            }

            String data = task_et.getText() + "=" + imagePath + "=" + date_selector_tv.getText() + "\n";
            writeToFile(data, this);
            Toast.makeText(this, "Task was added!", Toast.LENGTH_SHORT).show();
        }
        /*else if(v == resetTasks_btn)
        {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("tasks.txt", MODE_PRIVATE));
                outputStreamWriter.write("");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }*/
        else if(v == date_selector_tv){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tasks.txt", MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date_selector_tv.setText(String.format("%d/%d/%d", dayOfMonth, month+1, year));
    }
}