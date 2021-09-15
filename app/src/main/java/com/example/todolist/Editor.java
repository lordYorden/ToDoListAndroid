package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Parcelable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class Editor extends AppCompatActivity implements View.OnClickListener {

    ImageView display_selected;
    Button image_picker_btn;
    Button add_btn;
    TextView display_info;
    Button resetTasks_btn;
    String imagePath;
    EditText task_et, date_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        display_selected = findViewById(R.id.display_selected);
        image_picker_btn = findViewById(R.id.image_pick_btn);
        display_info = findViewById(R.id.display_info);
        add_btn = findViewById(R.id.addToFile_btn);
        task_et = findViewById(R.id.task_et);
        date_et = findViewById(R.id.date_et);
        resetTasks_btn = findViewById(R.id.resetTasks_btn);

        add_btn.setOnClickListener(this);
        image_picker_btn.setOnClickListener(this);
        resetTasks_btn.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                display_info.setText(getRealPathFromURI(imageUri));
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
        if(v == image_picker_btn)
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 0);
        }
        else if (v == add_btn)
        {
            String data = task_et.getText() + "=" + imagePath + "=" + date_et.getText() + "\n";
            writeToFile(data, this);
        }
        else if(v == resetTasks_btn)
        {
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("tasks.txt", MODE_PRIVATE));
                outputStreamWriter.write("");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
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


}