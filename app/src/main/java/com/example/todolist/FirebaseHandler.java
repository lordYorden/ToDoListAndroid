package com.example.todolist;

import android.content.Context;
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

import java.util.concurrent.Semaphore;

public class FirebaseHandler {
    private FirebaseDatabase db;
    private Account user;
    private Context context;

    public FirebaseHandler(Context context) {
        this.user = null;
        this.db = FirebaseDatabase.getInstance();
        this.context = context;
    }

    public void Login(String username, String password) {
        DatabaseReference users = this.db.getReference("users");
        Query q = users.orderByValue();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account temp = null;
                for(DataSnapshot dst : snapshot.getChildren()){
                    try {
                        temp = dst.getValue(Account.class);
                    }
                    catch (Exception e){
                        Log.e("firebase", e.getMessage());
                    }

                    if(temp != null && temp.getUsername().equals(username) && temp.getPassword().equals(password)){
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
                        user = temp;
                    }else {
                        Toast.makeText(context, "Login Failed, Wrong username or password!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void signup(String username, String password) {
        DatabaseReference users = this.db.getReference("users");
        Query q = users.orderByValue();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account temp = null;
                for (DataSnapshot dst : snapshot.getChildren()) {
                    try {
                        temp = dst.getValue(Account.class);
                    } catch (Exception e) {
                        Log.e("firebase", e.getMessage());
                    }

                    if (temp != null && temp.getUsername().equals(username)) {
                        Toast.makeText(context, "Signup Failed, User already exist!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show();
                        user = new Account(username,password);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
