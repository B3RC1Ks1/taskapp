// Task.java
package com.example.taskapp;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

    @DocumentId
    private String id;
    private String title;
    private String description;
    private Long dueDate;
    private String priority;
    private boolean completed;
    private String status; // "Upcoming", "In Progress"

    @ServerTimestamp
    private Date lastUpdated;

    public static final String STATUS_UPCOMING = "Upcoming";
    public static final String STATUS_IN_PROGRESS = "In Progress";


    public Task() {}

    public Task(String title, String description, Long dueDate, String priority, boolean completed) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        if (!completed) {
            this.status = STATUS_UPCOMING; // Default status for new, non-completed tasks
        }
    }

    public Task(String title, String description, Long dueDate, String priority, boolean completed, String status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        if (!completed) {
            this.status = status;
        } else {
            this.status = null; // Status is not relevant for completed tasks in this context
        }
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getDueDate() { return dueDate; }
    public void setDueDate(Long dueDate) { this.dueDate = dueDate; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            this.status = null; // Clear status when task is completed
        } else if (this.status == null) {
            this.status = STATUS_UPCOMING; // Default to upcoming if un-completed and status was null
        }
    }
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}