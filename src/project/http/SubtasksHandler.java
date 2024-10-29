package project.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import project.exception.InvalidEpicIdException;
import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.manager.TaskManager;
import project.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
            String response = gson.toJson(taskManager.getSubtasks());
            sendText(exchange, response, 200);
        } else if (splitPath.length == 3) {
            try {
                int subtaskId = Integer.parseInt(splitPath[2]);

                Subtask subtask = taskManager.getSubtask(subtaskId);

                String response = gson.toJson(subtask);
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

    private void handlePost(HttpExchange exchange) throws IOException, ManagerSaveException, InvalidEpicIdException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        if (subtask.getId() == 0) {
            try {
                subtask.setId(taskManager.getId());
                taskManager.createSubtask(subtask);
                sendText(exchange, "Подзадача успешно создана.", 201);
            } catch (ManagerSaveException | TimeConflictException | InvalidEpicIdException e) {
                sendHasInteractions(exchange);
            }
        } else {
            if (taskManager.hasTask(subtask.getId())) {
                taskManager.updateTask(subtask.getId(), subtask);
                sendText(exchange, "Подзадача успешно обновлена.", 201);
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
            sendText(exchange, "Подзадача успешно удалена.", 200);
        } else {
            sendNotFound(exchange);
        }
    }

}