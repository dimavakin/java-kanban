package project.task;

import org.junit.jupiter.api.Test;
import project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    Epic epic = new Epic(0, "Test addNewEpic", "Test addNewEpic description");
    Subtask subtask = new Subtask(1, "Test addNewSubtask", "Test addNewSubtask description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2024, 10, 23, 15, 30), 0);

    @Test
    void getEpicID() {
        assertEquals(0, subtask.getEpicID(), "Неверный id эпика.");
    }
}