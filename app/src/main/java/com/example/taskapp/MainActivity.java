// MainActivity.java
package com.example.taskapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements TaskListFragment.OnTaskInteractionListener {

    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;

    private TaskViewModel taskViewModel;
    private ViewPager2 viewPager;
    private KanbanPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    private final String[] tabTitles = new String[]{"Upcoming", "In Progress", "Finished"};

    private ActivityResultLauncher<Intent> taskActivityLauncher;

    private enum SortOrder {
        DUE_DATE, PRIORITY, ALPHABETICALLY
    }
    private SortOrder currentSortOrder = SortOrder.DUE_DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        pagerAdapter = new KanbanPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText(tabTitles[position]);
                    tab.setContentDescription(tabTitles[position]);
                }
        ).attach();

        taskActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(this, R.string.task_saved, Toast.LENGTH_SHORT).show();
                    }
                });

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, tasks -> {
            if (tasks != null) {
                processAndDisplayTasks(new ArrayList<>(tasks));
            } else {
                processAndDisplayTasks(new ArrayList<>());
            }
        });

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            taskActivityLauncher.launch(intent);
        });
    }

    private void processAndDisplayTasks(List<Task> tasks) {
        sortTaskList(tasks);

        List<Task> upcomingTasks = tasks.stream()
                .filter(task -> !task.isCompleted() && (task.getStatus() == null || Task.STATUS_UPCOMING.equals(task.getStatus())))
                .collect(Collectors.toList());
        List<Task> inProgressTasks = tasks.stream()
                .filter(task -> !task.isCompleted() && Task.STATUS_IN_PROGRESS.equals(task.getStatus()))
                .collect(Collectors.toList());
        List<Task> finishedTasks = tasks.stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());

        updateFragmentList(TaskListFragment.TYPE_UPCOMING, upcomingTasks);
        updateFragmentList(TaskListFragment.TYPE_IN_PROGRESS, inProgressTasks);
        updateFragmentList(TaskListFragment.TYPE_FINISHED, finishedTasks);
    }

    private void sortTaskList(List<Task> tasks) {
        switch (currentSortOrder) {
            case DUE_DATE:
                tasks.sort(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case PRIORITY:
                tasks.sort(Comparator.comparingInt(task -> getPriorityOrder(task.getPriority())));
                break;
            case ALPHABETICALLY:
                tasks.sort(Comparator.comparing(Task::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
                break;
        }
    }

    private int getPriorityOrder(String priority) {
        if (priority == null) return 3;
        switch (priority.toLowerCase()) {
            case "high": return 0;
            case "medium": return 1;
            case "low": return 2;
            default: return 3;
        }
    }


    private void updateFragmentList(int fragmentType, List<Task> tasks) {
        int position = -1;
        switch (fragmentType) {
            case TaskListFragment.TYPE_UPCOMING:
                position = 0;
                break;
            case TaskListFragment.TYPE_IN_PROGRESS:
                position = 1;
                break;
            case TaskListFragment.TYPE_FINISHED:
                position = 2;
                break;
        }

        if (position != -1) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);
            if (fragment instanceof TaskListFragment) {
                ((TaskListFragment) fragment).updateTasks(tasks);
            }
        }
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
    public void onTaskClickedInFragment(Task task) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK, task);
        taskActivityLauncher.launch(intent);
    }

    @Override
    public void onTaskCheckedChangedInFragment(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        if (!isChecked && task.getStatus() == null) {
            task.setStatus(Task.STATUS_UPCOMING);
        } else if (isChecked) {
            task.setStatus(null);
        }
        taskViewModel.update(task);
    }

    @Override
    public void onDeleteClickedInFragment(Task task) {
        showDeleteConfirmationDialog(task);
    }

    @Override
    public void onStartTaskClickedInFragment(Task task) {
        task.setStatus(Task.STATUS_IN_PROGRESS);
        task.setCompleted(false);
        taskViewModel.update(task);
    }

    @Override
    public void onFinishTaskClickedInFragment(Task task) {
        task.setCompleted(true);
        taskViewModel.update(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean needsRefresh = false;

        if (id == R.id.action_sort_by_due_date) {
            if (currentSortOrder != SortOrder.DUE_DATE) {
                currentSortOrder = SortOrder.DUE_DATE;
                needsRefresh = true;
            }
        } else if (id == R.id.action_sort_by_priority) {
            if (currentSortOrder != SortOrder.PRIORITY) {
                currentSortOrder = SortOrder.PRIORITY;
                needsRefresh = true;
            }
        } else if (id == R.id.action_sort_by_alphabetically) {
            if (currentSortOrder != SortOrder.ALPHABETICALLY) {
                currentSortOrder = SortOrder.ALPHABETICALLY;
                needsRefresh = true;
            }
        }

        if (needsRefresh) {
            List<Task> currentTasks = taskViewModel.getAllTasks().getValue();
            if (currentTasks != null) {
                processAndDisplayTasks(new ArrayList<>(currentTasks));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}