package com.example.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static com.example.todolist.DisplayTasksActivity.taskAdapter;
import static com.example.todolist.MainActivity.arr;
import static com.example.todolist.AccountManagerActivity.firebaseHandler;

public class ServiceHandler {

    public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public static Bitmap fixPictureRotation(Task curr, Context context)
    {
        Bitmap picToRotate = null;
        try {
            picToRotate = ServiceHandler.imageFromFile(curr.pic);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(context, "Image dose not exist anymore...Sorry :(", Toast.LENGTH_SHORT).show();
        }
/*        catch(IOException e) {
          Log.e("Download image", e.getMessage()) ;
        }*/

        Matrix matrix = new Matrix();
        int rotation = ServiceHandler.getCameraPhotoOrientation(curr.pic);
        if(rotation == 270)
            matrix.postRotate(-90);
        return Bitmap.createBitmap(picToRotate, 0, 0, picToRotate.getWidth(), picToRotate.getHeight(), matrix, true);
    }

    public static Bitmap fixPictureRotation(String imagePath, Context context) throws FileNotFoundException {
        Bitmap picToRotate = null;
        picToRotate = ServiceHandler.imageFromFile(imagePath);
/*        catch(IOException e) {
          Log.e("Download image", e.getMessage()) ;
        }*/

        Matrix matrix = new Matrix();
        int rotation = ServiceHandler.getCameraPhotoOrientation(imagePath);
        if(rotation == 270)
            matrix.postRotate(-90);
        else if(rotation == 90)
            matrix.postRotate(90);
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

    public static void addTasksFromArray(ArrayList<Task> tasks, Context context){
        /*String data = "";*/
        for(Task task : tasks){
            /*data = task.task + "$" + task.pic + "$" + format.format(task.doDate) + "$" + task.isFin + "\n";*/
            /*Toast.makeText(context, data, Toast.LENGTH_SHORT).show();*/
            writeToFile(task.toString(), context, "tasks.txt");
        }
    }

    public static void addTaskToFirebase(Task task){
        Account user = firebaseHandler.user;
        ArrayList<Task> tasks = user.getTasks();
        if(tasks == null)
            tasks = new ArrayList<Task>();
        tasks.add(task);
        user.setTasks(tasks);
        FirebaseHandler.db.getReference(String.format("users/%s/tasks", user.getUsername())).setValue(user.getTasks());
    }

    public static void setTaskToFirebase(ArrayList<Task> tasks){
        Account user = firebaseHandler.user;
        user.setTasks(tasks);
        FirebaseHandler.db.getReference(String.format("users/%s/tasks", user.getUsername())).setValue(user.getTasks());
    }

    public static void writeToFile(String data, Context context, String filePath) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filePath, MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeToFileNonAppend(String data, Context context, String filePath){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filePath, MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void resetLocalTasks(Context context){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("tasks.txt", MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
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
                        String[] strList = receiveString.split("\\$");
                        /*Toast.makeText(context, "added node", Toast.LENGTH_SHORT).show();*/
                        arr.add(new Task(strList[0], strList[1], format.parse(strList[2]), strList[3] ,Boolean.parseBoolean(strList[4])));
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

    public static String readFromFile(String filePath, Context context) {
        try {
            InputStream inputStream = context.openFileInput(filePath);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                receiveString = bufferedReader.readLine();
                if(receiveString != null)
                    return receiveString.replaceAll("\n", "");
                else
                    return "";
            } else
                return "";
        }
        catch (FileNotFoundException e) {
            Log.e("File search", "File not found: " + e.toString());
            return "";
        } catch (IOException e) {
            Log.e("File read", "Can not read file: " + e.toString());
            return "";
        }
    }


    public static void sortList (Context context){
        switch (SettingsActivity.selection){
            case NAME_AZ:
                Toast.makeText(context, "a-z", Toast.LENGTH_SHORT).show();
                if(!arr.isEmpty()) {
                    Collections.sort(arr, (a, b) -> a.task.compareTo(b.task));
                }
                break;
            case NAME_ZA:
                Toast.makeText(context, "z-a", Toast.LENGTH_SHORT).show();
                if(!arr.isEmpty()) {
                    Collections.sort(arr, (a, b) -> b.task.compareTo(a.task));
                }
                break;
            case DATE_LATEST:
                Toast.makeText(context, "date latest", Toast.LENGTH_SHORT).show();
                if(!arr.isEmpty()) {
                    Collections.sort(arr, (a, b) -> a.doDate.compareTo(b.doDate));
                }
                break;
            case DATE_NEAREST:
                Toast.makeText(context, "date nearest", Toast.LENGTH_SHORT).show();
                if(!arr.isEmpty()) {
                    Collections.sort(arr, (a, b) -> b.doDate.compareTo(a.doDate));
                }
                break;
            case NO_SELECTION:
                break;
            default:
                Toast.makeText(context, "How?", Toast.LENGTH_SHORT).show();
        }
        taskAdapter.notifyDataSetChanged();
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
