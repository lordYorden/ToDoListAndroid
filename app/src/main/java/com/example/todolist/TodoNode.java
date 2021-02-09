package com.example.todolist;

import java.util.LinkedList;

public class TodoNode {
    String Task;
    String pic;

    TodoNode(String Task, String pic)
    {
        this.pic = pic;
        this.Task = Task;
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
