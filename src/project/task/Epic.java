package project.task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtaskIds = new ArrayList<>();
    }

    public void addToSubtasksIds(int id) {
        subtaskIds.add(id);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return super.getId() + "," + "EPIC" + "," + super.getName() + "," + super.getStatus() + "," + super.getDescription();
    }
}
