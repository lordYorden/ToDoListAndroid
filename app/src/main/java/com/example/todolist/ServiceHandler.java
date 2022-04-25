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

    //defines the date format for displayed tasks
    public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");


    /**
     * this method gets a task with an image from a file
     * and return the bitmap image rotated correctly
     * @param curr the current task
     * @param context the current screen to display an alert if needed
     * @return image bitmap rotated correctly
     */
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

    /**
     * this method gets an image from a file
     * and return the bitmap image rotated correctly
     * @param imagePath image to rotate
     * @param context the current screen to display an alert if needed
     * @return image bitmap rotated correctly
     */
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


    /**
     * helper for functions above
     * uses an android query to get the curr rotation of an image
     * @param imagePath image storage location
     * @return the image rotation as an integer value
     */
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

    /**
     * used to add task to local storage (task.txt)
     * @param tasks array of tasks to upload
     * @param context used for debugging purposes (prints a Toast)
     */
    public static void addTasksFromArray(ArrayList<Task> tasks, Context context){
        /*String data = "";*/
        for(Task task : tasks){
            /*data = task.task + "$" + task.pic + "$" + format.format(task.doDate) + "$" + task.isFin + "\n";*/
            /*Toast.makeText(context, data, Toast.LENGTH_SHORT).show();*/
            writeToFile(task.toString(), context, "tasks.txt");
        }
    }

    /**
     * uploads a single task to firebase's real time storage
     * @param task task to upload
     */
    public static void addTaskToFirebase(Task task){
        Account user = firebaseHandler.user;
        ArrayList<Task> tasks = user.getTasks();
        if(tasks == null)
            tasks = new ArrayList<Task>();
        tasks.add(task);
        user.setTasks(tasks);
        FirebaseHandler.db.getReference(String.format("users/%s/tasks", user.getUsername())).setValue(user.getTasks());
    }

    /**
     * changes the task segment in firebase's real time storage
     * to a given task array
     * @param tasks tasks to set
     */
    public static void setTaskToFirebase(ArrayList<Task> tasks){
        Account user = firebaseHandler.user;
        user.setTasks(tasks);
        FirebaseHandler.db.getReference(String.format("users/%s/tasks", user.getUsername())).setValue(user.getTasks());
    }

    /**
     * helper method writes information
     * to the desired file path (mode append)
     * @param data data to write
     * @param context used for debugging purposes (prints a Toast) in case of a failiure
     * @param filePath file to write into
     */
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

    /**
     * helper method writes information
     * to the desired file path (overwrite current info)
     * @param data data to write
     * @param context used for debugging purposes (prints a Toast) in case of a failiure
     * @param filePath file to write into
     */
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

    /**
     * uses the function above to delete tasks in local storage (tasks.txt)
     * @param context used for debugging purposes (prints a Toast) in case of a failiure
     */
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

    /**
     * takes task from local storage (tasks.txt)
     * and inserts them into the desired array
     * @param filePath file to read tasks from
     * @param context used for debugging purposes (prints a Toast) in case of a failiure
     * @param arr array to put the tasks in
     */
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

    /**
     * helper method
     * reads the data from a desired file
     * @param filePath file to read from
     * @param context used for debugging purposes (prints a Toast) in case of a failiure
     * @return data read from file
     */
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


    /**
     * sorts the data in the task array (arr)
     * @param context used for debugging purposes (prints a Toast) in case of a failiure
     */
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

    /**
     * helper method
     * returns an image given its path
     * @param filePath the image path
     * @return bitmap image
     * @throws FileNotFoundException in case of image not found
     */
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
