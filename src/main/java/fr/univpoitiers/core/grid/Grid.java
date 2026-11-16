package fr.univpoitiers.core.grid;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents the Sudoku game board, handling cells, board dimensions,
 * constraint verification (rows, columns, sub-blocks), and candidate note updates.
 */
public class Grid {

    private Cell[] cells;
    private final int WIDTH;
    private final int HEIGHT;
    private ArrayList<Integer> availableCells;
    private final int BLOCK_WIDTH;
    private final int BLOCK_HEIGHT;

    /**
     * Constructs a new Grid with the specified dimensions and initializes empty cells and available positions.
     *
     * @param width the width of the grid (number of columns)
     * @param height the height of the grid (number of rows)
     */
    public Grid(int width, int height) {
        int max = Math.max(width, height);
        this.WIDTH = width;
        this.HEIGHT = height;
        this.cells = new Cell[width*height];
        this.availableCells = new ArrayList<>();
        this.BLOCK_WIDTH = (int) Math.sqrt(width);
        this.BLOCK_HEIGHT = (int) Math.sqrt(height);
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(max);
            this.availableCells.add(i); // a modifier
        }
    }

    /**
     * Constructs a new Grid by creating a deep copy of another Grid instance.
     *
     * @param other the Grid instance to copy
     */
    public Grid(Grid other) {
        this.WIDTH = other.WIDTH;
        this.HEIGHT = other.HEIGHT;
        this.cells = new Cell[other.cells.length];
        this.availableCells = new ArrayList<>(other.availableCells);
        this.BLOCK_WIDTH = other.BLOCK_WIDTH;
        this.BLOCK_HEIGHT = other.BLOCK_HEIGHT;
        for (int i = 0; i < other.cells.length; i++) {
            this.cells[i] = new Cell(other.cells[i]);
        }
    }

    /**
     * Calculates the zero-based row index for a given 1D cell position.
     *
     * @param pos the 1D position of the cell
     * @return the row index (from 0 to HEIGHT - 1)
     */
    public int getRowIndex(int pos) {
        return pos / this.WIDTH;
    }

    /**
     * Returns the cell index at the start of the row from a position
     * @param pos 1D position of the cell
     * @return cell index at the start of the row
     */
    public int getStartRowIndex(int pos) {
        return (pos / this.WIDTH) * 9;
    }

    /**
     * Returns the active candidate notes of a cell.
     * @param pos the 1D position of the cell
     * @return the zero-based indices of the active notes; empty if {@code pos} is outside the grid
     */
    public ArrayList<Integer> getNotes(int pos) {
        ArrayList<Integer> res = new ArrayList<>();
        if (0 <= pos && pos < this.cells.length) {
            for (int i = 0; i < WIDTH; i++) {
                if (this.cells[pos].getNote(i) == 1) {
                    res.add(i);
                }
            }
        }
        return res;
    }

    /**
     * Calculates the zero-based column index for a given 1D cell position.
     *
     * @param pos the 1D position of the cell
     * @return the column index (from 0 to WIDTH - 1)
     */
    public int getColumnIndex(int pos) {
        return pos % this.WIDTH;
    }

    /**
     * Calculates the 1D index of the top-left cell of the sub-block containing the given position.
     *
     * @param pos the 1D position of the cell
     * @return the 1D index of the top-left cell of the corresponding sub-block
     */
    public int getBlockIndex(int pos) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);
        int startX = x - (x % this.BLOCK_WIDTH);
        int startY = y - (y % this.BLOCK_HEIGHT);
        return startY * this.WIDTH + startX;
    }

    /**
     * Gets the width of the grid (number of columns).
     *
     * @return the grid width
     */
    public int getWidth() {
        return this.WIDTH;
    }

    /**
     * Gets the height of the grid (number of rows).
     *
     * @return the grid height
     */
    public int getHeight() {
        return this.HEIGHT;
    }

    /**
     * Checks whether a value can be placed at the given position:
     * it must not already be present in the same row, column, or sub-block.
     *
     * @param pos the cell position (1D index)
     * @param value the value to place (from 1 up to the maximum number of possible values)
     * @return true if the value satisfies all three constraints, false otherwise
     *         (including if the value is out of bounds, e.g., 0 for an empty cell)
     */
    public boolean checkValidValues(int pos, int value) {
        if (value < 1 || value > Math.max(this.WIDTH, this.HEIGHT)) {
            return false;
        }
        return checkBlock(pos, value) && checkColumn(pos, value) && checkRow(pos, value);
    }


    /**
     * Places a value in the cell at the given position and updates the list of available cells.
     *
     * @param pos the 1D index of the target cell
     * @param value the numeric value to set
     * @return true if the operation succeeded
     */
    public boolean addNumber(int pos, int value) {
        this.cells[pos].setValue(value);
        this.availableCells.remove(Integer.valueOf(pos));
        return true;
    }

    /**
     * Gets the number (value) stored in the cell at the given 1D position.
     *
     * @param pos the 1D position of the cell
     * @return the value of the cell (0 if empty)
     */
    public int getNumber(int pos) {
        return this.cells[pos].getValue();
    }

    /**
     * Retrieves the index of the first available (empty) cell in the grid.
     *
     * @return the 1D index of the next available cell
     */
    public int nextAvailableCell() {
        return this.availableCells.getFirst();
    }

    /**
     * Inserts a value into a sorted ArrayList using binary search, ensuring no duplicate values are added.
     *
     * @param list the target sorted list of integers
     * @param value the integer value to insert
     */
    private void insertSorted(ArrayList<Integer> list, int value) {
        int index = Collections.binarySearch(list, value);

        // If the element is not already present, binarySearch returns (-insertion_point - 1)
        if (index < 0) {
            int insertIndex = -(index + 1);
            list.add(insertIndex, value);
        }
    }

    /**
     * Restores a cell index to the list of available cells in sorted order.
     *
     * @param pos the 1D index of the cell to make available again
     */
    public void backCellAvailable(int pos) {
        this.insertSorted(this.availableCells, pos);
    }

    /**
     * Checks whether a specific note (candidate value) is active for the cell at the given position.
     *
     * @param pos the 1D position of the cell
     * @param note the note (candidate value) to check
     * @return true if the note is present (set to 1), false otherwise
     */
    public boolean isCellContaining(int pos, int note) {
        return this.cells[pos].getNote(note) == 1;
    }

    /**
     * Removes the number from the cell at the given position (resets to 0) and marks it as available.
     *
     * @param pos the 1D position of the cell to clear
     */
    public void removeNumber(int pos) {
        this.cells[pos].setValue(0);
        backCellAvailable(pos);
    }



    /**
     * Removes a specific candidate note (pencil mark) for the cell at the given position.
     *
     * @param pos the 1D position index of the target cell
     * @param note the zero-based index of the candidate note to remove (ranging from 0 to max values - 1)
     */
    public void removeNote(int pos, int note) {
        this.cells[pos].removeNote(note);
    }

    /**
     * Enables a specific candidate note (pencil mark) for the cell at the given position.
     *
     * @param pos the 1D position index of the target cell
     * @param note the zero-based index of the candidate note to set (ranging from 0 to max values - 1)
     */
	public void setNote(int pos, int note) {
		this.cells[pos].setNote(note);
	}

	/**
	 * Locks every cell that currently holds a value, so that it can no longer be modified.
	 */
	public void lockFilledCells() {
        for (Cell cell : this.cells) {
            if (cell.getValue() != 0) {
                cell.lock();
            }
        }
    }

    /**
     * Clears all player entries from the grid, leaving initial locked clues intact.
     */
    public void clearEntries() {
        for (int i = 0; i < this.cells.length; i++) {
            if (!this.cells[i].isLocked()) {
                this.removeNumber(i);
            }
        }
    }

    /**
     * Resets the entire grid by re-initializing all cells and marking every position as available.
     */
    public void resetGrid() {
        int max = Math.max(this.WIDTH, this.HEIGHT);
        this.cells = new Cell[this.WIDTH * this.HEIGHT];
        this.availableCells.clear();
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(max);
            this.availableCells.add(i);
        }
    }

    /**
     * Removes a candidate note value from all cells on a specified row.
     *
     * @param x the zero-based index of the row to update
     * @param valeur the note value to remove from the row
     */
    public void lineUpdate(int x, int valeur) {
        for (int i = x * this.WIDTH; i < (x + 1) * this.WIDTH; i++) {
            this.cells[i].removeNote(valeur);
        }
    }

    /**
     * Removes a candidate note value from all cells on a specified column.
     *
     * @param y the zero-based index of the column to update
     * @param valeur the note value to remove from the column
     */
    public void columnUpdate(int y, int valeur) {
        for (int i = y; i < this.cells.length; i += this.WIDTH) {
            this.cells[i].removeNote(valeur);
        }
    }

    /**
     * Removes a candidate note value from all cells within the sub-block (BLOCK_WIDTH x BLOCK_HEIGHT) containing the given position.
     *
     * @param pos the 1D position specifying the sub-block
     * @param valeur the note value to remove from the sub-block
     * @throws IllegalArgumentException if pos is out of grid bounds
     */
    public void blockUpdate(int pos, int valeur) {
        if (pos < 0 || pos >= this.cells.length) {
            throw new IllegalArgumentException("pos must be within the grid bounds.");
        }

        int startBlock = getBlockIndex(pos);

        for (int r = 0; r < this.BLOCK_HEIGHT; r++) {
            for (int c = 0; c < this.BLOCK_WIDTH; c++) {
                int index = startBlock + r * this.WIDTH + c;
                this.cells[index].removeNote(valeur);
            }
        }
    }

    /**
     * Removes a candidate note value along the main diagonal or anti-diagonal if the position belongs to them.
     *
     * @param pos the 1D position to check for diagonal membership
     * @param valeur the note value to remove along the diagonal(s)
     * @throws IllegalArgumentException if calculated coordinates are out of grid bounds
     */
    public void diagonalUpdate(int pos, int valeur) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);

        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        // Check if the cell is on the main diagonal
        if (x == y) {
            for (int i = 0; i < Math.min(this.WIDTH, this.HEIGHT); i++) {
                this.cells[i * this.WIDTH + i].removeNote(valeur);
            }
        }

        // Check if the cell is on the anti-diagonal
        if (x + y == this.WIDTH - 1) {
            for (int i = 0; i < Math.min(this.WIDTH, this.HEIGHT); i++) {
                this.cells[i * this.WIDTH + (this.WIDTH - 1 - i)].removeNote(valeur);
            }
        }
    }

    /**
     * Two grids are equal when they have the same dimensions and every cell holds the same
     * value and the same candidate notes (lock status is ignored).
     *
     * @param obj the object to compare with
     * @return true if {@code obj} is an equal grid
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        Grid otherGrid = (Grid) obj;

        if (this.WIDTH != otherGrid.WIDTH || this.HEIGHT != otherGrid.HEIGHT) return false;

        for (int i = 0; i < this.cells.length; i++) {
            if (this.cells[i].getValue() != otherGrid.cells[i].getValue()) {
                return false;
            }
            int max = Math.max(this.WIDTH, this.HEIGHT);
            for (int j = 0; j < max; j++) {
                if (this.cells[i].getNote(j) != otherGrid.cells[i].getNote(j)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Computes a hash code from the cells and the dimensions of the grid.
     *
     * @return the hash code of this grid
     */
    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(this.cells);
        result = 31 * result + Integer.hashCode(this.WIDTH);
        result = 31 * result + Integer.hashCode(this.HEIGHT);
        return result;
    }

    /**
     * Converts 2D coordinates (x, y) into a 1D array index.
     *
     * @param x the column, starting at 0
     * @param y the row, starting at 0
     * @return the corresponding 1D index
     * @throws IllegalArgumentException if the coordinates are outside the grid
     */
    public int cellToIndex(int x, int y) {
        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("Coordonnées hors de la grille : (" + x + ", " + y + ").");
        }
        return y * WIDTH + x;
    }

    /**
     * Checks if the value is not already present in the row.
     *
     * @param pos the cell 1D position
     * @param value the candidate value to test
     * @return true if the value can be placed in the row, false otherwise
     */
    public boolean checkRow(int pos, int value) {
        int y = getRowIndex(pos);
        for (int c = 0; c < this.WIDTH; c++) {
            int index = cellToIndex(c, y);

            if (index != pos) {
                Cell cell = cells[index];
                if (cell != null && cell.getValue() == value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the value is not already present in the column.
     *
     * @param pos the cell 1D position
     * @param value the candidate value to test
     * @return true if the value can be placed in the column, false otherwise
     */
    public boolean checkColumn(int pos, int value) {
        int x = getColumnIndex(pos);
        for (int r = 0; r < this.HEIGHT; r++) {
            int index = cellToIndex(x, r);

            if (index != pos) {
                Cell cell = cells[index];
                if (cell != null && cell.getValue() == value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the value is not already present in the sub-block (BLOCK_WIDTH x BLOCK_HEIGHT) of the cell.
     *
     * @param pos the cell 1D position
     * @param value the candidate value to test
     * @return true if the value can be placed in the sub-block, false otherwise
     */
    public boolean checkBlock(int pos, int value) {
        int startBlock = getBlockIndex(pos);

        for (int r = 0; r < this.BLOCK_HEIGHT; r++) {
            for (int c = 0; c < this.BLOCK_WIDTH; c++) {
                int index = startBlock + r * this.WIDTH + c;

                if (index != pos) {
                    Cell cell = cells[index];
                    if (cell != null && cell.getValue() == value) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the width of a sub-block.
     *
     * @return the number of columns in a sub-block
     */
    public int getBlockWidth() { return this.BLOCK_WIDTH; }

    /**
     * Returns the height of a sub-block.
     *
     * @return the number of rows in a sub-block
     */
    public int getBlockHeight() { return this.BLOCK_HEIGHT; }

    /**
     * Checks whether the cell at the given position is locked.
     *
     * @param pos the 1D position of the cell
     * @return true if the cell is locked
     */
    public boolean isLocked(int pos) {
        return this.cells[pos].isLocked();
    }

    /**
     * Unlocks every cell of the grid, making all values editable again.
     */
    public void unlockGrid() {
        for (Cell cell : this.cells) {
            cell.unlock();
        }
    }

    /**
     * Removes every candidate note of the cell at the given position.
     *
     * @param pos the 1D position of the cell
     */
    public void clearNotes(int pos) {
        int max = Math.max(this.WIDTH, this.HEIGHT);
        for (int i = 0; i < max; i++) {
            this.cells[pos].removeNote(i);
        }
    }

    /**
     * Prints the current state of the grid to the standard output
     * ('.' for an empty cell), followed by the notes of each cell that has some.
     */
    public void printGrid() {
        int max = Math.max(this.WIDTH, this.HEIGHT);
        int digits = String.valueOf(max).length();
        StringBuilder notes = new StringBuilder();

        StringBuilder blockLine = new StringBuilder();
        for (int x = 0; x < this.WIDTH; x++) {
            if (x > 0 && x % this.BLOCK_WIDTH == 0) {
                blockLine.append("+-");
            }
            blockLine.append("-".repeat(digits + 1));
        }
        String frame = "=".repeat(blockLine.length());

        System.out.printf("%n%s%n", frame);
        System.out.printf("ÉTAT DE LA GRILLE%n");
        System.out.printf("%s%n", frame);

        for (int y = 0; y < this.HEIGHT; y++) {
            if (y > 0 && y % this.BLOCK_HEIGHT == 0) {
                System.out.printf("%s%n", blockLine);
            }
            for (int x = 0; x < this.WIDTH; x++) {
                if (x > 0 && x % this.BLOCK_WIDTH == 0) {
                    System.out.printf("| ");
                }
                Cell cell = this.cells[cellToIndex(x, y)];
                int value = cell.getValue();
                System.out.printf("%" + digits + "s ", value == 0 ? "." : String.valueOf(value));

                if (cell.count() > 0) {
                    notes.append(String.format("  (%d, %d) :", x, y));
                    for (int i = 0; i < max; i++) {
                        if (cell.getNote(i) == 1) {
                            notes.append(' ').append(i + 1);
                        }
                    }
                    notes.append(String.format("%n"));
                }
            }
            System.out.printf("%n");
        }
        if (!notes.isEmpty()) {
            System.out.printf("%s%n", blockLine);
            System.out.printf("Notes :%n%s", notes);
        }
        System.out.printf("%s%n%n", frame);
    }

}