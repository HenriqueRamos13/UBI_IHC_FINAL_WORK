package pt.ubi.di;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainFX extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("view/kanban.css").toExternalForm());
        
        stage.setTitle("Kanban Board - UBI 2025");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}