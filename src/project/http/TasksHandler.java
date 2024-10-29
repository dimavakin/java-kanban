package project.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.manager.TaskManager;
import project.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, "Ошибка при сохранении данных.", 500);
        } catch (Exception e) {
            sendText(exchange, "Произошла ошибка сервера: " + e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");

        if (splitPath.length == 2) {
            String response = gson.toJson(taskManager.getTasks());
            sendText(exchange, response, 200);
        } else if (splitPath.length == 3) {
            try {
                int taskId = Integer.parseInt(splitPath[2]);

                Task task = taskManager.getTask(taskId);

                String response = gson.toJson(task);
                sendText(exchange, response, 200);

            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            } catch (NumberFormatException e) {
                sendHasInteractions(exchange);
            }
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException, ManagerSaveException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            try {
                task.setId(taskManager.getId());
                taskManager.createTask(task);
                sendText(exchange, "Задача успешно создана.", 201);
            } catch (ManagerSaveException | TimeConflictException e) {
                sendHasInteractions(exchange);
            }
        } else {
            if (taskManager.hasTask(task.getId())) {
                taskManager.updateTask(task.getId(), task);
                sendText(exchange, "Задача успешно обновлена.", 201);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws ManagerSaveException, IOException {
        String[] splitPath = path.split("/");

        if (splitPath.length == 3) {
            int taskId = Integer.parseInt(splitPath[2]);
            taskManager.deleteTaskById(taskId);
            sendText(exchange, "Задача успешно удалена.", 200);
        } else {
            sendNotFound(exchange);
        }
    }

}