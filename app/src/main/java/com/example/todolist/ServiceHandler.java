package com.example.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.UFormat;
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
import java.util.Date;

public class ServiceHandler {

    public static SimpleDateFormat foramt = new SimpleDateFormat("dd/MM/yyyy");

    public static void readFromFileToArr(String filePath, Context context, ArrayList<Task> arr) {
        arr.clear();
        try {
            InputStream inputStream = context.openFileInput(filePath);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    if(!receiveString.equals("")) {
                        String[] strList = receiveString.split("=");
                        Toast.makeText(context, "added node", Toast.LENGTH_SHORT).show();
                        arr.add(new Task(strList[0], strList[1], foramt.parse(strList[2])));
                    }
                }

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
