/*
package fr.univpoitiers.app.view.grid;

import java.util.Objects;
import java.util.function.BiConsumer;
import javafx.scene.layout.GridPane;

/**
 * Affiche une grille de Sudoku en créant une vue pour chaque cellule.

public class GridView extends GridPane {

    /**
     * Taille d'un bloc standard de Sudoku.

    private static final int SUBGRID_SIZE = 3;
    private final CellView[][] cells;

    /**
     * Construit une nouvelle grille de Sudoku avec des dimensions spécifiques.
     * Le nombre de notes par cellule est déterminé par la valeur maximale
     * entre la largeur et la hauteur.
     *
     * @param w   largeur de la grille (nombre de colonnes)
     * @param h   hauteur de la grille (nombre de lignes)
     * @param css feuille de style des cellules
     * @throws IllegalArgumentException si la largeur ou la hauteur n'est pas positive

    public GridView(int w, int h, String css) {
        this(w, h, css, (row, column) -> { });
    }

    /**
     * Construit une grille dont les clics sont transmis au gestionnaire fourni.
     *
     * @param w largeur en cellules
     * @param h hauteur en cellules
     * @param css feuille de style des cellules
     * @param onCellSelected gestionnaire recevant la ligne puis la colonne sélectionnées
     * @throws IllegalArgumentException si une dimension n'est pas positive

    public GridView(int w, int h, String css, BiConsumer<Integer, Integer> onCellSelected) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Les dimensions de la grille doivent être positives.");
        }
        Objects.requireNonNull(onCellSelected, "Le gestionnaire de sélection ne peut pas être nul.");

        this.setAlignment(javafx.geometry.Pos.CENTER);
        this.getStylesheets().add(css);
        int max = Math.max(w, h);
        cells = new CellView[h][w];

        for (int row = 0; row < h; row++) {
            for (int column = 0; column < w; column++) {
                CellView cellView = new CellView(max, css);
                cells[row][column] = cellView;
                int selectedRow = row;
                int selectedColumn = column;
                cellView.setOnMouseClicked(event -> {
                    selectCell(selectedRow, selectedColumn);
                    onCellSelected.accept(selectedRow, selectedColumn);
                });
                String borderWidthTop = (row % SUBGRID_SIZE == 0 && row != 0) ? "3px" : "1px";
                String borderWidthLeft = (column % SUBGRID_SIZE == 0 && column != 0) ? "3px" : "1px";
                cellView.setStyle(
                        "-fx-border-width: " + borderWidthTop + " 1px 1px " + borderWidthLeft + ";"
                );
                this.add(cellView, column, row);
            }
        }
    }

    /**
     * Retourne la vue de cellule aux coordonnées indiquées, à partir de zéro.
     *
     * @param row ligne de la cellule
     * @param column colonne de la cellule
     * @return vue de la cellule
     * @throws IllegalArgumentException si les coordonnées sont hors de la grille

    public CellView getCellAt(int row, int column) {
        if (row < 0 || row >= cells.length || column < 0 || column >= cells[row].length) {
            throw new IllegalArgumentException("Les coordonnées ne correspondent pas à une cellule.");
        }
        return cells[row][column];
    }

    private void selectCell(int row, int column) {
        getCellAt(row, column);
        for (int currentRow = 0; currentRow < cells.length; currentRow++) {
            for (int currentColumn = 0; currentColumn < cells[currentRow].length; currentColumn++) {
                boolean selected = currentRow == row && currentColumn == column;
                boolean sharesRowOrColumn = currentRow == row || currentColumn == column;
                boolean sharesBlock = currentRow / SUBGRID_SIZE == row / SUBGRID_SIZE
                        && currentColumn / SUBGRID_SIZE == column / SUBGRID_SIZE;
                CellView cell = cells[currentRow][currentColumn];
                cell.setSelected(selected);
                cell.setHighlighted(!selected && (sharesRowOrColumn || sharesBlock));
            }
        }
    }
}
 */

package fr.univpoitiers.app.view.grid;

import java.util.Objects;
import java.util.function.BiConsumer;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * Affiche une grille de Sudoku en créant une vue pour chaque cellule.
 *
 * <p>La vue gère la sélection d'une cellule au clic et la surbrillance croisée (ligne,
 * colonne et bloc de la cellule sélectionnée). Elle ne dépend pas du modèle : les
 * dimensions des blocs sont dérivées des dimensions de la grille avec la même règle que
 * {@code Grid} ({@code (int) Math.sqrt(dimension)}), ce qui donne des blocs 3x3 en 9x9,
 * 4x4 en 16x16 et 5x5 en 25x25.</p>
 *
 * <p><b>Threading :</b> toutes les méthodes doivent être appelées depuis le thread JavaFX.</p>
 */
public class GridView extends GridPane {

    private static final String THIN_BORDER = "1px";
    private static final String BLOCK_BORDER = "3px";

    private final CellView[][] cells;

    /** Largeur d'un bloc, en cellules. */
    private final int blockWidth;

    /** Hauteur d'un bloc, en cellules. */
    private final int blockHeight;

    /**
     * Construit une nouvelle grille de Sudoku avec des dimensions spécifiques.
     * Le nombre de notes par cellule est déterminé par la valeur maximale
     * entre la largeur et la hauteur.
     *
     * @param w   largeur de la grille (nombre de colonnes)
     * @param h   hauteur de la grille (nombre de lignes)
     * @param css feuille de style des cellules
     * @throws IllegalArgumentException si la largeur ou la hauteur n'est pas positive
     */
    public GridView(int w, int h, String css) {
        this(w, h, css, (row, column) -> { });
    }

    /**
     * Construit une grille dont les clics sont transmis au gestionnaire fourni.
     *
     * @param w              largeur en cellules
     * @param h              hauteur en cellules
     * @param css            feuille de style des cellules
     * @param onCellSelected gestionnaire recevant la ligne puis la colonne sélectionnées
     * @throws IllegalArgumentException si une dimension n'est pas positive
     * @throws NullPointerException     si le gestionnaire ou la feuille de style est nul
     */
    public GridView(int w, int h, String css, BiConsumer<Integer, Integer> onCellSelected) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Les dimensions de la grille doivent être positives.");
        }
        Objects.requireNonNull(onCellSelected, "Le gestionnaire de sélection ne peut pas être nul.");
        Objects.requireNonNull(css, "La feuille de style ne peut pas être nulle.");

        this.blockWidth = (int) Math.sqrt(w);
        this.blockHeight = (int) Math.sqrt(h);
        this.cells = new CellView[h][w];

        setAlignment(Pos.CENTER);
        getStylesheets().add(css);
        buildCells(w, h, css, onCellSelected);
    }

    /**
     * Retourne la vue de cellule aux coordonnées indiquées, à partir de zéro.
     *
     * @param row    ligne de la cellule
     * @param column colonne de la cellule
     * @return vue de la cellule
     * @throws IllegalArgumentException si les coordonnées sont hors de la grille
     */
    public CellView getCellAt(int row, int column) {
        if (row < 0 || row >= cells.length || column < 0 || column >= cells[row].length) {
            throw new IllegalArgumentException("Les coordonnées ne correspondent pas à une cellule.");
        }
        return cells[row][column];
    }

    /**
     * Crée toutes les cellules, leur bordure et leur gestionnaire de clic.
     *
     * @param w              largeur en cellules
     * @param h              hauteur en cellules
     * @param css            feuille de style des cellules
     * @param onCellSelected gestionnaire appelé après la mise à jour visuelle de la sélection
     */
    private void buildCells(int w, int h, String css, BiConsumer<Integer, Integer> onCellSelected) {
        int notesPerCell = Math.max(w, h);
        for (int row = 0; row < h; row++) {
            for (int column = 0; column < w; column++) {
                CellView cellView = new CellView(notesPerCell, css);
                cells[row][column] = cellView;

                int selectedRow = row;
                int selectedColumn = column;
                cellView.setOnMouseClicked(event -> {
                    selectCell(selectedRow, selectedColumn);
                    onCellSelected.accept(selectedRow, selectedColumn);
                });
                cellView.setStyle(borderStyle(row, column));
                add(cellView, column, row);
            }
        }
    }

    /**
     * Calcule le style de bordure d'une cellule : bordure épaisse en haut ou à gauche
     * lorsque la cellule ouvre un bloc (hors bord de la grille), fine sinon.
     *
     * @param row    ligne de la cellule
     * @param column colonne de la cellule
     * @return la déclaration CSS {@code -fx-border-width} de la cellule
     */
    private String borderStyle(int row, int column) {
        String top = (row % blockHeight == 0 && row != 0) ? BLOCK_BORDER : THIN_BORDER;
        String left = (column % blockWidth == 0 && column != 0) ? BLOCK_BORDER : THIN_BORDER;
        return "-fx-border-width: " + top + " 1px 1px " + left + ";";
    }

    /**
     * Met à jour la sélection et la surbrillance croisée autour de la cellule indiquée.
     *
     * @param row    ligne de la cellule sélectionnée
     * @param column colonne de la cellule sélectionnée
     * @throws IllegalArgumentException si les coordonnées sont hors de la grille
     */
    private void selectCell(int row, int column) {
        getCellAt(row, column);
        for (int currentRow = 0; currentRow < cells.length; currentRow++) {
            for (int currentColumn = 0; currentColumn < cells[currentRow].length; currentColumn++) {
                boolean selected = currentRow == row && currentColumn == column;
                boolean related = currentRow == row
                        || currentColumn == column
                        || isSameBlock(currentRow, currentColumn, row, column);
                CellView cell = cells[currentRow][currentColumn];
                cell.setSelected(selected);
                cell.setHighlighted(!selected && related);
            }
        }
    }

    /**
     * Indique si deux cellules appartiennent au même bloc.
     *
     * @param rowA    ligne de la première cellule
     * @param columnA colonne de la première cellule
     * @param rowB    ligne de la seconde cellule
     * @param columnB colonne de la seconde cellule
     * @return vrai si les deux cellules sont dans le même bloc
     */
    private boolean isSameBlock(int rowA, int columnA, int rowB, int columnB) {
        return rowA / blockHeight == rowB / blockHeight
                && columnA / blockWidth == columnB / blockWidth;
    }
}