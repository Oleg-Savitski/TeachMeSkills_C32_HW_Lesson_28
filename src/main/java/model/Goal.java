package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Goal implements Serializable {
    private String text;
    private boolean completed;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Goal(String text) {
        this.text = text;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public static Goal fromStorageString(String goalString) {
        int completedIndex = goalString.indexOf("[completed:");
        Goal goal = new Goal(completedIndex != -1
                ? goalString.substring(0, completedIndex).trim()
                : goalString);

        if (completedIndex != -1) {
            goal.setCompleted(true);
            try {
                String dateStr = goalString.substring(
                        completedIndex + 11,
                        goalString.length() - 1
                );
                goal.setCompletedAt(LocalDateTime.parse(dateStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception ignored) {}
        }
        return goal;
    }

    public String toStorageString() {
        StringBuilder sb = new StringBuilder(text);
        if (completed && completedAt != null) {
            sb.append(" [completed:").append(completedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("]");
        }
        return sb.toString();
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.completedAt = completed ? LocalDateTime.now() : null;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "text='" + text + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                '}';
    }
}