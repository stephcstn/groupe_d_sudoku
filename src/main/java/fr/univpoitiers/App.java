package fr.univpoitiers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univpoitiers/views/vue.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Sudoku Grid");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        /*
        double racineCarree1 = Math.sqrt(25);
        int racineCarree2 = (int) Math.sqrt(25);
        if ( racineCarree1 == (double) racineCarree2){
            System.out.println("==");

        }else{
            System.out.println("=!!!=");

        }

         */

    }

    public static void main(String[] args) {
        launch(args);
    }
}