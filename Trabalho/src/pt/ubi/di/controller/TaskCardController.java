package pt.ubi.di.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.ubi.di.model.KanbanModel;
import pt.ubi.di.model.Task;

public class TaskCardController implements Initializable {

    @FXML
    private VBox taskCard;
    
    @FXML
    private HBox dragHandle;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button deleteButton;
    
    private Task task;
    private KanbanModel model;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                titleField.selectAll();
            } else if (!newVal) {
                task.setTitle(titleField.getText());
            }
        });
        
        titleField.setOnMouseClicked(event -> {
            titleField.selectAll();
        });
        
        descriptionArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                descriptionArea.selectAll();
            } else if (!newVal) {
                task.setDescription(descriptionArea.getText());
            }
        });
        
        descriptionArea.setOnMouseClicked(event -> {
            descriptionArea.selectAll();
        });
        
        styleDeleteButton();
        
        setupDragAndDrop();
    }
    
    private void styleDeleteButton() {
        deleteButton.getStyleClass().add("delete-button");
    }
    
    public void setTask(Task task) {
        this.task = task;
        updateView();
    }
    
    public void setModel(KanbanModel model) {
        this.model = model;
    }
    
    private void updateView() {
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
        }
    }
    
    public Task getTask() {
        return task;
    }
    
    @FXML
    private void handleDeleteTask(ActionEvent event) {
        if (task != null && model != null) {
            model.removeTask(task);
            
            KanbanController.refreshAllColumns();
        }
    }
    
    private void setupDragAndDrop() {
        dragHandle.setOnDragDetected((MouseEvent event) -> {
            if (task == null) return;
            
            Dragboard db = taskCard.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            
            content.putString(task.getStatus() + ":" + System.identityHashCode(task));
            db.setContent(content);
            
            KanbanController.setDraggedTask(task);
            KanbanController.setDragSource(taskCard);
            
            event.consume();
        });
        
        taskCard.setOnDragDone(event -> {
            KanbanController.refreshAllColumns();
            event.consume();
        });
        
        dragHandle.setCursor(javafx.scene.Cursor.MOVE);
        
        dragHandle.setOnMouseEntered(e -> dragHandle.getStyleClass().add("drag-active"));
        dragHandle.setOnMouseExited(e -> dragHandle.getStyleClass().remove("drag-active"));
    }
} 