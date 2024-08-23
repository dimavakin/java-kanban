package project.manager;

import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

    void deleteAll();

    Task getTaskById(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void updateTask(int id, Task task);

    void deleteTaskById(int id);

    List<Subtask> getSubtasksByEpicId(int id);

    int getId();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);
}
