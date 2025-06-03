package pt.ubi.di.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import pt.ubi.di.service.NotificationService;

public class KanbanModel {

    private ObservableList<Task> tasks;

    public static final String TO_DO = "TO_DO";
    public static final String DOING = "DOING";
    public static final String DONE = "DONE";

    public KanbanModel() {
        tasks = FXCollections.observableArrayList();
        loadTasksFromFile();

        tasks.addListener((ListChangeListener.Change<? extends Task> c) -> {
            TaskFileManager.saveTasks(tasks);
        });

        // Inicializa o serviço de notificações
        NotificationService.getInstance().setTasks(tasks);
    }

    private void loadTasksFromFile() {
        tasks.addAll(TaskFileManager.loadTasks());
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public Task createNewTask() {
        Task newTask = new Task("Nova Tarefa", "Descrição da tarefa", TO_DO);
        tasks.add(newTask);
        return newTask;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}