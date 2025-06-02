package pt.ubi.di.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import pt.ubi.di.model.KanbanModel;
import pt.ubi.di.model.Task;

public class KanbanController implements Initializable {

    @FXML
    private BorderPane kanbanRoot;
    
    @FXML
    private StackPane responsiveContainer;
    
    @FXML
    private HBox columnsContainer;
    
    @FXML
    private VBox columnsContainerVertical;
    
    @FXML
    private Button addTaskButton;
    
    private KanbanModel model;
    
    private static Task draggedTask;
    private static Node dragSource;
    
    private static List<KanbanColumnController> columnControllers = new ArrayList<>();
    
    private static final double RESPONSIVE_THRESHOLD = 800.0;
    
    private long lastClickTime = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new KanbanModel();
        
        columnControllers.clear();
        
        setupKanbanColumns();
        
        setupResponsiveLayout();
    }
    
    private void setupResponsiveLayout() {
        kanbanRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
            boolean isNarrow = newVal.doubleValue() < RESPONSIVE_THRESHOLD;
            updateLayoutMode(isNarrow);
        });
        
        Platform.runLater(() -> {
            boolean isNarrow = kanbanRoot.getWidth() < RESPONSIVE_THRESHOLD;
            updateLayoutMode(isNarrow);
        });
    }
    
    private void updateLayoutMode(boolean isNarrow) {
        columnsContainer.setVisible(!isNarrow);
        columnsContainer.setManaged(!isNarrow);
        columnsContainerVertical.setVisible(isNarrow);
        columnsContainerVertical.setManaged(isNarrow);
    }
    
    private void setupKanbanColumns() {
        try {
            List<Node> columnRoots = new ArrayList<>();
            
            FXMLLoader todoLoader = new FXMLLoader(getClass().getResource("/pt/ubi/di/view/KanbanColumnView.fxml"));
            todoLoader.load();
            KanbanColumnController todoController = todoLoader.getController();
            todoController.setColumnInfo("📋 A Fazer", KanbanModel.TO_DO, model.getTasks(), model);
            columnControllers.add(todoController);
            columnRoots.add(todoController.getRoot());
            
            FXMLLoader doingLoader = new FXMLLoader(getClass().getResource("/pt/ubi/di/view/KanbanColumnView.fxml"));
            doingLoader.load();
            KanbanColumnController doingController = doingLoader.getController();
            doingController.setColumnInfo("⚙️ Em Progresso", KanbanModel.DOING, model.getTasks(), model);
            columnControllers.add(doingController);
            columnRoots.add(doingController.getRoot());
            
            FXMLLoader doneLoader = new FXMLLoader(getClass().getResource("/pt/ubi/di/view/KanbanColumnView.fxml"));
            doneLoader.load();
            KanbanColumnController doneController = doneLoader.getController();
            doneController.setColumnInfo("✅ Concluído", KanbanModel.DONE, model.getTasks(), model);
            columnControllers.add(doneController);
            columnRoots.add(doneController.getRoot());
            
            columnsContainer.getChildren().addAll(columnRoots);
            
            for (Node columnRoot : columnRoots) {
                Node clonedColumn = cloneColumnForVerticalLayout(columnRoot);
                if (clonedColumn != null) {
                    columnsContainerVertical.getChildren().add(clonedColumn);
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Node cloneColumnForVerticalLayout(Node columnRoot) {
        try {
            VBox originalColumn = (VBox) columnRoot;
            
            String title = ((javafx.scene.control.Label) findLabelInColumn(originalColumn)).getText();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pt/ubi/di/view/KanbanColumnView.fxml"));
            Node newColumn = loader.load();
            KanbanColumnController controller = loader.getController();
            
            String status = "";
            if (title.contains("A Fazer")) status = KanbanModel.TO_DO;
            else if (title.contains("Em Progresso")) status = KanbanModel.DOING;
            else if (title.contains("Concluído")) status = KanbanModel.DONE;
            
            controller.setColumnInfo(title, status, model.getTasks(), model);
            columnControllers.add(controller);
            
            VBox verticalColumn = (VBox) newColumn;
            verticalColumn.getStyleClass().add("vertical-column");
            verticalColumn.setMaxWidth(Double.MAX_VALUE);
            
            return verticalColumn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Label findLabelInColumn(VBox column) {
        for (Node child : column.getChildren()) {
            if (child instanceof HBox) {
                HBox hbox = (HBox) child;
                for (Node hboxChild : hbox.getChildren()) {
                    if (hboxChild instanceof javafx.scene.control.Label) {
                        return (javafx.scene.control.Label) hboxChild;
                    }
                }
            }
        }
        return null;
    }
    
    @FXML
    private void handleAddTask(ActionEvent event) {
        Task newTask = model.createNewTask();
        
        refreshAllColumns();
    }
    
    public static void refreshAllColumns() {
        Platform.runLater(() -> {
            for (KanbanColumnController controller : columnControllers) {
                controller.refreshTasksView();
            }
        });
    }
    
    public static void setDraggedTask(Task task) {
        draggedTask = task;
    }
    
    public static Task getDraggedTask() {
        return draggedTask;
    }
    
    public static void setDragSource(Node source) {
        dragSource = source;
    }
    
    public static Node getDragSource() {
        return dragSource;
    }
} 