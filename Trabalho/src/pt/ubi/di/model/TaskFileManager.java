package pt.ubi.di.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskFileManager {
    private static final String FILE_PATH = "tasks.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void saveTasks(ObservableList<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                // Formato: título|descrição|status|horário|notificationSeen
                String dateTimeStr = task.getDueDateTime() != null ? 
                    task.getDueDateTime().format(formatter) : "";
                
                String line = String.format("%s|%s|%s|%s|%b",
                    task.getTitle().replace("|", "\\|"),
                    task.getDescription().replace("|", "\\|"),
                    task.getStatus(),
                    dateTimeStr,
                    task.isNotificationSeen());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    String title = parts[0].replace("\\|", "|");
                    String description = parts[1].replace("\\|", "|");
                    String status = parts[2];
                    Task task = new Task(title, description, status);
                    
                    // Carrega o horário se existir
                    if (parts.length > 3 && !parts[3].isEmpty()) {
                        try {
                            LocalDateTime dueDateTime = LocalDateTime.parse(parts[3], formatter);
                            task.setDueDateTime(dueDateTime);
                        } catch (Exception e) {
                            System.err.println("Erro ao parsear data/hora: " + parts[3]);
                        }
                    }
                    
                    // Carrega o status de visualização da notificação
                    if (parts.length > 4) {
                        try {
                            boolean notificationSeen = Boolean.parseBoolean(parts[4]);
                            task.setNotificationSeen(notificationSeen);
                        } catch (Exception e) {
                            System.err.println("Erro ao parsear status de notificação: " + parts[4]);
                        }
                    }
                    
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }
}