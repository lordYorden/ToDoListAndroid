package com.example.todolist;

import java.util.Date;

public class Task {
    String task;
    String pic;
    Date doDate;
    Boolean isFin;

    Task(){

    }

    Task(String Task, String pic, Date doDate)
    {
        this.pic = pic;
        this.task = Task;
        this.doDate = doDate;
        this.isFin = false;
    }

    Task(String Task, String pic, Date doDate, Boolean isFin)
    {
        this.pic = pic;
        this.task = Task;
        this.doDate = doDate;
        this.isFin = isFin;
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
}
