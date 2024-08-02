package project.manager;

import project.task.Epic;
import project.task.Subtask;
import project.task.Task;
import project.status.Status;

import java.util.*;


public class TaskManager {
    private int id = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    //Получение списка всех задач

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    //Удаление всех задач.

    public void deleteAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    //Получение по id

    public Task getOneTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (epics.containsKey(id)) {
            return subtasks.get(id);
        } else return null;
    }

    //Создание

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        id++;
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        id++;
    }

    public void createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicID())) {
            epics.get(subtask.getEpicID()).addToSubtasksIds(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            id++;
        }
    }

    // Обновление

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else if (epics.containsKey(id)) {
            epics.put(id, (Epic) task);
        } else if (subtasks.containsKey(id)) {
            subtasks.put(id, (Subtask) task);
            changeEpicStatus(epics.get(subtasks.get(id).getEpicID()));

        }
    }

    //Удаление по id

    public void deleteTaskById(int id) {
        tasks.remove(id);
        subtasks.remove(id);
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtaskIds()) {
                deleteTaskById(subtaskId);
            }
            epics.remove(id);
        }
    }

    //Получение списка всех подзадач определённого эпика.

    public List<Subtask> getSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtaskByEpic = new ArrayList<>();
            for (int subtaskId : epics.get(id).getSubTaskIdList()) {
                subtaskByEpic.add(subtasks.get(subtaskId));
            }
            return subtaskByEpic;
        } else return null;
    }

    public int getId() {
        return id;
    }

    private void changeEpicStatus(Epic epic) {

        if (epic.getSubtaskIds().isEmpty()) {
            epics.get(epic.getId()).setStatus(Status.NEW);

        } else {
            int statusNew = 0;
            int statusDone = 0;
            int statusInProgress = 0;
            for (int id : epic.getSubtaskIds()) {
                switch (subtasks.get(id).getStatus()) {
                    case NEW:
                        statusNew++;
                        break;
                    case DONE:
                        statusDone++;
                        break;
                    case IN_PROGRESS:
                        statusInProgress++;
                        break;
                }
            }
            if (statusNew == 0 && statusInProgress == 0) {
                epics.get(epic.getId()).setStatus(Status.DONE);
            } else if (statusInProgress == 0 && statusDone == 0) {

                epics.get(epic.getId()).setStatus(Status.NEW);
            } else {
                epics.get(epic.getId()).setStatus(Status.IN_PROGRESS);
            }
        }
    }

}
