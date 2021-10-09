package com.example.todolist;

import java.util.ArrayList;
import java.util.UUID;

public class Account {
    private String username;
    private String password;
    private ArrayList<Task> tasks;

    public Account() {
    }

    public Account(String username, String password, ArrayList<Task> tasks) {
        this.username = username;
        this.password = password;
        this.tasks = tasks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
