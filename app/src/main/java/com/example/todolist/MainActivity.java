package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static ArrayList<Task> arr = new ArrayList<Task>();
    ImageView pic;
    CheckBox check_btn;
    Button reset_btn;
    Button editor_switch_btn;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pic = findViewById(R.id.display_iv);
        check_btn = findViewById(R.id.checkbox_btn);
        reset_btn = findViewById(R.id.reset_btn);
        editor_switch_btn = findViewById(R.id.editor_switch_btn);

        check_btn.setOnClickListener(this);
        reset_btn.setOnClickListener(this);
        editor_switch_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == check_btn) {
            displayNextNode();
            i++;
            if (i >= arr.size()) {
                i = 0;
            }

        }
        else if (v == reset_btn) {
            ServiceHandler.readFromFileToArr("tasks.txt", this, arr);
            displayNextNode();
        }
        else if (v == editor_switch_btn) {
            Intent main_to_editor = new Intent(this, Editor.class);
            startActivity(main_to_editor);
        }
    }

    /*public Bitmap imageFromFile(String filePath) throws FileNotFoundException
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
    }*/

   /* private void readFromFileToLinkedList(String filePath, Context context) {
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
                        arr.add(new TodoNode(strList[0], strList[1]));
                    }
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }*/

    public void displayNextNode()
    {
        Task curr = arr.get(i);
        try
        {
            pic.setImageBitmap(ServiceHandler.imageFromFile(curr.pic));
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(this, "Image dose not exist anymore...Sorry :(", Toast.LENGTH_SHORT).show();
        }

        check_btn.setText(curr.Task);
        check_btn.setChecked(false);
        Toast.makeText(this, ServiceHandler.foramt.format(curr.doDate), Toast.LENGTH_SHORT).show();
    }
}