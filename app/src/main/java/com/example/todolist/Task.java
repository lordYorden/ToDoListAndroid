package com.example.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Task {
    String Task;
    String pic;
    Date doDate;

    Task(String Task, String pic, Date doDate)
    {
        this.pic = pic;
        this.Task = Task;
        this.doDate = doDate;
    }

    public String getPic() {
        return pic;
    }

    public String getTask() {
        return Task;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setTask(String task) {
        Task = task;
    }
}
