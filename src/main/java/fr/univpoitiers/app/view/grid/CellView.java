package fr.univpoitiers.app.view.grid;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Représente la vue d'une cellule dans la grille du jeu de Sudoku.
 * Une cellule peut contenir une grille secondaire (pour les notes ou hypothèses)
 * ainsi qu'une valeur finale affichée par-dessus.
 *
 * <p>Cette classe hérite de {@link StackPane} pour superposer la grille de notes
 * et le panneau de la valeur finale.</p>
 *
 * @author Votre Nom / Groupe D
 * @version 1.0
 */
public class CellView extends StackPane {

    /** Grille contenant les petites notes ou candidats de la cellule. */
    private GridPane gridNotes;

    /** Panneau conteneur pour afficher la valeur finale de la cellule. */
    private Pane finalPane;

    /** Label affichant la valeur finale principale de la cellule. */
    private Label finalValue;
    private String cssPath;

    private boolean isSelected;
    private int posX;
    private int posY;

    /**
     * Construit une nouvelle vue de cellule en fonction du nombre de notes possibles.
     * Détermine dynamiquement la disposition en grille (racine carrée arrondie au supérieur)
     * pour répartir les étiquettes de notes.
     *
     * @param howManyNotes Le nombre total de notes/candidats à intégrer dans la cellule
     *                     (par exemple, 9 pour une grille de Sudoku standard 9x9).
     */
    public CellView(int howManyNotes, String css, int xP, int yP) {
        isSelected = false;
        posX=xP;
        posY=yP;
        this.getStylesheets().add(css);

        double x_double = Math.sqrt(howManyNotes);
        int x_int = (int) x_double;
        int y_int = x_int;

        // Si le nombre de notes n'est pas un carré parfait (ex: 9 -> 3*3)
        if (x_double != (double) x_int) {
            x_int += 1;
        }

        gridNotes = new GridPane();

        // Création et positionnement de chaque label de note dans la grille
        for (int y = 0; y < y_int; y++) {
            for (int x = 0; x < x_int; x++) {
                Label labelNote = new Label("0");
                gridNotes.add(labelNote, x, y);
            }
        }

        // Ajout de la grille de notes dans le conteneur principal
        this.getChildren().add(gridNotes);

        finalValue = new Label("*");

        // Initialisation et ajout du panneau de la valeur finale au StackPane
        finalPane = new Pane();
        finalPane.getChildren().add(finalValue);
        this.getChildren().add(finalPane);

        finalPane.getStylesheets().add(css);
        finalPane.getStyleClass().add("cell-view");
        //finalPane.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");



        this.getStyleClass().add("cell-view");
        // Exemple dans CellView.java
        this.setOnMouseEntered(event -> {
            // Action exécutée lorsque la souris survole la cellule
            //this.setStyle("-fx-background-color: #3799a6;");
        });


        // Définir une taille fixe ou préférée (ex: 50x50 pixels)
        this.setPrefSize(50, 50);
        this.setMinSize(50, 50);
        this.setMaxSize(50, 50);

        // --------------------------EVENTS-----------------------------
        this.setOnMouseExited(event -> {
            // Action exécutée lorsque la souris quitte la cellule
            //this.getStyleClass().add("cell-view");
        });

        this.setOnMouseClicked(event -> {
            if (isSelected){
                isSelected=false;
                finalPane.setOpacity(1);

            }else{
                isSelected=true;
                finalPane.setOpacity(0.3);


            }
        });
    }
}