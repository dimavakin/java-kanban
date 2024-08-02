package project.main;

import project.manager.TaskManager;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;
import project.status.Status;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task(taskManager.getId(), "Поставить чайник", "Нужно поставить чайник в 17:00", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task(taskManager.getId(), "Поехать в деревню", "Нужно поехать в деревню", Status.NEW);
        taskManager.createTask(task2);
        Epic epic3 = new Epic(taskManager.getId(), "Приготовить покушать", "Нужно приготовить поесть к 20:00");
        taskManager.createEpic(epic3);
        Subtask subtask4 = new Subtask(taskManager.getId(), "Купить соль", "Нужно купить соль", Status.NEW, 3);
        taskManager.createSubtask(subtask4);
        Subtask subtask5 = new Subtask(taskManager.getId(), "Вскипятить воду", "Нужно вскипятить воду", Status.NEW, 3);
        taskManager.createSubtask(subtask5);
        Epic epic6 = new Epic(taskManager.getId(), "Сходить погулять", "Нужно пойти на прогулку к 21:00");
        taskManager.createEpic(epic6);
        Subtask subtask7 = new Subtask(taskManager.getId(), "Надеть кроссовки", "Нужно надеть кроссовки", Status.NEW, 6);
        taskManager.createSubtask(subtask7);

        System.out.println("1 __________________________________");
        System.out.println("Обычные задачи: " + taskManager.getTasks());
        System.out.println();
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println();
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        taskManager.updateTask(1, new Task(5, "Поехать в деревню", "Нужно поехать в деревню", Status.DONE));
        taskManager.updateTask(5, new Subtask(5, "Подсластить воду", "Нужно Подсластить воду", Status.DONE, 3));
        taskManager.updateTask(4, new Subtask(5, "Подсластить воду", "Нужно Подсластить воду", Status.IN_PROGRESS, 3));
        taskManager.updateTask(7, new Subtask(5, "Подсластить воду", "Нужно Подсластить воду", Status.DONE, 6));

        System.out.println();
        System.out.println("2 __________________________________");
        System.out.println("Обычные задачи: " + taskManager.getTasks());
        System.out.println();
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println();
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(6);

        System.out.println();
        System.out.println("3 __________________________________");
        System.out.println("Обычные задачи: " + taskManager.getTasks());
        System.out.println();
        System.out.println("Эпики: " + taskManager.getEpics());
        System.out.println();
        System.out.println("Подзадачи: " + taskManager.getSubtasks());

    }
}
