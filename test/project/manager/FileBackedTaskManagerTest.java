package project.manager;

import org.junit.jupiter.api.Test;
import project.task.Epic;
import project.task.Subtask;
import project.task.Task;

import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    @Test
    void testSaveAndLoadEmptyFile() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager());
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Задачи не пустые");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Эпики не пустые");
        assertTrue(loadedManager.getAllSubtask().isEmpty(), "Подзадачи не пустые");
    }

    @Test
    void testLoadTasks() throws Exception {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("id,type,name,status,description,epic\n");
            writer.write("1,TASK,Task 1,NEW,Task description\n");
            writer.write("2,EPIC,Epic 1,NEW,Epic description\n");
            writer.write("3,SUBTASK,Subtask 1,NEW,Subtask description,2\n");
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Отсутствует задача");
        assertEquals(1, loadedManager.getAllEpics().size(), "Отсутствует эпик");
        assertEquals(1, loadedManager.getAllSubtask().size(), "Отсутствует подзадача");

        Task task = loadedManager.getAllTasks().getFirst();
        assertEquals(1, task.getId());
        assertEquals("Task 1", task.getName());
        assertEquals("Task description", task.getDescription());

        Epic epic = loadedManager.getAllEpics().getFirst();
        assertEquals(2, epic.getId());
        assertEquals("Epic 1", epic.getName());

        Subtask subtask = loadedManager.getAllSubtask().getFirst();
        assertEquals(3, subtask.getId());
        assertEquals("Subtask 1", subtask.getName());
        assertEquals(2, subtask.getEpicID());
    }
}