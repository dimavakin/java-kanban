package project.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import project.manager.TaskManager;
import project.task.Task;

import java.io.IOException;
import java.util.TreeSet;

class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedTasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

            String response = gson.toJson(prioritizedTasks);

            sendText(exchange, response, 200);
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}