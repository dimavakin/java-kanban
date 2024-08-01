import java.util.*;


public class TaskManager {
    private int id = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Получение списка всех задач
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
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
            epics.get(subtask.getEpicID()).addToSubtasksIdsList(subtask.getId());
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
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).subtaskIds) {
                deleteTaskById(subtaskId);
            }
            epics.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        }
    }

    //Получение списка всех подзадач определённого эпика.

    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtaskByEpic = new ArrayList<>();
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

        if (epic.subtaskIds.isEmpty()) {
            epics.get(epic.getId()).setStatus(Status.NEW);

        } else {
            int statusNew = 0;
            int statusDone = 0;
            int statusInProgress = 0;
            for (int id : epic.subtaskIds) {
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
