package com.example.todolist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.todolist.MainActivity.arr;


public class TaskAdapter extends ArrayAdapter<Task> implements CompoundButton.OnCheckedChangeListener {
    Context context;
    ArrayList<Task> objects;
/*    CheckBox fin_cb;
    TextView task_tv;*/

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

        CheckBox fin_cb;
        TextView task_tv;

        ImageView pic = view.findViewById(R.id.pic_iv);
        TextView date_tv = view.findViewById(R.id.date_tv);
        task_tv = view.findViewById(R.id.task_tv);
        fin_cb = view.findViewById(R.id.fin_cb);

        Task temp = objects.get(bitmap);

        //checks for image source (firebase or device storage)
        if(temp.pic.contains("https://"))

            Picasso.get().load(temp.pic).into(pic);
        else
            pic.setImageBitmap(ServiceHandler.fixPictureRotation(temp, context));
        /*Toast.makeText(context, String.valueOf(rotation), Toast.LENGTH_SHORT).show();*/

        date_tv.setText(ServiceHandler.format.format(temp.doDate));
        task_tv.setText(temp.task);
        if(temp.isFin){
            fin_cb.setChecked(true);
            task_tv.setPaintFlags(task_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //if tasks is finish upload firebase's real time database
        fin_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(context, "task fin!", Toast.LENGTH_SHORT).show();
                    //finish & marks task
                    fin_cb.setChecked(true);
                    task_tv.setPaintFlags(task_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    temp.isFin = true;
                    ServiceHandler.resetLocalTasks(context);
                    ServiceHandler.addTasksFromArray(arr, context);
                    ServiceHandler.setTaskToFirebase(arr);

                } else{
                    task_tv.setPaintFlags(0);
                    temp.isFin = false;
                    Toast.makeText(context, "undo", Toast.LENGTH_SHORT).show();
                    ServiceHandler.resetLocalTasks(context);
                    ServiceHandler.addTasksFromArray(arr, context);
                    ServiceHandler.setTaskToFirebase(arr);

                }
            }
        });

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //gets the curr view and components
        View view = (View) buttonView.getParent().getParent();
        CheckBox fin_cb = view.findViewById(R.id.fin_cb);
        TextView task_tv = view.findViewById(R.id.task_tv);

        if(isChecked) {
            Toast.makeText(context, "task fin!", Toast.LENGTH_SHORT).show();
            //finish & marks task
            fin_cb.setChecked(true);
            task_tv.setPaintFlags(task_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else{
            task_tv.setPaintFlags(0);
            Toast.makeText(context, "undo", Toast.LENGTH_SHORT).show();

        }
    }
}
