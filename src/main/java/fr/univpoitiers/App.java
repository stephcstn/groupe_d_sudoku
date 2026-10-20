package fr.univpoitiers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univpoitiers/views/vue.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Sudoku Grid");
        primaryStage.setScene(new Scene(root));

        //Désactive le redimensionnement manuel
        primaryStage.setResizable(false);

        // Fenetre de la taille maximum possible
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}