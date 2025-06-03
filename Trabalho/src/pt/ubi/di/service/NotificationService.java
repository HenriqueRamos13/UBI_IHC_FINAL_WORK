package pt.ubi.di.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import pt.ubi.di.model.Task;
import pt.ubi.di.model.TaskFileManager;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

public class NotificationService {
    private static NotificationService instance;
    private final ScheduledExecutorService scheduler;
    private ObservableList<Task> tasks;
    
    private NotificationService() {
        scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    public void setTasks(ObservableList<Task> tasks) {
        this.tasks = tasks;
        startMonitoring();
    }
    
    private void startMonitoring() {
        // Verifica as tasks a cada minuto
        scheduler.scheduleAtFixedRate(() -> {
            checkTasks();
        }, 0, 1, TimeUnit.MINUTES);
    }
    
    private void checkTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : tasks) {
            LocalDateTime dueDateTime = task.getDueDateTime();
            if (dueDateTime != null && 
                !task.getStatus().equals("DONE") && 
                !task.isNotificationSeen()) {
                // Verifica se está na hora da task (com margem de 1 minuto)
                if (Math.abs(now.getMinute() - dueDateTime.getMinute()) <= 1 &&
                    now.getHour() == dueDateTime.getHour() &&
                    now.getDayOfYear() == dueDateTime.getDayOfYear() &&
                    now.getYear() == dueDateTime.getYear()) {
                    showNotification(task);
                }
            }
        }
    }
    
    private void showNotification(Task task) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Lembrete de Tarefa");
            alert.setHeaderText("Hora de completar a tarefa!");
            alert.setContentText("A tarefa \"" + task.getTitle() + "\" está programada para ser concluída agora.\n\n" +
                               "Descrição: " + task.getDescription());
            
            // Quando o usuário clicar em OK, marca a notificação como vista
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                task.setNotificationSeen(true);
                TaskFileManager.saveTasks(tasks);
            }
        });
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
} 