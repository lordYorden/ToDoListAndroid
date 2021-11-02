package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
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
            Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
    }
}