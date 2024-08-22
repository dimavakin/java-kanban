package project.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTest {
    Epic epic = new Epic(0,"Test addNewEpic", "Test addNewEpic description");

    @Test
    void getSubtaskIds() {
        epic.addToSubtasksIds(1);
        assertNotNull(epic.getSubtaskIds().get(0), "Сабтаски не добавляются в список.");
        assertEquals(1, epic.getSubtaskIds().size(), "Неверное количество сабтасок.");
    }
}