import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subtaskIds;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, Status status, ArrayList subTaskIdList) {
        super(id, name, description, status);
        subTaskIdList = new ArrayList<>();
    }

    public void addToSubtasksIdsList(int id) {
        subtaskIds.add(id);
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subtaskIds;
    }


}
