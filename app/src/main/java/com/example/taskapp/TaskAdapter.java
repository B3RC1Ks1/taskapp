// TaskAdapter.java
package com.example.taskapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;
    private int taskStatusType;

    public TaskAdapter(Context context, int taskStatusType) {
        this.context = context;
        this.taskStatusType = taskStatusType;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_task, parent, false);
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
            holder.textViewDueDate.setText(R.string.no_due_date);
            holder.textViewDueDate.setVisibility(View.GONE);
        }

        holder.textViewPriority.setText(currentTask.getPriority());
        setPriorityIndicator(holder.priorityIndicator, currentTask.getPriority());

        updateTaskAppearance(holder, currentTask.isCompleted());

        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(currentTask.isCompleted());


        holder.buttonStartTask.setVisibility(View.GONE);
        holder.buttonFinishTask.setVisibility(View.GONE);
        holder.checkBoxCompleted.setVisibility(View.GONE);

        if (currentTask.isCompleted()) {
            holder.checkBoxCompleted.setVisibility(View.VISIBLE);
        } else if (Task.STATUS_UPCOMING.equals(currentTask.getStatus())) {
            holder.buttonStartTask.setVisibility(View.VISIBLE);
        } else if (Task.STATUS_IN_PROGRESS.equals(currentTask.getStatus())) {
            holder.buttonFinishTask.setVisibility(View.VISIBLE);
        }


        if (holder.checkBoxCompleted.getVisibility() == View.VISIBLE) {
            holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onTaskCheckedChanged(tasks.get(adapterPosition), isChecked);
                    }
                }
            });
        }
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
            holder.textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    private void setPriorityIndicator(View indicator, String priority) {
        indicator.setBackgroundResource(R.drawable.priority_indicator_shape);
        indicator.setVisibility(View.VISIBLE);

        if (priority == null) {
            indicator.getBackground().setTintList(null);
            return;
        }

        int colorResId;
        switch (priority.toLowerCase()) {
            case "high": colorResId = R.color.priority_high; break;
            case "medium": colorResId = R.color.priority_medium; break;
            case "low": colorResId = R.color.priority_low; break;
            default:
                indicator.getBackground().setTintList(null);
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
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDueDate;
        TextView textViewPriority;
        CheckBox checkBoxCompleted;
        View priorityIndicator;
        ImageButton buttonDeleteTask;
        Button buttonStartTask;
        Button buttonFinishTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDueDate = itemView.findViewById(R.id.text_view_due_date);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
            buttonDeleteTask = itemView.findViewById(R.id.button_delete_task);
            buttonStartTask = itemView.findViewById(R.id.button_start_task);
            buttonFinishTask = itemView.findViewById(R.id.button_finish_task);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
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

            buttonStartTask.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onStartTaskClick(tasks.get(position));
                }
            });

            buttonFinishTask.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onFinishTaskClick(tasks.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
        void onTaskCheckedChanged(Task task, boolean isChecked);
        void onDeleteClick(Task task);
        void onStartTaskClick(Task task);
        void onFinishTaskClick(Task task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}