// TaskAdapter.java
package com.example.taskapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
// import com.example.taskapp.R; // R is automatically available
// import com.example.taskapp.Task; // Correct import
// import com.example.taskapp.DateUtils; // Correct import
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_task, parent, false); // Assumes list_item_task.xml
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.textViewTitle.setText(currentTask.getTitle());

        if (currentTask.getDueDate() != null) {
            String dueDateText = context.getString(R.string.due_date_prefix) + DateUtils.formatDate(currentTask.getDueDate());
            holder.textViewDueDate.setText(dueDateText);
            holder.textViewDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDueDate.setText(R.string.no_due_date); // Or just set to "" and hide
            holder.textViewDueDate.setVisibility(View.GONE); // More consistent to hide if no date
        }

        holder.textViewPriority.setText(currentTask.getPriority()); // Display the priority string
        setPriorityIndicator(holder.priorityIndicator, currentTask.getPriority());

        holder.checkBoxCompleted.setOnCheckedChangeListener(null); // Avoid listener conflicts
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted());
        updateTaskAppearance(holder, currentTask.isCompleted());

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCheckedChanged(currentTask, isChecked);
            }
        });
    }

    private void updateTaskAppearance(TaskViewHolder holder, boolean isCompleted) {
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        if (isCompleted) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.task_item_completed_background));
            holder.textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.task_item_title_completed));
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.task_item_background));
            holder.textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.black)); // Or your default title color
        }
    }

    private void setPriorityIndicator(View indicator, String priority) {
        // Ensure the original shape drawable is set as the background
        indicator.setBackgroundResource(R.drawable.priority_indicator_shape);
        // Make sure indicator is visible if it might have been hidden
        indicator.setVisibility(View.VISIBLE);

        if (priority == null) {
            // Option: Remove tint to show default shape color (e.g., darker_gray from the XML)
            indicator.getBackground().setTintList(null);
            // Alternative: Set a specific 'no priority' color tint
            // indicator.getBackground().setTint(ContextCompat.getColor(context, R.color.priority_none_or_default));
            // Alternative: Hide the indicator
            // indicator.setVisibility(View.GONE);
            return;
        }

        int colorResId;
        switch (priority.toLowerCase()) { // Use toLowerCase for case-insensitive matching
            case "high": colorResId = R.color.priority_high; break;
            case "medium": colorResId = R.color.priority_medium; break;
            case "low": colorResId = R.color.priority_low; break;
            default:
                // Unknown priority string, remove tint or set to a default
                indicator.getBackground().setTintList(null);
                // Or hide if unknown priority should not show an indicator
                // indicator.setVisibility(View.GONE);
                return;
        }
        indicator.getBackground().setTint(ContextCompat.getColor(context, colorResId));
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged(); // For simplicity. Use DiffUtil for better performance.
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDueDate;
        TextView textViewPriority;
        CheckBox checkBoxCompleted;
        View priorityIndicator;
        ImageButton buttonDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDueDate = itemView.findViewById(R.id.text_view_due_date);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
            buttonDeleteTask = itemView.findViewById(R.id.button_delete_task);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition(); // Use getBindingAdapterPosition()
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(tasks.get(position));
                }
            });
            buttonDeleteTask.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(tasks.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
        void onTaskCheckedChanged(Task task, boolean isChecked);
        void onDeleteClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}