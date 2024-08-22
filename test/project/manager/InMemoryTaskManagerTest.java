package project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.status.Status;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void BeforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddNewTaskTest() {
        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = taskManager.getId();
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final Map<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testAddNewEpicTest() {
        Epic epic = new Epic(taskManager.getId(), "Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.getId();
        taskManager.createEpic(epic);

        final Task savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final Map<Integer, Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void testAddNewSubtask() {
        Epic epic = new Epic(taskManager.getId(), "Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(taskManager.getId(), "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        final int subtaskId = taskManager.getId();
        taskManager.createSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final Map<Integer, Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(1), "Подзадачи не совпадают.");
    }

    @Test
    void testSubtaskOwnEpic() {
        Subtask subtask = new Subtask(0, "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, 0);
        taskManager.createSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtask(0);

        assertNull(savedSubtask, "Подзадача найдена.");
    }

    @Test
    public void testGetDefaultTaskManagerReturnsInitializedInstance() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    public void testAddingAndFindingTasksById() {
        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic(taskManager.getId(), "Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.getId();
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(taskManager.getId(), "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, epicId);
        taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTask(0), "Задача не найдена");
        assertEquals(epic, taskManager.getEpic(1), "Эпик не найден");
        assertEquals(subtask, taskManager.getSubtask(2), "Подзадача не найдена");
    }

    @Test
    public void testGeneratedAndExplicitIdsDoNotConflict() {
        Task explicitIdTask = new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW);

        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.createTask(task);

        assertEquals(explicitIdTask, taskManager.getTask(0), "Задачи с сгенерированным id и с заданым id не совпадают");
    }


}