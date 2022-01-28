package com.example.todolist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class FirebaseHandler {
    public static final FirebaseDatabase db = FirebaseDatabase.getInstance();
    public static final FirebaseStorage storage = FirebaseStorage.getInstance();
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
                        Log.d("Connection State: ", "Login Successful");
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
                    Log.d("Connection State: ", "SignUp Successful");
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

    public static void uploadImage(String fileName, String fileLocation, String date , Uri photo, Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Uploading photo");
        pd.show();

        if(photo != null) {
            StorageReference imageRef = storage.getReference(fileLocation).child(fileName);
            imageRef.putFile(photo).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.e("photoUrl", url);
                            pd.dismiss();

                            String data = fileName + "$" + url + "$" + date + "\n";
                            ServiceHandler.writeToFile(data, context, "tasks.txt");
                            try {
                                ServiceHandler.addTaskToFirebase(new Task(fileName, url, ServiceHandler.format.parse(date)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "task failed", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(context, "Task was added!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            Toast.makeText(context, "image wasn't found", Toast.LENGTH_SHORT).show();
        }
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
