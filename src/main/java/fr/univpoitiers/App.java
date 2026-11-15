package fr.univpoitiers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application JavaFX : charge la vue principale et ouvre la fenêtre.
 */
public class App extends Application {

    /**
     * Charge la vue {@code views/vue.fxml} et l'affiche dans une fenêtre maximisée non redimensionnable.
     *
     * @param primaryStage fenêtre principale fournie par JavaFX
     * @throws Exception si la vue ne peut pas être chargée
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
     * Lance l'application JavaFX.
     *
     * @param args arguments de la ligne de commande, transmis à JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}