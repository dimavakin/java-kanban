package project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.exception.ManagerSaveException;
import project.status.Status;
import project.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void BeforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW);
    }

    @Test
    void testAdd() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История пустая и равняется нулю.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    void testHistoryManagerPreservesPreviousVersionOfTask() throws ManagerSaveException {
        TaskManager taskManager = new InMemoryTaskManager(historyManager);
        Task task = new Task(taskManager.getId(), "Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.createTask(task);

        historyManager.add(task);

        taskManager.updateTask(0, new Task(0, "Test addTask", "Test addTask description", Status.DONE));

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не добавилась в историю");

        Task taskInHistory = history.get(0);
        assertEquals(0, taskInHistory.getId(), "id в истории и добавленной задачи не совпадают");
        assertEquals(Status.NEW, taskInHistory.getStatus(), "Статус задачи добавленной в историю изменился после обновления");
    }

    @Test
    void testGetDefaultHistoryManagerReturnsInitializedInstance() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    void testRemoveTaskFromMiddle() {
        historyManager.add(new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW));
        historyManager.add(new Task(1, "Test addSecondTask", "Test addSecondTask description", Status.NEW));
        historyManager.add(new Task(2, "Test addThirdTask", "Test addThirdTask description", Status.NEW));

        historyManager.remove(1);
        final List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не обновилась.");
        assertEquals(new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW), history.get(0), "Первый элемент в истории не совпадает.");
        assertEquals(new Task(2, "Test addThirdTask", "Test addThirdTask description", Status.NEW), history.get(1), "Третий элемент в истории не совпадает.");

    }

    @Test
    void testRemoveFirstTask() {
        historyManager.add(new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW));
        historyManager.add(new Task(1, "Test addSecondTask", "Test addSecondTask description", Status.NEW));
        historyManager.add(new Task(2, "Test addThirdTask", "Test addThirdTask description", Status.NEW));

        historyManager.remove(0);
        final List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не обновилась.");
        assertEquals(new Task(1, "Test addSecondTask", "Test addSecondTask description", Status.NEW), history.get(0), "Первый элемент в истории не совпадает.");
        assertEquals(new Task(2, "Test addThirdTask", "Test addThirdTask description", Status.NEW), history.get(1), "Третий элемент в истории не совпадает.");

    }

    @Test
    void testAddAndReAddTask() {
        Task task1 = new Task(1, "Test Task 1", "Description 1", Status.NEW);
        Task task2 = new Task(2, "Test Task 2", "Description 2", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи без дубликатов.");
        assertEquals(task2, history.get(0), "Первая задача должна быть task2.");
        assertEquals(task1, history.get(1), "Вторая задача должна быть task1.");
    }

    @Test
    void testRemoveLastTask() {
        historyManager.add(new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW));
        historyManager.add(new Task(1, "Test addSecondTask", "Test addSecondTask description", Status.NEW));
        historyManager.add(new Task(2, "Test addThirdTask", "Test addThirdTask description", Status.NEW));

        historyManager.remove(2);
        final List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не обновилась.");
        assertEquals(new Task(0, "Test addNewTask", "Test addNewTask description", Status.NEW), history.get(0), "Первый элемент в истории не совпадает.");
        assertEquals(new Task(1, "Test addSecondTask", "Test addSecondTask description", Status.NEW), history.get(1), "Третий элемент в истории не совпадает.");

    }
}