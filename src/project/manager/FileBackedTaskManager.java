package project.manager;

import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.status.Status;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data.csv"))) {
            bw.write("id,type,name,status,description,duration,startTime,epic\n");

            List<Task> tasks = super.getAllTasks();
            for (Task task : tasks) {
                bw.write(task.toString() + "\n");
            }
            List<Epic> epics = super.getAllEpics();
            for (Task epic : epics) {
                bw.write(epic.toString() + "\n");
            }
            List<Subtask> subtasks = super.getAllSubtask();
            for (Task subtask : subtasks) {
                bw.write(subtask.toString() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл ", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException, TimeConflictException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(new InMemoryHistoryManager());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();

            while (br.ready()) {
                String line = br.readLine();

                String[] fields = line.split(",");

                int id = Integer.parseInt(fields[0]);
                String type = fields[1];
                String name = fields[2];
                String status = fields[3];
                String description = fields[4];
                String duration = fields[5];
                String startTime = fields[6];

                switch (type) {
                    case "TASK":
                        Task task = new Task(id, name, description, Status.valueOf(status), Duration.parse(duration), LocalDateTime.parse(startTime));
                        taskManager.createTask(task);
                        break;

                    case "EPIC":
                        Epic epic = new Epic(id, name, description);
                        taskManager.createEpic(epic);
                        break;

                    case "SUBTASK":
                        int epicId = Integer.parseInt(fields[7]);
                        Subtask subtask = new Subtask(id, name, description, Status.valueOf(status), Duration.parse(duration), LocalDateTime.parse(startTime), epicId);
                        taskManager.createSubtask(subtask);
                        break;

                    default:
                        throw new ManagerSaveException("Неизвестный тип задачи: " + type);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла ", e);
        }
        return taskManager;
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException, TimeConflictException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException, TimeConflictException {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(int id, Task task) throws ManagerSaveException {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAll() throws ManagerSaveException {
        super.deleteAll();
        save();
    }

}
