package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class FirebaseHandler {
    public static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    public Account user;
    private Context context;

    public FirebaseHandler(Context context) {
        this.user = null;
        this.context = context;
    }

    public void disconnect(){
        user = null;
    }

    public void Login(String username, String password) {
        user = null;
        DatabaseReference users = db.getReference("users");
        Query q = users.orderByValue();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account temp = null;
                boolean doseUserExist = false;
                for(DataSnapshot dst : snapshot.getChildren()){
                    try {
                        temp = dst.getValue(Account.class);
                    }
                    catch (Exception e){
                        Log.e("firebase", e.getMessage());
                    }

                    if(temp != null && temp.getUsername().equals(username) && temp.getPassword().equals(password)){
                        user = temp;
                        doseUserExist = true;
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent toTasks = new Intent(context, DisplayTasksActivity.class);
                        context.startActivity(toTasks);
                        break;
                    }
                }

                if(!doseUserExist){
                    Toast.makeText(context, "Login Failed, Wrong username or password!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void signup(String username, String password) {
        user = null;
        DatabaseReference users = db.getReference("users");
        Query q = users.orderByValue();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                Account temp = null;
                boolean doseUserExist = false;
                for (DataSnapshot dst : snapshot.getChildren()) {
                    try {
                        temp = dst.getValue(Account.class);
                    } catch (Exception e) {
                        Log.e("firebase", e.getMessage());
                    }

                    if (temp != null && temp.getUsername().equals(username)) {
                        doseUserExist = true;
                        Toast.makeText(context, "Signup Failed, User already exist!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if(!doseUserExist){
                    Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show();
                    user = new Account(username,password);
                    user.setTasks(new ArrayList<Task>());
                    users.child(username).setValue(user);
                    Intent toTasks = new Intent(context, DisplayTasksActivity.class);
                    context.startActivity(toTasks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static FirebaseDatabase getDb() {
        return db;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }
}
