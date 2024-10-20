package project.manager;

public class Managers {
    static HistoryManager historyManager = new InMemoryHistoryManager();

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(historyManager);
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
