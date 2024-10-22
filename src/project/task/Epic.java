package project.task;

import project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public void addToSubtasks(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void recalculateTimes() {
        if (subtasks.isEmpty()) {
            this.duration = Duration.ZERO;
            this.startTime = null;
            this.endTime = null;
        } else {
            this.duration = subtasks.stream()
                    .map(Subtask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
            this.startTime = subtasks.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            this.endTime = subtasks.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void changeStatus() {
        if (getSubtasks().isEmpty()) {
            setStatus(Status.NEW);
        } else {
            long statusNew = subtasks.stream()
                    .filter(subtask -> subtask.getStatus() == Status.NEW)
                    .count();

            long statusDone = subtasks.stream()
                    .filter(subtask -> subtask.getStatus() == Status.DONE)
                    .count();

            long statusInProgress = subtasks.stream()
                    .filter(subtask -> subtask.getStatus() == Status.IN_PROGRESS)
                    .count();

            if (statusNew == 0 && statusInProgress == 0) {
                setStatus(Status.DONE);
            } else if (statusInProgress == 0 && statusDone == 0) {
                setStatus(Status.NEW);
            } else {
                setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public String toString() {
        return super.getId() + "," + "EPIC" + "," + super.getName() + "," + super.getStatus() + "," + super.getDescription() + "," + this.duration + "," + this.startTime;
    }
}
