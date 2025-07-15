package task_management_system;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    private final IntegerProperty id   = new SimpleIntegerProperty(-1);  // DB primary‑key
    private final StringProperty  name = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> datetime = new SimpleObjectProperty<>();
    private final BooleanProperty done = new SimpleBooleanProperty(false);

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public Task(String name, String description) {
        this.name.set(name);
        this.description.set(description);
        this.datetime.set(LocalDateTime.now());
    }

    public Task(int id, String name, String description,
                LocalDateTime datetime, boolean done) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.datetime.set(datetime);
        this.done.set(done);
    }

    public int getId()                 { return id.get(); }
    public void setId(int id)          { this.id.set(id); }
    public IntegerProperty idProperty(){ return id; }

    public String getName()            { return name.get(); }
    public void setName(String n)      { this.name.set(n); }
    public StringProperty nameProperty(){ return name; }

    public String getDescription()            { return description.get(); }
    public void setDescription(String d)      { this.description.set(d); }
    public StringProperty descriptionProperty(){ return description; }

    public LocalDateTime getDatetime()         { return datetime.get(); }
    public ObjectProperty<LocalDateTime> datetimeProperty(){ return datetime; }

    public boolean isDone()             { return done.get(); }
    public void setDone(boolean d)      { this.done.set(d); }
    public BooleanProperty doneProperty(){ return done; }

  
    public String getFormattedDatetime() {
        return datetime.get().format(FORMAT);
    }

    @Override
    public String toString() {
        return name.get() + " (" + getFormattedDatetime() + ")" +
               (done.get() ? " [Done]" : "");
    }
}
