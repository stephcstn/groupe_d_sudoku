package fr.univpoitiers.app.view.grid;

import java.util.Objects;
import java.util.function.BiConsumer;
import javafx.scene.layout.GridPane;

/**
 * Affiche une grille de Sudoku en créant une vue pour chaque cellule.
 */
public class GridView extends GridPane {

    /**
     * Taille d'un bloc standard de Sudoku.
     */
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
     */
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
     */
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
     */
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