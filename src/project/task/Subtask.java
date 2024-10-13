package project.task;

import project.status.Status;

public class Subtask extends Task {
    private int epicID;

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return super.getId() + "," + "SUBTASK" + "," + super.getName() + "," + super.getStatus() + "," + super.getDescription() + "," + epicID;
    }

    public int getEpicID() {
        return epicID;
    }
}
