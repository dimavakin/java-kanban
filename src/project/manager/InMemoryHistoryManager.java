package project.manager;

import project.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> lastTenTasks = new ArrayList<>();

    public void add(Task task) {
        if (lastTenTasks.size() == 10) {
            lastTenTasks.remove(0);
        }
        lastTenTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return lastTenTasks;
    }
}
