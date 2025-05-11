// TaskViewModel.java
package com.example.taskapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.taskapp.Task; // Correct import
import com.example.taskapp.TaskRepository; // Correct import
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository();
        allTasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }
    public void insert(Task task) {
        repository.addTask(task);
    }
    public void update(Task task) {
        repository.updateTask(task);
    }
    public void delete(Task task) {
        repository.deleteTask(task);
    }
}