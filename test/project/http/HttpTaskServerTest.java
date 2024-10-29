package project.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.exception.InvalidEpicIdException;
import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.manager.HistoryManager;
import project.manager.InMemoryHistoryManager;
import project.manager.InMemoryTaskManager;
import project.manager.TaskManager;
import project.status.Status;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;
import project.typeAdapter.EpicTypeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(historyManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    HttpTaskServerTest() throws IOException {
    }


    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(0, "Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Gson gson = HttpTaskServer.getGson();
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException {
        Task task = new Task(1, "Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Gson gson = HttpTaskServer.getGson();
        Type taskMapType = new TypeToken<Map<Integer, Task>>() {
        }.getType();
        Map<Integer, Task> tasks = gson.fromJson(response.body(), taskMapType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasks.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException {
        Task task = new Task(2, "Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Map<Integer, Task> tasksFromManager = manager.getTasks();

        assertTrue(tasksFromManager.isEmpty(), "Задача не была удалена");
    }

    @Test
    void testAddSubtask() throws IOException, InterruptedException, ManagerSaveException {
        Epic epic = new Epic(1, "Test 23", "Testing task 23");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 1);
        Gson gson = HttpTaskServer.getGson();
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(2).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetSubtasks() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException, InvalidEpicIdException {
        Epic epic = new Epic(1, "Test 23", "Testing task 23");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.now(), 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Gson gson = HttpTaskServer.getGson();
        Type taskMapType = new TypeToken<Map<Integer, Subtask>>() {
        }.getType();
        Map<Integer, Subtask> subtasks = gson.fromJson(response.body(), taskMapType);

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество задач");
        assertEquals("Test 1", subtasks.get(2).getName(), "Некорректное имя задачи");
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException, InvalidEpicIdException {
        Epic epic = new Epic(1, "Test 23", "Testing task 23");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Map<Integer, Subtask> tasksFromManager = manager.getSubtasks();

        assertTrue(tasksFromManager.isEmpty(), "Задача не была удалена");
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException, ManagerSaveException {
        Epic epic = new Epic(1, "Test 23", "Testing task 23");

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Epic.class, new EpicTypeAdapter())
                .create();
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 23", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException, InvalidEpicIdException {
        Epic epic = new Epic(1, "Test 1", "Testing task 1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(15), LocalDateTime.now(), 1);
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Gson gson = HttpTaskServer.getGson();
        Type taskMapType = new TypeToken<Map<Integer, Epic>>() {
        }.getType();
        Map<Integer, Epic> tasks = gson.fromJson(response.body(), taskMapType);
        System.out.println(tasks);
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasks.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException {
        Task task = new Task(0, "Test 4", "Testing task 4",
                Status.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        manager.createTask(task);
        manager.getTaskById(0);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Gson gson = HttpTaskServer.getGson();
        Type historyListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), historyListType);

        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Некорректное количество элементов в истории");
        assertEquals("Test 4", history.get(0).getName(), "Некорректное имя задачи в истории");
    }

    @Test
    void testGetPrioritizedTasks() throws IOException, InterruptedException, TimeConflictException, ManagerSaveException {
        Task task1 = new Task(0, "Priority Task 1", "Testing priority task 1",
                Status.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        Task task2 = new Task(1, "Priority Task 2", "Testing priority task 2",
                Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());

        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Gson gson = HttpTaskServer.getGson();
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> prioritizedTasks = gson.fromJson(response.body(), listType);

        assertNotNull(prioritizedTasks, "Приоритетные задачи не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество приоритетных задач");
        assertEquals("Priority Task 2", prioritizedTasks.getFirst().getName(), "Некорректный порядок задач");
        assertEquals("Priority Task 1", prioritizedTasks.getLast().getName(), "Некорректный порядок задач");
    }
}