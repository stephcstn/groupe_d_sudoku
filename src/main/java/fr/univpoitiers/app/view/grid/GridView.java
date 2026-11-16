
package fr.univpoitiers.app.view.grid;

import java.util.Objects;
import java.util.function.BiConsumer;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * Displays a Sudoku grid by creating a view for each cell.
 *
 * <p>The view handles the selection of a cell on click and the cross highlight (row,
 * column and block of the selected cell). It does not depend on the model: the
 * block dimensions are derived from the grid dimensions with the same rule as
 * {@code Grid} ({@code (int) Math.sqrt(dimension)}), which gives 3x3 blocks for 9x9,
 * 4x4 for 16x16 and 5x5 for 25x25.</p>
 *
 * <p><b>Threading:</b> every method must be called from the JavaFX thread.</p>
 */
public class GridView extends GridPane {

    private static final String THIN_BORDER = "1px";
    private static final String BLOCK_BORDER = "3px";

    private final CellView[][] cells;

    /** Width of a block, in cells. */
    private final int blockWidth;

    /** Height of a block, in cells. */
    private final int blockHeight;

    /**
     * Builds a new Sudoku grid with the given dimensions.
     * The number of notes per cell is the maximum
     * of the width and the height.
     *
     * @param w   width of the grid (number of columns)
     * @param h   height of the grid (number of rows)
     * @param css style sheet of the cells
     * @throws IllegalArgumentException if the width or the height is not positive
     */
    public GridView(int w, int h, String css) {
        this(w, h, css, (row, column) -> { });
    }

    /**
     * Builds a grid whose clicks are passed on to the given handler.
     *
     * @param w              width in cells
     * @param h              height in cells
     * @param css            style sheet of the cells
     * @param onCellSelected handler receiving the selected row, then the selected column
     * @throws IllegalArgumentException if a dimension is not positive
     * @throws NullPointerException     if the handler or the style sheet is null
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
     * Returns the cell view at the given coordinates, starting at zero.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @return the cell view
     * @throws IllegalArgumentException if the coordinates are outside the grid
     */
    public CellView getCellAt(int row, int column) {
        if (row < 0 || row >= cells.length || column < 0 || column >= cells[row].length) {
            throw new IllegalArgumentException("Les coordonnées ne correspondent pas à une cellule.");
        }
        return cells[row][column];
    }

    /**
     * Creates every cell, its border and its click handler.
     *
     * @param w              width in cells
     * @param h              height in cells
     * @param css            style sheet of the cells
     * @param onCellSelected handler called after the selection has been updated visually
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
     * Computes the border style of a cell: thick border at the top or on the left
     * when the cell starts a block (except on the grid edge), thin otherwise.
     *
     * @param row    row of the cell
     * @param column column of the cell
     * @return the CSS {@code -fx-border-width} declaration of the cell
     */
    private String borderStyle(int row, int column) {
        String top = (row % blockHeight == 0 && row != 0) ? BLOCK_BORDER : THIN_BORDER;
        String left = (column % blockWidth == 0 && column != 0) ? BLOCK_BORDER : THIN_BORDER;
        return "-fx-border-width: " + top + " 1px 1px " + left + ";";
    }

    /**
     * Updates the selection and the cross highlight around the given cell.
     *
     * @param row    row of the selected cell
     * @param column column of the selected cell
     * @throws IllegalArgumentException if the coordinates are outside the grid
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
     * Tells whether two cells belong to the same block.
     *
     * @param rowA    row of the first cell
     * @param columnA column of the first cell
     * @param rowB    row of the second cell
     * @param columnB column of the second cell
     * @return true if both cells are in the same block
     */
    private boolean isSameBlock(int rowA, int columnA, int rowB, int columnB) {
        return rowA / blockHeight == rowB / blockHeight
                && columnA / blockWidth == columnB / blockWidth;
    }
}