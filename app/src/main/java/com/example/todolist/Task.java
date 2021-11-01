package com.example.todolist;

import java.util.Date;

public class Task {
    String task;
    String pic;
    Date doDate;

    Task(){

    }

    Task(String Task, String pic, Date doDate)
    {
        this.pic = pic;
        this.task = Task;
        this.doDate = doDate;
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
}
