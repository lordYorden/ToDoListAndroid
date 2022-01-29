package com.example.todolist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Tasks;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class TaskNotificationService extends Service {

    NotificationCompat.Builder builder;
    NotificationManager manager;
    StringBuilder tasksDue;
    Date curr;
    ArrayList<Task> tasks;
    ArrayList<Task> toDisplay;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    tasks = AccountManagerActivity.firebaseHandler.user.getTasks();
                    if(tasks != null){
                        for (Task t : tasks) {
                            tasksDue = new StringBuilder();
                            curr = Calendar.getInstance().getTime();
                            toDisplay = new ArrayList<Task>();
                            if (curr.compareTo(t.doDate) >= 0) {
                                toDisplay.add(t);
                                tasksDue.append(t.task).append(" is Due ").append(ServiceHandler.format.format(t.doDate)).append('\n');
                            }
                        }

                        if(!toDisplay.isEmpty()) {
                            createNotificationChannel("Task Due", "Notify when the end of a tasks are near.", "10");
                            builder = new NotificationCompat.Builder(TaskNotificationService.this, "10")
                                    .setSmallIcon(R.drawable.app_icon_mid)
                                    .setContentTitle("Due soon: ")
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(tasksDue.toString()));

                            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.notify(0, builder.build());
                        }
                    }

                    try {
                        Thread.sleep(3600000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {


    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    private void createNotificationChannel(String name, String description, String channelID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}