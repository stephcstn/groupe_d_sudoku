package fr.univpoitiers.core.grid;

import java.util.ArrayList;
import java.util.Collections;

public class Grid {

    private Cell[] cells;
    private final int WIDTH;
    private final int HEIGHT;
    private ArrayList<Integer> availableCells;
    private final int BLOCK_WIDTH;
    private final int BLOCK_HEIGHT;

    /**
     *
     * @param width
     * @param height
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
        int startX = x - (x % this.BLOCK_WIDTH);
        int startY = y - (y % this.BLOCK_HEIGHT);
        return startY * this.WIDTH + startX;
    }

    public int getWidth() {
        return this.WIDTH;
    }

    public int getHeight() {
        return this.HEIGHT;
    }

    /**
     * Vérifie que la valeur peut être placée à cette position :
     * elle ne doit pas déjà être présente sur la ligne, la colonne ou le bloc.
     *
     * @param pos position de la case (index 1D)
     * @param value valeur à placer (de 1 au nombre de valeurs possibles)
     * @return true si la valeur respecte les trois contraintes, false sinon
     *         (y compris si la valeur est hors bornes, par exemple 0 = case vide)
     */
    public boolean checkValidValues(int pos, int value) {
        if (value < 1 || value > Math.max(this.WIDTH, this.HEIGHT)) {
            return false;
        }
        return checkBlock(pos, value) && checkColumn(pos, value) && checkRow(pos, value);
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
        int max = Math.max(this.WIDTH, this.HEIGHT);
        this.cells = new Cell[this.WIDTH * this.HEIGHT];
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
        for (int i = x * this.WIDTH; i < (x + 1) * this.WIDTH; i++) {
            this.cells[i].removeNote(valeur);
        }
    }

    /**
     *
     * @param y
     * @param valeur
     */
    public void columnUpdate(int y, int valeur) {
        for (int i = y; i < this.cells.length; i+=this.WIDTH) {
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

        for (int r = 0; r < this.BLOCK_HEIGHT; r++) {
            for (int c = 0; c < this.BLOCK_WIDTH; c++) {
                int index = startBlock + r * this.WIDTH + c;
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
     * @param x
     * @param y
     * @return the corresponding 1D index
     */
    public int cellToIndex(int x, int y) {
        return y * WIDTH + x;
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
     * Vérifie que la valeur n'est pas déjà présente sur la colonne.
     * @param pos
     * @param value
     *
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
     * Vérifie que la valeur n'est pas déjà présente dans le sous-bloc 3x3.
     * @param pos
     * @param value
     *
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

}