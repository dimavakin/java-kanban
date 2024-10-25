package project.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import project.exception.ManagerSaveException;
import project.manager.TaskManager;
import project.task.Epic;
import project.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                handleGet(exchange, path);
                break;
            case "POST":
                try {
                    handlePost(exchange);
                } catch (ManagerSaveException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "DELETE":
                try {
                    handleDelete(exchange, path);
                } catch (ManagerSaveException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");


        if (splitPath.length == 2) {
            String response = gson.toJson(taskManager.getEpics());
            sendText(exchange, response, 200);
        } else if (splitPath.length == 3) {
            try {
                int epicId = Integer.parseInt(splitPath[2]);

                Task epic = taskManager.getEpic(epicId);

                String response = gson.toJson(epic);
                sendText(exchange, response, 200);

            } catch (NoSuchElementException e) {
                sendNotFound(exchange);
            } catch (NumberFormatException e) {
                sendHasInteractions(exchange);
            }
        } else if (splitPath.length == 4 && splitPath[3].equals("subtasks")) {
            try {
                int epicId = Integer.parseInt(splitPath[2]);

                String response = gson.toJson(taskManager.getSubtasksByEpicId(epicId));
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
        Epic epic = gson.fromJson(body, Epic.class);
        try {
            epic.setId(taskManager.getId());
            taskManager.createEpic(epic);
            sendText(exchange, "Задача успешно создана.", 201);
        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws ManagerSaveException, IOException {
        String[] splitPath = path.split("/");

        if (splitPath.length == 3) {
            int epicId = Integer.parseInt(splitPath[2]);
            taskManager.deleteTaskById(epicId);
            sendText(exchange, "Эпик успешно удален.", 201);
        } else {
            sendNotFound(exchange);
        }
    }

}