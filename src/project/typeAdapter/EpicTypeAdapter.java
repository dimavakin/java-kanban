package project.typeAdapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import project.task.Epic;

import java.io.IOException;

public class EpicTypeAdapter extends TypeAdapter<Epic> {
    @Override
    public void write(JsonWriter out, Epic epic) throws IOException {
        out.beginObject();
        out.name("id").value(epic.getId());
        out.name("name").value(epic.getName());
        out.name("description").value(epic.getDescription());
        out.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        int id = 0;
        String name = null;
        String description = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    id = in.nextInt();
                    break;
                case "name":
                    name = in.nextString();
                    break;
                case "description":
                    description = in.nextString();
                    break;
            }
        }
        in.endObject();

        return new Epic(id, name, description);
    }
}
