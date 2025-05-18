package com.example.taskapp;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    public static final String ARG_TASK_STATUS_TYPE = "task_status_type";
    public static final int TYPE_UPCOMING = 0;
    public static final int TYPE_IN_PROGRESS = 1;
    public static final int TYPE_FINISHED = 2;

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TextView emptyView;
    private OnTaskInteractionListener listener;
    private int currentTaskStatusType;

    public interface OnTaskInteractionListener {
        void onTaskClickedInFragment(Task task);
        void onTaskCheckedChangedInFragment(Task task, boolean isChecked);
        void onDeleteClickedInFragment(Task task);
        void onStartTaskClickedInFragment(Task task);
        void onFinishTaskClickedInFragment(Task task);
    }

    public static TaskListFragment newInstance(int taskStatusType) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_STATUS_TYPE, taskStatusType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTaskStatusType = getArguments().getInt(ARG_TASK_STATUS_TYPE);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskInteractionListener) {
            listener = (OnTaskInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTaskInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_tasks_in_fragment);
        emptyView = view.findViewById(R.id.empty_view_tasks_in_fragment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(getContext(), currentTaskStatusType);
        recyclerView.setAdapter(taskAdapter);

        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                if (listener != null) {
                    listener.onTaskClickedInFragment(task);
                }
            }

            @Override
            public void onTaskCheckedChanged(Task task, boolean isChecked) {
                if (listener != null) {
                    listener.onTaskCheckedChangedInFragment(task, isChecked);
                }
            }

            @Override
            public void onDeleteClick(Task task) {
                if (listener != null) {
                    listener.onDeleteClickedInFragment(task);
                }
            }

            @Override
            public void onStartTaskClick(Task task) {
                if (listener != null) {
                    listener.onStartTaskClickedInFragment(task);
                }
            }

            @Override
            public void onFinishTaskClick(Task task) {
                if (listener != null) {
                    listener.onFinishTaskClickedInFragment(task);
                }
            }
        });
        return view;
    }

    public void updateTasks(List<Task> tasks) {
        if (taskAdapter != null && emptyView != null && recyclerView != null) {
            taskAdapter.setTasks(tasks);
            if (tasks.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}