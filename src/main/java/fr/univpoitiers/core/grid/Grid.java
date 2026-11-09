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
        int blockWidth = (int) Math.sqrt(this.WIDTH);
        int blockHeight = (int) Math.sqrt(this.HEIGHT);
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);
        int startX = x - (x % blockWidth);
        int startY = y - (y % blockHeight);
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

	/**
	 * 
	 * @param pos
	 */
	public void backCellAvailable(int pos) {
		this.availableCells.add(pos);
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
        int x = getColumnIndex(pos);
        int y = getRowIndex(pos);

        if (x < 0 || x >= this.WIDTH || y < 0 || y >= this.HEIGHT) {
            throw new IllegalArgumentException("x and y must be within the grid bounds.");
        }

        int startBlock = getBlockIndex(pos);

        // rowStart = première case de chaque ligne du bloc
        for (int rowStart = startBlock; rowStart < startBlock + this.BLOCK_HEIGHT * this.WIDTH; rowStart += this.WIDTH) {
            for (int i = rowStart; i < rowStart + this.BLOCK_WIDTH; i++) {
                this.cells[i].removeNote(valeur);
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
            for (int j = 0; j < Math.max(this.WIDTH, this.HEIGHT); j++) {
                if (this.cells[i].getNote(j) != otherGrid.cells[i].getNote(j)) {
                    return false;
                }
            }
        }

        return true;
    }

	/**
	 *
	 * @param pos
	 */
	public void diagonalUpdate(int pos) {
		// TODO - implement Grid.diagonalUpdate
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
	 * @param pos position de la case (index 1D)
	 * @param value valeur à vérifier
	 * @return true si aucune autre case de la ligne ne contient cette valeur
	 */
	public boolean checkRow(int pos, int value) {
		int y = getRowIndex(pos);
		for (int c = 0; c < this.WIDTH; c++) {
			int index = cellToIndex(c, y);

			if (index != pos) {
				if (cells[index].getValue() == value) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Vérifie que la valeur n'est pas déjà présente sur la colonne.
	 *
	 * @param pos position de la case (index 1D)
	 * @param value valeur à vérifier
	 * @return true si aucune autre case de la colonne ne contient cette valeur
	 */
	public boolean checkColumn(int pos, int value) {
		int x = getColumnIndex(pos);
		for (int r = 0; r < this.HEIGHT; r++) {
			int index = cellToIndex(x, r);

			if (index != pos) {
				if (cells[index].getValue() == value) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Vérifie que la valeur n'est pas déjà présente dans le bloc de la case
	 * (taille BLOCK_WIDTH x BLOCK_HEIGHT).
	 *
	 * @param pos position de la case (index 1D)
	 * @param value valeur à vérifier
	 * @return true si aucune autre case du bloc ne contient cette valeur
	 */
	public boolean checkBlock(int pos, int value) {
		int startBlock = getBlockIndex(pos);
		int startX = getColumnIndex(startBlock);
		int startY = getRowIndex(startBlock);

		for (int r = startY; r < startY + this.BLOCK_HEIGHT; r++) {
			for (int c = startX; c < startX + this.BLOCK_WIDTH; c++) {
				int index = cellToIndex(c, r);

				if (index != pos) {
					if (cells[index].getValue() == value) {
						return false;
					}
				}
			}
		}
		return true;
	}

}