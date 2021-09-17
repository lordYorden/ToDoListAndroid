package com.example.todolist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {
    Context context;
    ArrayList<Task> objects;

    public TaskAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull ArrayList<Task> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int bitmap, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.tasks_layout, parent, false);

        ImageView pic = view.findViewById(R.id.pic_iv);
        TextView date_tv = view.findViewById(R.id.date_tv);
        TextView task_tv = view.findViewById(R.id.task_tv);

        Task temp = objects.get(bitmap);

        try
        {
            pic.setImageBitmap(ServiceHandler.fixPictureRotation(temp));
            /*Toast.makeText(context, String.valueOf(rotation), Toast.LENGTH_SHORT).show();*/
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(context, "Image dose not exist anymore...Sorry :(", Toast.LENGTH_SHORT).show();
        }

        date_tv.setText(ServiceHandler.format.format(temp.doDate));
        task_tv.setText(temp.Task);

        return view;
    }
}
