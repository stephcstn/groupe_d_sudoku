package fr.univpoitiers.app.view.grid;

import javafx.scene.layout.GridPane;

import java.awt.event.MouseListener;

/**
 * Représente la grille principale du jeu de Sudoku.
 * Cette classe hérite de {@link GridPane} et initialise dynamiquement
 * un ensemble de cellules ({@link CellView}) en fonction des dimensions
 * de la grille (largeur et hauteur).
 *
 * @author Votre Nom / Groupe D
 * @version 1.0
 */
public class GridView extends GridPane {

    /** Largeur de la grille (nombre de colonnes). */
    private int WIDTH;

    /** Hauteur de la grille (nombre de lignes). */
    private int HEIGHT;


    /**
     * Construit une nouvelle grille de Sudoku avec des dimensions spécifiques.
     * Le nombre de notes par cellule est déterminé par la valeur maximale
     * entre la largeur et la hauteur.
     *
     * @param w La largeur de la grille (nombre de colonnes).
     * @param h La hauteur de la grille (nombre de lignes).
     */
    public GridView(int w, int h,String css) {
        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.getStylesheets().add(css);
        WIDTH = w;
        HEIGHT = h;



        // La valeur max entre la longueur et la largeur détermine le nombre de notes pour chaque cellule
        int max = Math.max(w, h);

        for (int y = 0; y < WIDTH; y++) {
            for (int x = 0; x < HEIGHT; x++) {
                CellView cellView = new CellView(max,css,x,y);
                // Construction d'une bordure personnalisée selon la position (x, y)
                String borderWidthTop = (y % 3 == 0 && y != 0) ? "3px" : "1px";
                String borderWidthLeft = (x % 3 == 0 && x != 0) ? "3px" : "1px";

                cellView.setStyle(
                        "-fx-border-color: black;" +
                                "-fx-border-width: " + borderWidthTop + " 1px 1px " + borderWidthLeft + ";"
                );


                this.add(cellView, x, y);

            }
        }
    }
}