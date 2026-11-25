package fr.univpoitiers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point of the JavaFX application: loads the main view and opens the window.
 */
public class App extends Application {

    /**
     * Loads the {@code views/vue.fxml} view and shows it in a maximized, non-resizable window.
     *
     * @param primaryStage the main window provided by JavaFX
     * @throws Exception if the view cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("views/vue.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Sudoku Grid");
        primaryStage.setScene(new Scene(root));

        //Désactive le redimensionnement manuel
        primaryStage.setResizable(false);

        // Fenetre de la taille maximum possible
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments, passed on to JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}