package fr.univpoitiers.app.view.grid;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * Représente la grille principale du jeu de Sudoku.
 * Cette classe hérite de {@link GridPane} et initialise dynamiquement
 * un ensemble de cellules ({@link CellView}) en fonction des dimensions
 * de la grille (largeur et hauteur), tout en gérant l'interactivité
 * (sélection et surbrillance des lignes, colonnes et blocs).
 *
 * @author Groupe D
 * @version 1.1
 */
public class GridView extends GridPane {

    /** Largeur de la grille (nombre de colonnes). */
    private int width;

    /** Hauteur de la grille (nombre de lignes). */
    private int height;

    /** Tableau bidimensionnel stockant toutes les cellules de la grille. */
    private CellView[][] cells;

    /** Référence vers la cellule actuellement sélectionnée. */
    private CellView currentSelectedCell;

    /** Indique si les nombres saisis modifient les notes plutôt que la valeur. */
    private boolean notesMode;

    /**
     * Construit une nouvelle grille de Sudoku avec des dimensions spécifiques.
     *
     * @param w La largeur de la grille (nombre de colonnes).
     * @param h La hauteur de la grille (nombre de lignes).
     * @param css Le chemin vers la feuille de style CSS.
     */
    public GridView(int w, int h, String css) {
        this.setAlignment(Pos.CENTER);
        this.getStylesheets().add(css);
        this.width = w;
        this.height = h;
        this.cells = new CellView[w][h];

        // La valeur max entre la longueur et la largeur détermine le nombre de notes pour chaque cellule
        int max = Math.max(w, h);
        int boxSize = (int) Math.sqrt(max); // Taille d'un sous-bloc (ex: 3 pour 9x9)

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellView cellView = new CellView(max, css, x, y);
                cells[x][y] = cellView;

                // Enregistre l'action de clic pour gérer la sélection et la surbrillance croisée
                cellView.setOnClickAction(this::handleCellClick);

                // Construction d'une bordure personnalisée selon la position (x, y)
                String borderWidthTop = (y % boxSize == 0 && y != 0) ? "3px" : "0.5px";
                String borderWidthLeft = (x % boxSize == 0 && x != 0) ? "3px" : "0.5px";

                cellView.setStyle(
                        "-fx-border-width: " + borderWidthTop + " 0.5px 0.5px " + borderWidthLeft + ";"
                );

                this.add(cellView, x, y);
            }
        }
    }

    /**
     * Gère la sélection d'une cellule et met à jour la surbrillance
     * de sa ligne, de sa colonne et de son bloc.
     *
     * @param clickedCell La cellule qui vient d'être cliquée.
     */
    private void handleCellClick(CellView clickedCell) {
        int max = Math.max(width, height);
        int boxSize = (int) Math.sqrt(max);

        // 1. Réinitialiser tous les styles de sélection et surbrillance
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[x][y].setSelectedStyle(false);
                cells[x][y].setHighlightedStyle(false);
            }
        }

        // 2. Sélectionner la nouvelle cellule
        currentSelectedCell = clickedCell;
        clickedCell.setSelectedStyle(true);

        int cx = clickedCell.getPosX();
        int cy = clickedCell.getPosY();

        int blockRowStart = (cy / boxSize) * boxSize;
        int blockColStart = (cx / boxSize) * boxSize;

        // 3. Appliquer la surbrillance sur la même ligne, même colonne et même bloc
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellView target = cells[x][y];
                if (target == clickedCell) continue;

                boolean sameRow = (y == cy);
                boolean sameCol = (x == cx);
                boolean sameBlock = (x >= blockColStart && x < blockColStart + boxSize &&
                        y >= blockRowStart && y < blockRowStart + boxSize);

                if (sameRow || sameCol || sameBlock) {
                    target.setHighlightedStyle(true);
                }
            }
        }
    }

    /**
     * Active ou désactive la saisie de notes pour les prochains nombres.
     *
     * @param notesMode {@code true} pour saisir des notes, {@code false} pour saisir une valeur.
     */
    public void setNotesMode(boolean notesMode) {
        this.notesMode = notesMode;
    }

    /**
     * Saisit un nombre dans la cellule sélectionnée. En mode notes, ajoute ou
     * retire la note correspondante ; sinon, définit la valeur de la cellule.
     * Les cellules verrouillées ne sont pas modifiées ; les notes ne peuvent
     * être modifiées que si la cellule est vide.
     * Ne fait rien si aucune cellule n'est sélectionnée.
     *
     * @param value Nombre à saisir, compris entre 1 et la plus grande dimension de la grille.
     */
    public void inputNumber(int value) {
        if (currentSelectedCell == null) {
            return;
        }
        if (notesMode) {
            currentSelectedCell.toggleNote(value);
        } else {
            currentSelectedCell.setCellValue(value);
        }
    }

    /**
     * Efface la valeur et les notes de la cellule sélectionnée, sauf si elle
     * est verrouillée. Ne fait rien si aucune cellule n'est sélectionnée.
     */
    public void clearSelectedCell() {
        if (currentSelectedCell != null) {
            currentSelectedCell.clearCell();
        }
    }

    /**
     * Réinitialise les valeurs et les notes de toutes les cellules et les déverrouille.
     * La sélection et le mode de saisie restent inchangés.
     */
    public void resetGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[x][y].resetCell();
            }
        }
    }
}