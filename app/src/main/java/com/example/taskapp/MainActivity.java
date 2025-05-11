// MainActivity.java
package com.example.taskapp; // Package is now the root

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Corrected imports for the new structure
import com.example.taskapp.Task;
import com.example.taskapp.AddEditTaskActivity; // Activity is in 'ui' subpackage
import com.example.taskapp.TaskAdapter;         // Adapter is in 'ui' subpackage
import com.example.taskapp.TaskViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;

    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private TextView emptyViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Assumes activity_main.xml exists in res/layout

        setTitle("My Tasks");

        emptyViewText = findViewById(R.id.empty_view_text);

        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.tasks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new TaskAdapter(this); // TaskAdapter is in com.example.taskapp.ui
        recyclerView.setAdapter(adapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> {
            if (tasks != null) {
                adapter.setTasks(tasks);
                if (tasks.isEmpty()) {
                    emptyViewText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyViewText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                emptyViewText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                intent.putExtra(AddEditTaskActivity.EXTRA_TASK, task);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }

            @Override
            public void onTaskCheckedChanged(Task task, boolean isChecked) {
                // Create a new task object or modify the existing one carefully
                // to avoid issues if the original task object is shared.
                // For simplicity, we update the existing task object directly here.
                task.setCompleted(isChecked);
                // task.setLastUpdated(new Date()); // Firestore @ServerTimestamp handles this
                taskViewModel.update(task);
            }

            @Override
            public void onDeleteClick(Task task) {
                showDeleteConfirmationDialog(task);
            }
        });
    }

    private void showDeleteConfirmationDialog(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    taskViewModel.delete(task);
                    Toast.makeText(MainActivity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_TASK_REQUEST) {
                Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_TASK_REQUEST) {
                Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show();
            }
        }
    }
}