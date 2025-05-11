// AddEditTaskActivity.java
package com.example.taskapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.taskapp.R;
import com.example.taskapp.Task; // Correct import
import com.example.taskapp.DateUtils; // Correct import
import com.example.taskapp.TaskViewModel; // Correct import
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "com.example.taskapp.ui.EXTRA_TASK";

    private TextInputEditText editTextTitle;
    private TextInputEditText editTextDescription;
    private TextView textViewSelectedDueDate;
    private Button buttonPickDate;
    private Button buttonClearDate;
    private Spinner spinnerPriority;
    private Button buttonSaveTask;
    private TextInputLayout textInputLayoutTitle;

    private TaskViewModel taskViewModel;
    private Task currentTask;
    private Long selectedDueDateTimestamp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task); // Assumes activity_add_edit_task.xml

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewSelectedDueDate = findViewById(R.id.text_view_selected_due_date);
        buttonPickDate = findViewById(R.id.button_pick_date);
        buttonClearDate = findViewById(R.id.button_clear_date);
        spinnerPriority = findViewById(R.id.spinner_priority);
        buttonSaveTask = findViewById(R.id.button_save_task);
        textInputLayoutTitle = findViewById(R.id.text_input_title);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close); // Ensure ic_close.xml exists
        }

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TASK)) {
            currentTask = (Task) intent.getSerializableExtra(EXTRA_TASK);
            setTitle(R.string.edit_task);
            if (currentTask != null) {
                populateFields(currentTask);
            }
        } else {
            setTitle(R.string.add_task);
            updateDueDateDisplay(null);
        }

        buttonPickDate.setOnClickListener(v -> showDatePickerDialog());
        buttonClearDate.setOnClickListener(v -> {
            selectedDueDateTimestamp = null;
            updateDueDateDisplay(null);
        });
        buttonSaveTask.setOnClickListener(v -> saveTask());
    }

    private void populateFields(@NonNull Task task) {
        editTextTitle.setText(task.getTitle());
        editTextDescription.setText(task.getDescription());
        selectedDueDateTimestamp = task.getDueDate();
        updateDueDateDisplay(selectedDueDateTimestamp);
        if (task.getPriority() != null) {
            String[] priorities = getResources().getStringArray(R.array.priority_levels);
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equalsIgnoreCase(task.getPriority())) {
                    spinnerPriority.setSelection(i);
                    break;
                }
            }
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        if (selectedDueDateTimestamp != null) {
            calendar.setTimeInMillis(selectedDueDateTimestamp);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year1, monthOfYear, dayOfMonth, 0, 0, 0);
            selectedCalendar.set(Calendar.MILLISECOND, 0);
            selectedDueDateTimestamp = selectedCalendar.getTimeInMillis();
            updateDueDateDisplay(selectedDueDateTimestamp);
        }, year, month, day).show();
    }

    private void updateDueDateDisplay(Long timestamp) {
        if (timestamp != null) {
            textViewSelectedDueDate.setText(DateUtils.formatDate(timestamp));
            buttonClearDate.setVisibility(View.VISIBLE);
        } else {
            textViewSelectedDueDate.setText(R.string.no_due_date);
            buttonClearDate.setVisibility(View.GONE);
        }
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            textInputLayoutTitle.setError(getString(R.string.title_is_required));
            return;
        } else {
            textInputLayoutTitle.setError(null);
        }

        if (currentTask != null) {
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setDueDate(selectedDueDateTimestamp);
            currentTask.setPriority(priority);
            taskViewModel.update(currentTask);
        } else {
            Task newTask = new Task(title, description, selectedDueDateTimestamp, priority, false);
            taskViewModel.insert(newTask);
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}