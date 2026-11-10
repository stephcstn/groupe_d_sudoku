package fr.univpoitiers.core.grid;

import java.util.ArrayList;
import java.util.Collections;

public class Grid {

    private Cell[] cells;
    private final int width;
    private final int height;
    private ArrayList<Integer> availableCells;
    private final int blockWidth;
    private final int blockHeight;

    /**
     *
     * @param width
     * @param height
     */
    public Grid(int width, int height) {
        int max = Math.max(width, height);
        this.width = width;
        this.height = height;
        this.cells = new Cell[width*height];
        this.availableCells = new ArrayList<>();
        this.blockWidth = (int) Math.sqrt(width);
        this.blockHeight = (int) Math.sqrt(height);
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(max);
            this.availableCells.add(i); // a modifier
        }
    }

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

    public int getRowIndex(int pos) {
        return pos / this.WIDTH;
    }

    public int getColumnIndex(int pos) {
        return pos % this.WIDTH;
    }

    public int getBlockIndex(int pos) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);
        int startX = x - (x % this.blockWidth);
        int startY = y - (y % this.blockHeight);
        return startY * this.width + startX;
    }

    public int getWidth() {
        return this.WIDTH;
    }

    public int getHeight() {
        return this.HEIGHT;
    }

    /**
     *
     * @param pos
     * @param value
     *
     */
    public boolean checkValidValues(int pos, int value) {
        return checkBlock(pos,value) && checkColumn(pos,value) && checkRow(pos, value);
    }

    /**
     *
     * @param pos
     * @param value
     */
    public boolean addNumber(int pos, int value) {
        this.cells[pos].setValue(value);
        this.availableCells.remove(Integer.valueOf(pos));
        return true;
    }

    /**
     *
     * @param pos
     */
    public int getNumber(int pos) {
        return this.cells[pos].getValue();
    }

    public int nextAvailableCell() {
        return this.availableCells.getFirst();
    }

    private void insertSorted(ArrayList<Integer> list, int value) {
        int index = Collections.binarySearch(list, value);

        // Si l'élément n'est pas déjà présent, binarySearch renvoie (-point_insertion - 1)
        if (index < 0) {
            int insertIndex = -(index + 1);
            list.add(insertIndex, value);
        }
    }

    /**
     *
     * @param pos
     */
    public void backCellAvailable(int pos) {
        this.insertSorted(this.availableCells, pos);
    }

    /**
     *
     * @param pos
     * @param note
     */
    public boolean isCellContaining(int pos, int note) {
        return this.cells[pos].getNote(note) == 1;
    }

    /**
     *
     * @param pos
     */
    public void removeNumber(int pos) {
        this.cells[pos].setValue(0);
        backCellAvailable(pos);
    }

    public void lockFilledCells() {
        for (Cell cell : this.cells) {
            if (cell.getValue() != 0) {
                cell.lock();
            }
        }
    }

    public void clearEntries() {
        for (int i = 0; i < this.cells.length; i++) {
            if (!this.cells[i].isLocked()) {
                this.removeNumber(i);
            }
        }
    }

    public void resetGrid() {
        int max = Math.max(this.width, this.height);
        this.cells = new Cell[this.width * this.height];
        this.availableCells.clear();
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(max);
            this.availableCells.add(i);
        }
    }

    /**
     *
     * @param x
     * @param valeur
     */
    public void lineUpdate(int x, int valeur) {
        for (int i = x * this.width; i < (x + 1) * this.width; i++) {
            this.cells[i].removeNote(valeur);
        }
    }

    /**
     *
     * @param y
     * @param valeur
     */
    public void columnUpdate(int y, int valeur) {
        for (int i = y; i < this.cells.length; i+=this.width) {
            this.cells[i].removeNote(valeur);
        }
    }

    /**
     *
     * @param pos
     * @param valeur
     */
    public void blockUpdate(int pos, int valeur) {
        if (pos < 0 || pos >= this.cells.length) {
            throw new IllegalArgumentException("pos must be within the grid bounds.");
        }

        int startBlock = getBlockIndex(pos);

        for (int r = 0; r < this.blockHeight; r++) {
            for (int c = 0; c < this.blockWidth; c++) {
                int index = startBlock + r * this.width + c;
                this.cells[index].removeNote(valeur);
            }
        }
    }

    /**
     *
     * @param pos
     * @param valeur
     */
    public void diagonalUpdate(int pos, int valeur) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);

        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        // Check if the cell is on the main diagonal
        if (x == y) {
            for (int i = 0; i < Math.min(this.width, this.height); i++) {
                this.cells[i * this.width + i].removeNote(valeur);
            }
        }

        // Check if the cell is on the anti-diagonal
        if (x + y == this.width - 1) {
            for (int i = 0; i < Math.min(this.width, this.height); i++) {
                this.cells[i * this.width + (this.width - 1 - i)].removeNote(valeur);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;

        Grid otherGrid = (Grid) obj;

        if (this.WIDTH != otherGrid.WIDTH || this.HEIGHT != otherGrid.HEIGHT) return false;

        for (int i = 0; i < this.cells.length; i++) {
            if (this.cells[i].getValue() != otherGrid.cells[i].getValue()) {
                return false;
            }
            int max = Math.max(this.width, this.height);
            for (int j = 0; j < max; j++) {
                if (this.cells[i].getNote(j) != otherGrid.cells[i].getNote(j)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(this.cells);
        result = 31 * result + Integer.hashCode(this.width);
        result = 31 * result + Integer.hashCode(this.height);
        return result;
    }

    /**
     * Converts 2D coordinates (x, y) into a 1D array index.
     *
     * @param x
     * @param y
     * @return the corresponding 1D index
     */
    public int cellToIndex(int x, int y) {
        return y * width + x;
    }

    /**
     * Vérifie que la valeur n'est pas déjà présente sur la ligne.
     *
     * @param pos
     * @param value
     *
     */
    public boolean checkRow(int pos, int value) {
        int y = getRowIndex(pos);
        for (int c = 0; c < this.width; c++) {
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
     * Vérifie que la valeur n'est pas déjà présente sur la colonne.
     * @param pos
     * @param value
     *
     */
    public boolean checkColumn(int pos, int value) {
        int x = getColumnIndex(pos);
        for (int r = 0; r < this.height; r++) {
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
     * Vérifie que la valeur n'est pas déjà présente dans le sous-bloc 3x3.
     * @param pos
     * @param value
     *
     */
    public boolean checkBlock(int pos, int value) {
        int startBlock = getBlockIndex(pos);

        for (int r = 0; r < this.blockHeight; r++) {
            for (int c = 0; c < this.blockWidth; c++) {
                int index = startBlock + r * this.width + c;

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

}