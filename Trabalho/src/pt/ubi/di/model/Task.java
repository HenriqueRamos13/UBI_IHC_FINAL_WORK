package pt.ubi.di.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.time.LocalDateTime;

public class Task {
    private StringProperty title;
    private StringProperty description;
    private StringProperty status; // TO_DO, DOING, DONE
    private ObjectProperty<LocalDateTime> dueDateTime;
    private BooleanProperty notificationSeen;
    
    public Task(String title, String description, String status) {
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.status = new SimpleStringProperty(status);
        this.dueDateTime = new SimpleObjectProperty<>(null);
        this.notificationSeen = new SimpleBooleanProperty(false);
    }
    
    public Task(String title, String description, String status, LocalDateTime dueDateTime) {
        this(title, description, status);
        this.dueDateTime.set(dueDateTime);
    }
    
    // Getters e Setters
    public String getTitle() {
        return title.get();
    }
    
    public void setTitle(String title) {
        this.title.set(title);
    }
    
    public StringProperty titleProperty() {
        return title;
    }
    
    public String getDescription() {
        return description.get();
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public StringProperty descriptionProperty() {
        return description;
    }
    
    public String getStatus() {
        return status.get();
    }
    
    public void setStatus(String status) {
        this.status.set(status);
    }
    
    public StringProperty statusProperty() {
        return status;
    }
    
    // Novos getters e setters para dueDateTime
    public LocalDateTime getDueDateTime() {
        return dueDateTime.get();
    }
    
    public void setDueDateTime(LocalDateTime dateTime) {
        this.dueDateTime.set(dateTime);
        // Quando uma nova data é definida, reseta o status de visualização
        setNotificationSeen(false);
    }
    
    public ObjectProperty<LocalDateTime> dueDateTimeProperty() {
        return dueDateTime;
    }
    
    // Novos getters e setters para notificationSeen
    public boolean isNotificationSeen() {
        return notificationSeen.get();
    }
    
    public void setNotificationSeen(boolean seen) {
        this.notificationSeen.set(seen);
    }
    
    public BooleanProperty notificationSeenProperty() {
        return notificationSeen;
    }
} 