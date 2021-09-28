package com.example.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ServiceHandler {

    public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public static Bitmap fixPictureRotation(Task curr) throws FileNotFoundException
    {
        Bitmap picToRotate = ServiceHandler.imageFromFile(curr.pic);
        Matrix matrix = new Matrix();
        int rotation = ServiceHandler.getCameraPhotoOrientation(curr.pic);
        if(rotation == 270)
            matrix.postRotate(-90);
        return Bitmap.createBitmap(picToRotate, 0, 0, picToRotate.getWidth(), picToRotate.getHeight(), matrix, true);
    }

    public static int getCameraPhotoOrientation(String imagePath)
    {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static void readFromFileToArr(String filePath, Context context, ArrayList<Task> arr) {

        arr.clear();
        try {
            InputStream inputStream = context.openFileInput(filePath);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                 do{
                    receiveString = bufferedReader.readLine();
                    if(receiveString != null && !receiveString.equals("")) {
                        String[] strList = receiveString.split("=");
                        /*Toast.makeText(context, "added node", Toast.LENGTH_SHORT).show();*/
                        arr.add(new Task(strList[0], strList[1], format.parse(strList[2])));
                    }
                } while (receiveString != null );
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("File search", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File read", "Can not read file: " + e.toString());
        } catch (ParseException e) {
            Log.e("Date format", "not a valid date"+e.toString());
        }
    }

    public static Bitmap imageFromFile(String filePath) throws FileNotFoundException
    {
        File imgFile = new File(filePath);
        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
            if(myBitmap != null)
            {
                return myBitmap;
            }
            else
            {
                throw new FileNotFoundException("Could not find " + filePath);
            }
        }
        else
        {
            throw new FileNotFoundException("Could not find " + filePath);
        }
    }

}
