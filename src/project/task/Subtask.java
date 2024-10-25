package project.task;

import project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicID;

    public Subtask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicID) {
        super(id, name, description, status, duration, startTime);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return super.getId() + "," + "SUBTASK" + "," + super.getName() + "," + super.getStatus() + "," + super.getDescription() + "," + duration + "," + startTime + "," + epicID;
    }


}
