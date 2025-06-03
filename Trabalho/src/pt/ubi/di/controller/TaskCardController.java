package pt.ubi.di.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import pt.ubi.di.model.KanbanModel;
import pt.ubi.di.model.Task;
import pt.ubi.di.model.TaskFileManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    
    @FXML
    private Button setTimeButton;
    
    @FXML
    private Label dueDateLabel;
    
    private Task task;
    private KanbanModel model;
    private DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                titleField.selectAll();
            } else if (!newVal) {
                task.setTitle(titleField.getText());
                TaskFileManager.saveTasks(model.getTasks());
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
                TaskFileManager.saveTasks(model.getTasks());
            }
        });
        
        descriptionArea.setOnMouseClicked(event -> {
            descriptionArea.selectAll();
        });
        
        styleDeleteButton();
        setupDragAndDrop();
    }
    
    @FXML
    private void handleSetTime(ActionEvent event) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Definir Horário");
        dialog.setHeaderText("Escolha o horário para completar a tarefa");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        // Spinners para data e hora
        Spinner<Integer> daySpinner = new Spinner<>(1, 31, LocalDateTime.now().getDayOfMonth());
        Spinner<Integer> monthSpinner = new Spinner<>(1, 12, LocalDateTime.now().getMonthValue());
        Spinner<Integer> yearSpinner = new Spinner<>(LocalDateTime.now().getYear(), LocalDateTime.now().getYear() + 10, LocalDateTime.now().getYear());
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, LocalDateTime.now().getHour());
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, LocalDateTime.now().getMinute());
        
        grid.add(new Label("Dia:"), 0, 0);
        grid.add(daySpinner, 1, 0);
        grid.add(new Label("Mês:"), 0, 1);
        grid.add(monthSpinner, 1, 1);
        grid.add(new Label("Ano:"), 0, 2);
        grid.add(yearSpinner, 1, 2);
        grid.add(new Label("Hora:"), 0, 3);
        grid.add(hourSpinner, 1, 3);
        grid.add(new Label("Minuto:"), 0, 4);
        grid.add(minuteSpinner, 1, 4);
        
        dialogPane.setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return LocalDateTime.of(
                    yearSpinner.getValue(),
                    monthSpinner.getValue(),
                    daySpinner.getValue(),
                    hourSpinner.getValue(),
                    minuteSpinner.getValue()
                );
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(dateTime -> {
            task.setDueDateTime(dateTime);
            updateDueDateLabel();
            TaskFileManager.saveTasks(model.getTasks());
        });
    }
    
    private void updateDueDateLabel() {
        LocalDateTime dueDateTime = task.getDueDateTime();
        if (dueDateTime != null) {
            dueDateLabel.setText("⏰ " + dueDateTime.format(displayFormatter));
            dueDateLabel.setVisible(true);
            dueDateLabel.setManaged(true);
        } else {
            dueDateLabel.setVisible(false);
            dueDateLabel.setManaged(false);
        }
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
            updateDueDateLabel();
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