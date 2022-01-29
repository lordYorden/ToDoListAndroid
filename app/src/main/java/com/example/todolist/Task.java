package com.example.todolist;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class Task {
    String task;
    String pic;
    String description;
    Date doDate;
    Boolean isFin;

    Task(){

    }

    Task(String Task, String pic, Date doDate, String description)
    {
        this.pic = pic;
        this.task = Task;
        this.doDate = doDate;
        this.isFin = false;
        this.description = description;
    }

    Task(String Task, String pic, Date doDate, String description, Boolean isFin)
    {
        this.pic = pic;
        this.task = Task;
        this.doDate = doDate;
        this.isFin = isFin;
        this.description = description;
    }

    @Override
    public String toString() {
        return task + '$' +
                 pic + '$' +
                ServiceHandler.format.format(doDate) + "$" +
                description + "$" +
                isFin + '\n';

    }

    public String getPic() {
        return pic;
    }

    public String getTask() {
        return task;
    }

    public Date getDoDate() {
        return doDate;
    }

    public void setDoDate(Date doDate) {
        this.doDate = doDate;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Boolean getFin() {
        return isFin;
    }

    public void setFin(Boolean fin) {
        isFin = fin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
