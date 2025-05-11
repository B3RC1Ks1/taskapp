// TaskRepository.java
package com.example.taskapp;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.taskapp.Task; // Correct import
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private static final String TAG = "TaskRepository";
    private static final String TASKS_COLLECTION = "tasks";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksCollection = db.collection(TASKS_COLLECTION);
    private MutableLiveData<List<Task>> allTasksLiveData = new MutableLiveData<>();

    public TaskRepository() {
        tasksCollection.orderBy("lastUpdated", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        allTasksLiveData.postValue(null);
                        return;
                    }
                    List<Task> tasks = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Task task = doc.toObject(Task.class);
                            tasks.add(task);
                        }
                    }
                    allTasksLiveData.postValue(tasks);
                });
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasksLiveData;
    }

    public void addTask(Task task) {
        tasksCollection.add(task)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Task added: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding task", e));
    }

    public void updateTask(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            Log.e(TAG, "Task ID is null for update.");
            return;
        }
        tasksCollection.document(task.getId()).set(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task updated: " + task.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating task", e));
    }

    public void deleteTask(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            Log.e(TAG, "Task ID is null for delete.");
            return;
        }
        tasksCollection.document(task.getId()).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task deleted: " + task.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting task", e));
    }
}