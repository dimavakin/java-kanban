package project.manager;

import project.status.Status;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final  Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory(); // Инициализация через Managers
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else return null;
    }

    @Override
    public void deleteAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            throw new NoSuchElementException("Задача с таким id найдена");
        }
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        id++;
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        id++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicID())) {
            epics.get(subtask.getEpicID()).addToSubtasksIds(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            id++;
        }
    }

    @Override
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

    @Override
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

    @Override
    public List<Subtask> getSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            List<Subtask> subtaskByEpic = new ArrayList<>();
            for (int subtaskId : epics.get(id).getSubtaskIds()) {
                subtaskByEpic.add(subtasks.get(subtaskId));
            }
            return subtaskByEpic;
        } else return null;
    }


    @Override
    public int getId() {
        return id;
    }

    public void changeEpicStatus(Epic epic) {

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
