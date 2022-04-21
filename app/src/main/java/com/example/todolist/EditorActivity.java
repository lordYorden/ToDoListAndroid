package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    ImageButton display_selected;
    Button add_btn;
    TextView display_info, date_selector_tv;
   /* Button resetTasks_btn;*/
    String imagePath;
    EditText task_et, description_et;
    Uri image;
    enum reqCodes {GALLERY, CAMERA, NO_OPERATION;

        public static reqCodes valueOf(int value) {
            if (value == 0)
                return reqCodes.GALLERY;
            else if (value == 1)
                return reqCodes.CAMERA;
            else
                return reqCodes.NO_OPERATION;
        }


    };


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
        description_et = findViewById(R.id.description_et);
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
            if(reqCodes.valueOf(reqCode) == reqCodes.CAMERA){
                Uri imageUri = data.getData();
                image = imageUri;
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                display_selected.setImageBitmap(photo);
                scaleImage(display_selected);

            }
            else if(reqCodes.valueOf(reqCode) == reqCodes.GALLERY) {

                try {
                    Uri imageUri = data.getData();
                    image = imageUri;
                    /*display_info.setText(getRealPathFromURI(imageUri));*/
                    imagePath = getRealPathFromURI(imageUri);
                /*InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);*/
                    display_selected.setImageBitmap(ServiceHandler.fixPictureRotation(imagePath, EditorActivity.this));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

        } else {
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
            /*Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE*//*Intent.ACTION_PICK*//*);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 0);*/
            selectImage(EditorActivity.this);
        }
        else if (v == add_btn)
        {
            if(date_selector_tv.getText().equals("") || task_et.getText().toString().equals("")){
                Toast.makeText(this, "Select Date and name the task first!", Toast.LENGTH_SHORT).show();
                return;
            }

            /*String data = task_et.getText() + "=" + imagePath + "=" + date_selector_tv.getText() + "\n";
            ServiceHandler.writeToFile(data, this, "tasks.txt");*/
            Uri toUpload = getImageUri(EditorActivity.this, ((BitmapDrawable)display_selected.getDrawable()).getBitmap());
            try {
                Task task = new Task(task_et.getText().toString(), "",  ServiceHandler.format.parse(date_selector_tv.getText().toString()), description_et.getText().toString());
                FirebaseHandler.uploadImage(task, AccountManagerActivity.firebaseHandler.user.getUsername(), toUpload,  this);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(EditorActivity.this, "task failed", Toast.LENGTH_SHORT).show();
            }

            /*try {
                ServiceHandler.addTaskToFirebase(new Task(task_et.getText().toString(), imagePath, ServiceHandler.format.parse(date_selector_tv.getText().toString())));
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "task failed", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Task was added!", Toast.LENGTH_SHORT).show();*/
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

    private void selectImage(Context context)
    {
        final CharSequence[] options = {"Capture Photo", "Select from gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which)
                {
                    case 0:
                        Intent cameraPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraPickerIntent, reqCodes.CAMERA.ordinal());
                        dialog.dismiss();
                        break;
                    case 1:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, reqCodes.GALLERY.ordinal());
                        dialog.dismiss();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date_selector_tv.setText(String.format("%d/%d/%d", dayOfMonth, month+1, year));
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        /*String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);*/
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "null");
        Uri path = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        OutputStream imageOut = null;

        try {
            imageOut = getContentResolver().openOutputStream(path);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, imageOut/*bytes*/);
            imageOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    //code from https://stackoverflow.com/questions/8232608/fit-image-into-imageview-keep-aspect-ratio-and-then-resize-imageview-to-image-d
    private void scaleImage(ImageButton view) throws NoSuchElementException  {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}