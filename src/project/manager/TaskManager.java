package project.manager;

import project.exception.InvalidEpicIdException;
import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

    void deleteAll() throws ManagerSaveException;

    Task getTaskById(int id);

    List<Task> getAllTasks();

    void createTask(Task task) throws ManagerSaveException, TimeConflictException;

    void createEpic(Epic epic) throws ManagerSaveException;

    void createSubtask(Subtask subtask) throws ManagerSaveException, TimeConflictException, InvalidEpicIdException;

    void updateTask(int id, Task task) throws ManagerSaveException;

    void deleteTaskById(int id) throws ManagerSaveException;

    List<Subtask> getSubtasksByEpicId(int id);

    int getId();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    boolean hasTask(int id);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    void deleteAllTasks();
}
