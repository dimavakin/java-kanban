package project.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.exception.ManagerSaveException;
import project.status.Status;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EpicTest {
    private Epic epic;

    @BeforeEach
    public void BeforeEach() throws ManagerSaveException {
        epic = new Epic(1, "Test addNewEpic", "Test addNewEpic description");
    }

    @Test
    void shouldReturnNewWhenAllSubtasksAreNew() {
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30), 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2026, 10, 23, 15, 30), 1);
        epic.addToSubtasks(subtask1);
        epic.addToSubtasks(subtask2);

        epic.changeStatus();
        assertEquals(Status.NEW, epic.getStatus(), "Epic должен быть в статусе NEW, когда все подзадачи NEW.");
    }

    @Test
    void shouldReturnDoneWhenAllSubtasksAreDone() {
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description", Status.DONE, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30), 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description", Status.DONE, Duration.ofHours(1), LocalDateTime.of(2026, 10, 23, 15, 30), 1);
        epic.addToSubtasks(subtask1);
        epic.addToSubtasks(subtask2);

        epic.changeStatus();
        assertEquals(Status.DONE, epic.getStatus(), "Epic должен быть в статусе DONE, когда все подзадачи DONE.");
    }

    @Test
    void shouldReturnInProgressWhenSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30), 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description", Status.DONE, Duration.ofHours(1), LocalDateTime.of(2026, 10, 23, 15, 30), 1);
        epic.addToSubtasks(subtask1);
        epic.addToSubtasks(subtask2);

        epic.changeStatus();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic должен быть в статусе IN_PROGRESS, когда подзадачи имеют статусы NEW и DONE.");
    }

    @Test
    void shouldReturnInProgressWhenAllSubtasksInProgress() {
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2025, 10, 23, 15, 30), 1);
        Subtask subtask2 = new Subtask(3, "Subtask 2", "Description", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.of(2026, 10, 23, 15, 30), 1);
        epic.addToSubtasks(subtask1);
        epic.addToSubtasks(subtask2);

        epic.changeStatus();
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic должен быть в статусе IN_PROGRESS, когда все подзадачи IN_PROGRESS.");
    }

}