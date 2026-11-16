package fr.univpoitiers.app;

import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.solver.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Application model: links the user interface to the core grid
 * ({@link Grid}), addressing cells by their coordinates (x = column, y = row).
 */
public class SudokuCoreModel {

    private Solver solver;
    private Grid currentGrid;
    private boolean gridLocked;

    /**
     * Generates a new grid to solve. Not implemented yet.
     *
     * @throws UnsupportedOperationException always, until the generator is available
     */
    public void generateGrid() {
        // TODO : Dépendra de l'implémentation de Solver / Generator
        throw new UnsupportedOperationException();
    }

    /**
     * Creates and initializes a new grid with the given dimensions.
     *
     * @param width  width of the grid
     * @param height height of the grid
     */
    public void createGrid(int width, int height) {
        currentGrid = new Grid(width, height);
        gridLocked = false;
    }

    /**
     * Enters a value in the cell at coordinates {@code (x, y)}.
     *
     * @param value the value to enter (1 to 9), or 0 to empty the cell
     * @param x     column of the cell, starting at 0
     * @param y     row of the cell, starting at 0
     * @return true if the input was accepted
     * @throws IllegalArgumentException if the coordinates are outside the grid
     */
    public boolean input(int value, int x, int y) {
        Grid grid = requireGrid();
        int pos = grid.cellToIndex(x, y);

        if (value == 0) {
            grid.removeNumber(pos);
            return true;
        }

        return grid.addNumber(pos, value);
    }

    /**
     * Checks the filled cells and returns the 1D indices of the invalid / conflicting cells.
     *
     * @return an array of the 1D indices of the cells containing an error
     */
    public int[] checkValidValues() {
        Grid grid = requireGrid();
        int totalCells = grid.getWidth() * grid.getHeight();
        List<Integer> invalidPositions = new ArrayList<>();

        for (int pos = 0; pos < totalCells; pos++) {
            int val = grid.getNumber(pos);
            // checkValidValues vérifie la cohérence ligne, colonne, bloc
            if (val != 0 && !grid.checkValidValues(pos, val)) {
                invalidPositions.add(pos);
            }
        }
        return invalidPositions.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Solves the current grid using the given rules.
     *
     * @param rules identifiers of the rules to apply
     * @return true if a solution was found
     */
    public boolean solve(int[] rules) {
        // TODO : à brancher sur le Solver une fois disponible
        throw new UnsupportedOperationException();
    }

    /**
     * Completely resets the grid (every cell becomes empty again).
     */
    public void resetGrid() {
        requireGrid().resetGrid();
        gridLocked = false;
    }

    /**
     * Reads the value of the cell at coordinates {@code (x, y)}.
     *
     * @param x column of the cell, starting at 0
     * @param y row of the cell, starting at 0
     * @return the value, or 0 if the cell is empty
     * @throws IllegalArgumentException if the coordinates are outside the grid
     */
    public int getNumber(int x, int y) {
        Grid grid = requireGrid();
        return grid.getNumber(grid.cellToIndex(x, y));
    }

    /**
     * Locks every non-empty cell (to mark the initial clues of the grid).
     */
    public void lockFilledCells() {
        requireGrid().lockFilledCells();
        gridLocked = true;
    }

    /**
     * Unlocks the grid.
     */
    public void unlockGrid() {
        requireGrid().unlockGrid();
        gridLocked = false;
    }

    /**
     * Tells whether the grid is locked.
     *
     * @return true if the filled cells have been locked
     */
    public boolean isGridLocked() {
        return gridLocked;
    }

    /**
     * Clears only the digits entered by the user, keeping the locked cells.
     */
    public void clearGrid() {
        requireGrid().clearEntries();
    }

    // -------------------------------------------------------------------------
    // Méthodes pour relier la gestion des notes (candidats 1 à 9)
    // -------------------------------------------------------------------------

    /**
     * Checks whether the cell (x, y) contains a given candidate note.
     *
     * @param noteValue index of the note, starting at 0 (unlike {@link #getNote})
     * @param x         column of the cell, starting at 0
     * @param y         row of the cell, starting at 0
     * @return true if the note is active
     */
    public boolean isCellContainingNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        return grid.isCellContaining(grid.cellToIndex(x, y), noteValue);
    }

    /**
     * Returns the current grid.
     *
     * @return the current grid
     * @throws IllegalStateException if no grid has been created
     */
    private Grid requireGrid() {
        if (currentGrid == null) {
            throw new IllegalStateException("La grille doit être créée avant d'être utilisée.");
        }
        return currentGrid;
    }

    /**
     * Tells whether the cell (x, y) is locked.
     *
     * @param x column of the cell, starting at 0
     * @param y row of the cell, starting at 0
     * @return true if the cell is locked
     */
    public boolean isCellLocked(int x, int y) {
        Grid grid = requireGrid();
        return grid.isLocked(grid.cellToIndex(x, y));
    }

    /**
     * Tells whether the cell (x, y) contains the note of a digit.
     *
     * @param noteValue digit of the note, starting at 1
     * @param x         column of the cell, starting at 0
     * @param y         row of the cell, starting at 0
     * @return true if the note is active
     */
    public boolean getNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        return grid.isCellContaining(grid.cellToIndex(x, y), noteValue - 1);
    }

    /**
     * Adds the note of a digit to the cell (x, y), or removes it if it is already there.
     *
     * @param noteValue digit of the note, starting at 1
     * @param x         column of the cell, starting at 0
     * @param y         row of the cell, starting at 0
     */
    public void toggleNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        int pos = grid.cellToIndex(x, y);
        int index = noteValue - 1;
        if (grid.isCellContaining(pos, index)) {
            grid.removeNote(pos, index);
        } else {
            grid.setNote(pos, index);
        }
    }

    /**
     * Removes every note of the cell (x, y), without changing its value.
     *
     * @param x column of the cell, starting at 0
     * @param y row of the cell, starting at 0
     */
    public void clearCellNotes(int x, int y) {
        Grid grid = requireGrid();
        grid.clearNotes(grid.cellToIndex(x, y));
    }

    public int[] getRowAndColumn(int pos) {
        int row = currentGrid.getRowIndex(pos);
        int column = currentGrid.getColumnIndex(pos);
        return new int[]{row, column};
    }

    public boolean isGridComplete() {
        Grid grid = requireGrid();
        int totalCells = grid.getWidth() * grid.getHeight();
        for (int pos = 0; pos < totalCells; pos++) {
            if (grid.getNumber(pos) == 0) {
                return false;
            }
        }
        return true;
    }


}