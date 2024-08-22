package project.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void testHistoryManagerPreservesPreviousVersionOfTask() {
        TaskManager taskManager = new InMemoryTaskManager();
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
    public void testGetDefaultHistoryManagerReturnsInitializedInstance() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }
}