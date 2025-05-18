package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class KanbanPagerAdapter extends FragmentStateAdapter {

    public KanbanPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_UPCOMING);
            case 1:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_IN_PROGRESS);
            case 2:
                return TaskListFragment.newInstance(TaskListFragment.TYPE_FINISHED);
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Upcoming, In Progress, Finished
    }
}