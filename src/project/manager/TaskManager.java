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



    //Удаление всех задач.
    void deleteAll();

    //Получение по id
    Task getOneTask(int id);

    //Создание
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    // Обновление
    void updateTask(int id, Task task);

    //Удаление по id
    void deleteTaskById(int id);

    //Получение списка всех подзадач определённого эпика.
    List<Subtask> getSubtasksByEpicId(int id);

    int getId();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);
}
