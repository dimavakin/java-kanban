package project.main;

import project.exception.ManagerSaveException;
import project.exception.TimeConflictException;
import project.manager.FileBackedTaskManager;
import project.manager.HistoryManager;
import project.manager.InMemoryHistoryManager;
import project.manager.InMemoryTaskManager;
import project.status.Status;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws ManagerSaveException, TimeConflictException {
        System.out.println("Поехали!");
        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new FileBackedTaskManager(historyManager);

        Task task1 = new Task(taskManager.getId(), "Поставить чайник", "Нужно поставить чайник в 17:00", Status.NEW, Duration.ofHours(1), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task(taskManager.getId(), "Поехать в деревню", "Нужно поехать в деревню", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 10, 23, 15, 30));
        taskManager.createTask(task2);
        Epic epic3 = new Epic(taskManager.getId(), "Приготовить покушать", "Нужно приготовить поесть к 20:00");
        taskManager.createEpic(epic3);
        Epic epic6 = new Epic(taskManager.getId(), "Сходить погулять", "Нужно пойти на прогулку к 21:00");
        taskManager.createEpic(epic6);
        Task task5 = new Task(taskManager.getId(), "Поставить чайник5", "Нужно поставить чайник в 17:00", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 10, 24, 15, 30));
        taskManager.createTask(task5);
        Subtask subtask4 = new Subtask(taskManager.getId(), "Купить соль", "Нужно купить соль", Status.NEW, Duration.ofHours(24), LocalDateTime.of(2024, 10, 24, 15, 30), 2);
        taskManager.createSubtask(subtask4);
        Subtask subtask8 = new Subtask(taskManager.getId(), "Купить соль", "Нужно купить соль", Status.NEW, Duration.ofHours(24), LocalDateTime.of(2029, 10, 24, 15, 30), 2);
        taskManager.createSubtask(subtask8);
        Subtask subtask5 = new Subtask(taskManager.getId(), "Вскипятить воду", "Нужно вскипятить воду", Status.NEW, Duration.ofHours(4), LocalDateTime.of(2025, 10, 27, 15, 30), 2);
        taskManager.createSubtask(subtask5);
        Subtask subtask7 = new Subtask(taskManager.getId(), "Надеть кроссовки", "Нужно надеть кроссовки", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 10, 26, 15, 30), 3);
        taskManager.createSubtask(subtask7);



        taskManager.updateTask(0, new Task(0, "Поставить чайник 2", "Нужно поставить чайник в 17:00 2", Status.DONE, Duration.ofDays(1), LocalDateTime.now()));

        taskManager.deleteTaskById(1);

        System.out.println();
        System.out.println("3 __________________________________");
        System.out.println("Обычные задачи: " + taskManager.getTasks());
        System.out.println();
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println();
        System.out.println("Подзадачи: " + taskManager.getSubtasks());
        System.out.println(Duration.ofDays(6)+"                       "+ LocalDateTime.now());
        System.out.println("Отсортированные задачи " +taskManager.getPrioritizedTasks());
    }
}
