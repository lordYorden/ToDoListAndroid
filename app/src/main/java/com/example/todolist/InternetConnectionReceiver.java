package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class InternetConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*// an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");*/
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = network != null && network.isConnectedOrConnecting();
        if(isConnected)
            Log.d("Internet State", "Connected");
        else
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            Log.d("Internet State","No Internet Connection!");
    }
}