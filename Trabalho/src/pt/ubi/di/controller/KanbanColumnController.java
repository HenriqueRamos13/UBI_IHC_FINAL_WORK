package pt.ubi.di.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import pt.ubi.di.model.KanbanModel;
import pt.ubi.di.model.Task;

public class KanbanColumnController implements Initializable {

    @FXML
    private VBox columnRoot;
    
    @FXML
    private Label columnTitle;
    
    @FXML
    private VBox tasksContainer;
    
    private ObservableList<Task> tasks;
    private String status;
    private KanbanModel model;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setColumnInfo(String title, String status, ObservableList<Task> tasks, KanbanModel model) {
        this.columnTitle.setText(title);
        this.status = status;
        this.tasks = tasks;
        this.model = model;
        
        tasks.addListener((ListChangeListener.Change<? extends Task> c) -> {
            refreshTasks();
        });
        
        setupDragAndDrop();
        
        refreshTasks();
    }
    
    private void refreshTasks() {
        tasksContainer.getChildren().clear();
        
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/pt/ubi/di/view/TaskCardView.fxml"));
                    Parent taskView = loader.load();
                    
                    TaskCardController controller = loader.getController();
                    controller.setTask(task);
                    controller.setModel(model);
                    
                    task.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                        refreshTasks();
                    });
                    
                    tasksContainer.getChildren().add(taskView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void setupDragAndDrop() {
        tasksContainer.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != tasksContainer &&
                event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        
        tasksContainer.setOnDragEntered((DragEvent event) -> {
            if (event.getGestureSource() != tasksContainer &&
                event.getDragboard().hasString()) {
                tasksContainer.getStyleClass().add("drag-target");
            }
            event.consume();
        });
        
        tasksContainer.setOnDragExited((DragEvent event) -> {
            tasksContainer.getStyleClass().remove("drag-target");
            event.consume();
        });
        
        tasksContainer.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasString()) {
                Task draggedTask = KanbanController.getDraggedTask();
                if (draggedTask != null) {
                    draggedTask.setStatus(status);
                    
                    KanbanController.refreshAllColumns();
                    
                    success = true;
                }
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
        
        tasksContainer.setOnDragDone((DragEvent event) -> {
            KanbanController.refreshAllColumns();
            event.consume();
        });
    }
    
    public void refreshTasksView() {
        refreshTasks();
    }
    
    public VBox getRoot() {
        return columnRoot;
    }
} 