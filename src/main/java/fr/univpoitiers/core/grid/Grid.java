package fr.univpoitiers.core.grid;

import java.util.ArrayList;

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
		this.WIDTH = width;
		this.HEIGHT = height;
		this.cells = new Cell[width*height];
		this.availableCells = new ArrayList<>();
		this.BLOCK_WIDTH = (int) Math.sqrt(width);
		this.BLOCK_HEIGHT = (int) Math.sqrt(height);
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell();
            this.availableCells.add(i);
        }
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
	 * @param x
	 * @param y
	 * @param value
	 */
	public boolean addNumber(int x, int y, int value) {
        this.cells[y * this.WIDTH + x].setValue(value);
        this.availableCells.remove(Integer.valueOf(y * this.WIDTH + x));
        return true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public int getNumber(int x, int y) {
		return this.cells[y * this.WIDTH + x].getValue();
	}

	public int nextAvailableCell() {
		return this.availableCells.getFirst();
	}

	/**
	 * 
	 * @param pos
	 */
	public void backCellAvailable(int pos) {
		this.availableCells.add(pos);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param note
	 */
	public boolean isCellContaining(int x, int y, int note) {
        return this.cells[y * this.WIDTH + x].getNote(note) == 1;
    }

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void removeNumber(int x, int y) {
		this.cells[y * this.WIDTH + x].setValue(0);
	}

	public void lockFilledCells() {
        for (Cell cell : this.cells) {
            if (cell.getValue() != 0) {
                cell.lock();
            }
        }
	}

	public void clearEntries() {
        for (Cell cell : this.cells) {
            if (!cell.isLocked()) {
                cell.setValue(0);
            }
        }
	}

	public void resetGrid() {
        for (Cell cell : this.cells) {
            cell.setValue(0);
        }
    }

	/**
	 * 
	 * @param x
     * @param valeur
	 */
	public void lineUpdate(int x, int valeur) {
        for (int i = 0; i < this.cells.length; i++) {
            if (i / this.WIDTH == x) {
                this.cells[i].removeNote(valeur);
            }
        }
	}

	/**
	 *
	 * @param y
     * @param valeur
	 */
	public void columnUpdate(int y, int valeur) {
        for (int i = 0; i < this.cells.length; i++) {
            if (i % this.WIDTH == y) {
                this.cells[i].removeNote(valeur);
            }
        }
	}

    public int getRowIndex(int pos) {
        return pos / this.WIDTH;
    }

    public int getColumnIndex(int pos) {
        return pos % this.WIDTH;
    }

    public int getBlockIndex(int pos) {
        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);
        int startX = x - (x % blockWidth);
        int startY = y - (y % blockHeight);
        return startY * this.WIDTH + startX;
    }

	/**
	 *
	 * @param pos
	 * @param valeur
	 */
	public void blockUpdate(int pos, int valeur) {
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);

        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        int startBlock = getBlockIndex(pos);

        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);

        for (int i = startBlock; i < startBlock + blockHeight * this.WIDTH; i += this.WIDTH) {
            for (int j = startBlock; j < startBlock + blockWidth; j++) {
                this.cells[i * this.WIDTH + j].removeNote(valeur);
            }
        }
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void diagonalUpdate(int x, int y) {
		// TODO - implement Grid.diagonalUpdate
		throw new UnsupportedOperationException();
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
		int startX = getColumnIndex(startBlock);
		int startY = getRowIndex(startBlock);

		for (int r = startY; r < startY + this.BLOCK_HEIGHT; r++) {
			for (int c = startX; c < startX + this.BLOCK_WIDTH; c++) {
				int index = cellToIndex(c, r);

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