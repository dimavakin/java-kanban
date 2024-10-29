package project.manager;

import project.comparator.TaskComparator;
import project.exception.InvalidEpicIdException;
import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {
    private int id = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskComparator());
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
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
    public void deleteAll() throws ManagerSaveException {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new NoSuchElementException("Задача с таким id найдена");
        }
    }

    public boolean hasTask(int id) {
        if (tasks.containsKey(id)) {
            return true;
        } else if (epics.containsKey(id)) {
            return true;
        } else return subtasks.containsKey(id);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException, TimeConflictException {
        if (!hasTimeConflict(task)) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            id++;
        } else {
            throw new TimeConflictException("Новая задача " + task + " пересекается с существующими задачами.");
        }

    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        epics.put(epic.getId(), epic);
        id++;
    }

    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException, TimeConflictException, InvalidEpicIdException {
        if (epics.containsKey(subtask.getEpicID())) {
            if (!hasTimeConflict(subtask)) {
                epics.get(subtask.getEpicID()).addToSubtasks(subtask);
                epics.get(subtask.getEpicID()).changeStatus();
                epics.get(subtask.getEpicID()).recalculateTimes();
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
                id++;
            } else {
                throw new TimeConflictException("Новая задача " + subtask + " пересекается с существующими задачами.");
            }

        } else {
            throw new InvalidEpicIdException("Эпик с ID " + subtask.getEpicID() + " не найден.");
        }

    }

    @Override
    public void updateTask(int id, Task task) throws ManagerSaveException {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else if (epics.containsKey(id)) {
            epics.put(id, (Epic) task);
            epics.get(id).recalculateTimes();
        } else if (subtasks.containsKey(id)) {
            subtasks.put(id, (Subtask) task);
            epics.get(subtasks.get(id).getEpicID()).changeStatus();
            epics.get(subtasks.get(id).getEpicID()).recalculateTimes();
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        tasks.remove(id);
        if (subtasks.containsKey(id)) {
            int idForEpic = subtasks.get(id).getEpicID();
            subtasks.remove(id);
            epics.get(idForEpic).recalculateTimes();
        }
        if (epics.containsKey(id)) {
            epics.get(id).getSubtasks()
                    .forEach(subtask -> {
                        try {
                            deleteTaskById(subtask.getId());
                        } catch (ManagerSaveException e) {
                            throw new RuntimeException(e);
                        }
                    });
            epics.remove(id);
        }
    }

    public void deleteAllTasks() {
        historyManager.deleteHistory();
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id).getSubtasks();
        } else return null;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public int getId() {
        return id;
    }

    public boolean hasTimeConflict(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getStartTime() != null && existingTask.getEndTime() != null)
                .anyMatch(existingTask -> newTask.getStartTime().isBefore(existingTask.getEndTime()) && newTask.getEndTime().isAfter(existingTask.getStartTime()));
    }
}
