package project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.status.Status;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void BeforeEach() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void testAddNewTaskTest() throws ManagerSaveException, TimeConflictException {
        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30));
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
    void testAddNewEpicTest() throws ManagerSaveException {
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
    void testAddNewSubtask() throws ManagerSaveException, TimeConflictException {
        Epic epic = new Epic(taskManager.getId(), "Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(taskManager.getId(), "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30), 0);
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
    void testSubtaskOwnEpic() throws ManagerSaveException, TimeConflictException {
        Subtask subtask = new Subtask(0, "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 11, 23, 15, 30), 0);
        taskManager.createSubtask(subtask);

        final Task savedSubtask = taskManager.getSubtask(0);

        assertNull(savedSubtask, "Подзадача найдена.");
    }

    @Test
    void testGetDefaultTaskManagerReturnsInitializedInstance() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }

    @Test
    void testAddingAndFindingTasksById() throws ManagerSaveException, TimeConflictException {
        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30));
        taskManager.createTask(task);

        Epic epic = new Epic(taskManager.getId(), "Test addNewEpic", "Test addNewEpic description");
        final int epicId = taskManager.getId();
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(taskManager.getId(), "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2026, 10, 23, 15, 30), epicId);
        taskManager.createSubtask(subtask);

        assertEquals(task, taskManager.getTask(0), "Задача не найдена");
        assertEquals(epic, taskManager.getEpic(1), "Эпик не найден");
        assertEquals(subtask, taskManager.getSubtask(2), "Подзадача не найдена");
    }

    @Test
    void testGeneratedAndExplicitIdsDoNotConflict() throws ManagerSaveException, TimeConflictException {
        Task explicitIdTask = new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30));

        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2026, 10, 23, 15, 30));
        taskManager.createTask(task);

        assertEquals(explicitIdTask, taskManager.getTask(0), "Задачи с сгенерированным id и с заданым id не совпадают");
    }

    @Test
    void shouldDetectTimeConflict() throws ManagerSaveException, TimeConflictException {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2024, 10, 22, 10, 0));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 10, 22, 11, 0));

        taskManager.createTask(task1);

        assertTrue(taskManager.hasTimeConflict(task2), "Должно быть пересечение по времени между задачами.");
    }

    @Test
    void shouldNotDetectTimeConflict() throws ManagerSaveException, TimeConflictException {
        Task task1 = new Task(1, "Task 1", "Description", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2024, 10, 22, 10, 0));
        Task task2 = new Task(2, "Task 2", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 10, 22, 13, 0));

        taskManager.createTask(task1);

        assertFalse(taskManager.hasTimeConflict(task2), "Не должно быть пересечения по времени между задачами.");
    }


}