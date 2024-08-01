import java.util.Objects;

public class Task {
    private int id;
    private Status status;
    private String name;
    private String description;

    public Task(int id, String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(int id, String name, String description) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        status = Status.NEW;
    }

    public int getId() {
        return id;
    }



    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && status == task.status && Objects.equals(name, task.name) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, name, description);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
