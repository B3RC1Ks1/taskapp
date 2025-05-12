// MainActivity.java
package com.example.taskapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.Task;
import com.example.taskapp.AddEditTaskActivity;
import com.example.taskapp.TaskAdapter;
import com.example.taskapp.TaskViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;

    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private TextView emptyViewText;
    // No need for a Toolbar variable if only used in onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure the layout with CoordinatorLayout is set
        setContentView(R.layout.activity_main);

        // --- Setup the Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // The title is set via app:title in the XML, so no need for setTitle() here
        // --- End Toolbar Setup ---

        emptyViewText = findViewById(R.id.empty_view_text);
        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);

        // Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.tasks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); // Optional performance improvement

        adapter = new TaskAdapter(this);
        recyclerView.setAdapter(adapter);

        // Setup ViewModel and Observer
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> {
            if (tasks != null) {
                adapter.setTasks(tasks);
                // Toggle empty view visibility
                if (tasks.isEmpty()) {
                    emptyViewText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyViewText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                // Handle potential null list from LiveData (e.g., error state)
                emptyViewText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // Setup Adapter Click Listener
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
                intent.putExtra(AddEditTaskActivity.EXTRA_TASK, task);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }

            @Override
            public void onTaskCheckedChanged(Task task, boolean isChecked) {
                task.setCompleted(isChecked);
                taskViewModel.update(task); // ViewModel updates Firestore
            }

            @Override
            public void onDeleteClick(Task task) {
                showDeleteConfirmationDialog(task);
            }
        });

        // Setup FAB Click Listener
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivityForResult(intent, ADD_TASK_REQUEST);
        });
    }

    // --- Helper method for delete confirmation ---
    private void showDeleteConfirmationDialog(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    taskViewModel.delete(task); // ViewModel deletes from Firestore
                    Toast.makeText(MainActivity.this, R.string.task_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // --- Handle result from AddEditTaskActivity ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Show feedback message. LiveData handles the actual list update.
            if (requestCode == ADD_TASK_REQUEST || requestCode == EDIT_TASK_REQUEST) {
                Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show();
            }
        }
        // Optional: Handle RESULT_CANCELED or other results if needed
    }
}