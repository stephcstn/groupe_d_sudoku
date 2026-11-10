package fr.univpoitiers.core.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.univpoitiers.core.grid.Grid;

/**
 * Utility interface providing static methods to apply heuristic deduction rules
 * on a Sudoku grid (Pointing Pairs/Triples, Box-Line Reductions, X-Wing, Y-Wing, Swordfish).
 */
public interface RuleApplier {

	/**
	 * Clear and fill the initial domain values from 1 to k included.
	 * @param listOfValues domain values list to fill
	 * @param k maximum value of the domain
	 */
	static void fillValues(ArrayList<Integer> listOfValues, int k) {
		listOfValues.clear();
		for (int i = 1; i < k + 1; i++) {
			listOfValues.add(i);
		}
	}

	/**
	 * Remove the domain values possible in the block from the initial index cell.
	 * @param grid current grid
	 * @param listOfValues list of domain values to update
	 * @param cellIndex cell belonging to the block processing
	 */
	private static void listOfValuesBlock(Grid grid, ArrayList<Integer> listOfValues, int cellIndex) {
		int startBlockIndex = grid.getBlockIndex(cellIndex);
		// Iterating line by line 2 times more after the first
		for (int i = startBlockIndex; i <= startBlockIndex + grid.getHeight() * 2; i += grid.getHeight()) {
			for (int j = i; j < i + 3 ; j++) {
				int cellValue = grid.getNumber(j);
				if (cellValue != 0) {
					if (listOfValues.contains(cellValue)) {
						listOfValues.remove((Integer) cellValue);
					}
				}
			}
		}
	}

	/**
	 * Remove the domain values possible in the row from the initial index cell.
	 * @param grid current grid
	 * @param listOfValues list of domain values to update
	 * @param cellIndex cell belonging to the row processing
	 */
	private static void listOfValuesRow(Grid grid, ArrayList<Integer> listOfValues, int cellIndex) {
		int startRowIndex = grid.getStartRowIndex(cellIndex);
		for (int i = startRowIndex; i < startRowIndex + grid.getWidth(); i++) {
			int cellValue = grid.getNumber(i);
			if (cellValue != 0) {
				if (listOfValues.contains(cellValue)) {
					listOfValues.remove((Integer) cellValue);
				}
			}
		}
	}

	/**
	 * Remove the domain values possible in the column from the initial index cell.
	 * @param grid current grid
	 * @param listOfValues list of domain values to update
	 * @param cellIndex cell belonging to the column processing
	 */
	private static void listOfValuesColumn(Grid grid, ArrayList<Integer> listOfValues, int cellIndex) {
		int startColumnIndex = grid.getColumnIndex(cellIndex);
		for (int i = startColumnIndex; i <= startColumnIndex + (grid.getHeight() - 1) * grid.getHeight(); i += grid.getHeight()) {
			int cellValue = grid.getNumber(i);
			if (cellValue != 0) {
				if (listOfValues.contains(cellValue)) {
					listOfValues.remove((Integer) cellValue);
				}
			}
		}
	}

	/**
	 * Process if the next cell available is the last in the block or in the row or in the column and a value can be assigned.
	 * If possible, the value is set in the grid and removed from the available cells.
	 * @param grid current grid
	 * @return If it has changed grid state
	 */
	static boolean lastFreeCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();

		fillValues(listOfValues, grid.getWidth());
		listOfValuesBlock(grid, listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		fillValues(listOfValues, grid.getWidth());
		listOfValuesRow(grid, listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		fillValues(listOfValues, grid.getWidth());
		listOfValuesColumn(grid , listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}
		return false;
	}

	/**
	 * Select all rows and columns in the block that doesn't include the current cell
	 * and try to check if only one value can only be in the current cell and insert it if possible.
	 * @param grid the current grid
	 * @return If it has changed grid state
	 */
	static boolean lastRemainingCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();
		fillValues(listOfValues, grid.getWidth());
		listOfValuesBlock(grid, listOfValues, cellIndex);
		// Remove all values present in rows and columns in the block that doesn't include this cell
		int blockPosX = cellIndex % 3;
		int blockPosY = (cellIndex / 9) % 3;
		int startBlock = grid.getBlockIndex(cellIndex);
		for (int j = startBlock; j <= startBlock + grid.getHeight() * 2; j += grid.getHeight()) {
			for (int i = j; i < j + 3; i++) {
				if (i != cellIndex && grid.getNumber(i) == 0) {
					int k = 0;
					while (k < listOfValues.size()) {
						if (grid.checkRow(i, listOfValues.get(k)) && grid.checkColumn(i, listOfValues.get(k))) {
							listOfValues.remove(k);
						} else {
							k++;
						}
					}
				}
			}
		}
		if (listOfValues.size() == 1) {
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		return false;
	}

	/**
	 * Get the last possible value to assign to the current cell taking
	 * in account the block, the row and the columns values already present that it belongs.
	 * @param grid The current grid
	 * @return If it changed the grid state
	 */
	static boolean lastPossibleCell(Grid grid) {
		int cellIndex = grid.nextAvailableCell();
		ArrayList<Integer> listOfValues = new ArrayList<>();
		fillValues(listOfValues, grid.getWidth());

		listOfValuesBlock(grid, listOfValues, cellIndex);
		listOfValuesColumn(grid, listOfValues, cellIndex);
		listOfValuesRow(grid, listOfValues, cellIndex);
		if (listOfValues.size() == 1) {
			return grid.addNumber(cellIndex, listOfValues.getFirst());
		}

		return false;
	}

	/**
	 * Wrap all basic rules : Last free cell, Last remaining cell, and Last possible cell.
	 * @param grid The current grid
	 * @return If one of those rule has changed the grid state
	 */
	static boolean BasicRules(Grid grid) {
		return lastFreeCell(grid) || lastRemainingCell(grid) || lastPossibleCell(grid);
	}


    /**
     * Applies Naked K-tuples deduction rule on the grid.
     * @param grid the current Sudoku grid
     * @return
     */
	static boolean KObvious(Grid grid){
		return true;
	}

    /**
     * Applies Hidden K-tuples deduction rule on the grid.
     * @param grid the current Sudoku grid
     * @return true if at least one candidate note was removed or a value was set
     */
	static boolean KHidden(Grid grid){
		return true;
	}

    /**
     * Pointing pair: within a block, if a candidate appears in only 2 cells
     * and these cells share the same row (or column), this candidate is removed
     * from all other cells in that row (or column) outside the block.
     * @param grid the current Sudoku grid
     * @return true if at least one candidate note was removed
     */
	static boolean PointingPair(Grid grid) {
		return pointing(grid, 2);
	}

    /**
     * Pointing triple: same principle as pointing pair, but involving 3 cells.
     * @param grid the current Sudoku grid
     * @return true if at least one candidate note was removed
     */
	static boolean PointingTriple(Grid grid) {
		return pointing(grid, 3);
	}

    /**
     * Generic helper method for Pointing Pairs and Triples.
     *
     * @param grid the Sudoku grid
     * @param size the size of the tuple (2 for pair, 3 for triple)
     * @return true if at least one note was removed, false otherwise
     */
	private static boolean pointing(Grid grid, int size) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int blockW = (int) Math.sqrt(width);
		int blockH = (int) Math.sqrt(height);
		int nbValues = Math.max(width, height);

		for (int startY = 0; startY < height; startY += blockH) {
			for (int startX = 0; startX < width; startX += blockW) {
				for (int value = 1; value <= nbValues; value++) {
					int note = value - 1;

					List<Integer> positions = new ArrayList<>();
					for (int y = startY; y < startY + blockH; y++) {
						for (int x = startX; x < startX + blockW; x++) {
							int pos = y * width + x;
							if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, note)) {
								positions.add(pos);
							}
						}
					}
					if (positions.size() != size) continue;

					int row = grid.getRowIndex(positions.get(0));
					int col = grid.getColumnIndex(positions.get(0));
					boolean sameRow = true;
					boolean sameCol = true;
					for (int pos : positions) {
						if (grid.getRowIndex(pos) != row) sameRow = false;
						if (grid.getColumnIndex(pos) != col) sameCol = false;
					}

					boolean changed = false;

					// Élimination sur la ligne, hors du bloc
					if (sameRow) {
						for (int x = 0; x < width; x++) {
							if (x >= startX && x < startX + blockW) continue;
							int pos = row * width + x;
							if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, note)) {
								grid.removeNote(pos, note);
								changed = true;
							}
						}
					}
					// Élimination sur la colonne, hors du bloc
					if (sameCol) {
						for (int y = 0; y < height; y++) {
							if (y >= startY && y < startY + blockH) continue;
							int pos = y * width + col;
							if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, note)) {
								grid.removeNote(pos, note);
								changed = true;
							}
						}
					}


					if (changed) {
						return true;
					}
				}
			}
		}
		return false;
	}


    /**
     * Box/Line Reduction Pair (Box-2 Reduction): if a candidate in a row or column
     * is confined to a single block across 2 cells, it is removed from the rest of that block.
     *
     * @param grid the current Sudoku grid
     * @return true if at least one candidate note was removed
     */
	static boolean boxLineReductionPair(Grid grid) {
		return boxLine(grid, 2);
	}

    /**
     * Box/Line Reduction Triple (Box-3 Reduction): same principle as Box-2, but involving 3 cells.
     *
     * @param grid the current Sudoku grid
     * @return true if at least one candidate note was removed
     */
	static boolean boxLineReductionTriple(Grid grid) {
		return boxLine(grid, 3);
	}

    /**
     * Generic helper method for Box/Line Reductions.
     *
     * @param grid the Sudoku grid
     * @param size the size of the tuple (2 for pair, 3 for triple)
     * @return true if at least one note was removed, false otherwise
     */
	private static boolean boxLine(Grid grid, int size) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int nbValues = Math.max(width, height);

		for (int value = 1; value <= nbValues; value++) {
			int note = value - 1;

			// --- 1. Examen des Lignes ---
			for (int row = 0; row < height; row++) {
				List<Integer> positions = new ArrayList<>();
				for (int x = 0; x < width; x++) {
					int pos = row * width + x;
					if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, note)) {
						positions.add(pos);
					}
				}
				if (positions.size() != size) continue;

				int block = grid.getBlockIndex(positions.get(0));
				boolean sameBlock = true;
				for (int pos : positions) {
					if (grid.getBlockIndex(pos) != block) {
						sameBlock = false;
						break;
					}
				}

				// Si toutes les occurrences de la ligne sont dans le même bloc,
				// on effectue l'élimination et on s'arrête si au moins une note a été retirée
				if (sameBlock) {
					if (eliminateInBlock(grid, block, note, row, -1)) {
						return true; // Étape pas-à-pas appliquée !
					}
				}
			}

			// --- 2. Examen des Colonnes ---
			for (int col = 0; col < width; col++) {
				List<Integer> positions = new ArrayList<>();
				for (int y = 0; y < height; y++) {
					int pos = y * width + col;
					if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, note)) {
						positions.add(pos);
					}
				}
				if (positions.size() != size) continue;

				int block = grid.getBlockIndex(positions.get(0));
				boolean sameBlock = true;
				for (int pos : positions) {
					if (grid.getBlockIndex(pos) != block) {
						sameBlock = false;
						break;
					}
				}

				if (sameBlock) {
					if (eliminateInBlock(grid, block, note, -1, col)) {
						return true; // Étape pas-à-pas appliquée !
					}
				}
			}
		}
		return false; // Aucune déduction trouvée par cette règle
	}

    /**
     * Removes the candidate note from cells within a block, except those belonging
     * to the specified row (skipRow) or column (skipCol). Pass -1 to ignore either parameter.
     *
     * @param grid the Sudoku grid
     * @param startBlock the top-left index of the target block
     * @param note the zero-based index of the candidate note to eliminate
     * @param skipRow row index to preserve (-1 to skip)
     * @param skipCol column index to preserve (-1 to skip)
     * @return true if at least one candidate note was removed
     */
	private static boolean eliminateInBlock(Grid grid, int startBlock, int note, int skipRow, int skipCol) {
		int width = grid.getWidth();
		int blockW = (int) Math.sqrt(width);
		int blockH = (int) Math.sqrt(grid.getHeight());
		boolean changed = false;

		for (int dy = 0; dy < blockH; dy++) {
			for (int dx = 0; dx < blockW; dx++) {
				int pos = startBlock + dy * width + dx;
				if (grid.getRowIndex(pos) == skipRow) continue;
				if (grid.getColumnIndex(pos) == skipCol) continue;
				if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, note)) {
					grid.removeNote(pos, note);
					changed = true;
				}
			}
		}
		return changed;
	}

	/**
     * Applies the Y-Wing (XY-Wing) deductive rule to eliminate candidates.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
    static boolean YWing(Grid grid) {
        int n = grid.getWidth();
        int nbRows = grid.getHeight();
        int totalCells = n * nbRows;
        int nbValues = Math.max(n, nbRows);
        List<Integer> bivalueCells = new ArrayList<>();
        for (int pos = 0; pos < totalCells; pos++) {
            if (grid.getNumber(pos) == 0 && countCandidates(grid, pos, nbValues) == 2) {
                bivalueCells.add(pos);
            }
        }
        for (int i = 0; i < bivalueCells.size(); i++) {
            int pivot = bivalueCells.get(i);
            List<Integer> pivotNotes = getCandidates(grid, pivot, nbValues);
            int candA = pivotNotes.get(0);
            int candB = pivotNotes.get(1);
            for (int j = 0; j < bivalueCells.size(); j++) {
                if (i != j) {
                    int wing1 = bivalueCells.get(j);
                    if (cellsSeeEachOther(grid, pivot, wing1)) {
                        List<Integer> notes1 = getCandidates(grid, wing1, nbValues);
                        int sharedWithPivot = -1;
                        int candC = -1;
                        if (notes1.contains(candA) && !notes1.contains(candB)) {
                            sharedWithPivot = candA;
                            candC = (notes1.get(0) == candA) ? notes1.get(1) : notes1.get(0);
                        } else if (notes1.contains(candB) && !notes1.contains(candA)) {
                            sharedWithPivot = candB;
                            candC = (notes1.get(0) == candB) ? notes1.get(1) : notes1.get(0);
                        }
                        if (sharedWithPivot != -1) {
                            int requiredForWing2 = (sharedWithPivot == candA) ? candB : candA;
                            for (int k = j + 1; k < bivalueCells.size(); k++) {
                                if (k != i) {
                                    int wing2 = bivalueCells.get(k);
                                    if (cellsSeeEachOther(grid, pivot, wing2)) {
                                        List<Integer> notes2 = getCandidates(grid, wing2, nbValues);
                                        if (notes2.contains(requiredForWing2) && notes2.contains(candC)) {
                                            boolean modified = false;
                                            for (int target = 0; target < totalCells; target++) {
                                                if (target != pivot && target != wing1 && target != wing2) {
                                                    if (grid.getNumber(target) == 0 && grid.isCellContaining(target, candC)) {
                                                        if (cellsSeeEachOther(grid, target, wing1) && cellsSeeEachOther(grid, target, wing2)) {
                                                            grid.removeNote(target, candC);
                                                            modified = true;
                                                        }
                                                    }
                                                }
                                            }
                                            if (modified) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if two cells see each other (same row, same column, or same block).
     *
     * @param grid the Sudoku grid
     * @param pos1 the position of the first cell
     * @param pos2 the position of the second cell
     * @return true if the two cells share a line, column or block, false otherwise
     */
    private static boolean cellsSeeEachOther(Grid grid, int pos1, int pos2) {
        if (pos1 == pos2) {
            return false;
        }
        boolean sameRow = grid.getRowIndex(pos1) == grid.getRowIndex(pos2);
        boolean sameCol = grid.getColumnIndex(pos1) == grid.getColumnIndex(pos2);
        boolean sameBlock = grid.getBlockIndex(pos1) == grid.getBlockIndex(pos2);
        return sameRow || sameCol || sameBlock;
    }

    /**
     * Counts how many candidates are present in the notes of a cell.
     *
     * @param grid the Sudoku grid
     * @param pos the cell position
     * @param nbValues the maximum number of values
     * @return the number of active notes in the cell
     */
    private static int countCandidates(Grid grid, int pos, int nbValues) {
        int count = 0;
        for (int note = 0; note < nbValues; note++) {
            if (grid.isCellContaining(pos, note)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Retrieves the list of candidate note indices present in a cell.
     *
     * @param grid the Sudoku grid
     * @param pos the cell position
     * @param nbValues the maximum number of values
     * @return the list of note indices
     */
    private static List<Integer> getCandidates(Grid grid, int pos, int nbValues) {
        List<Integer> candidates = new ArrayList<>();
        for (int note = 0; note < nbValues; note++) {
            if (grid.isCellContaining(pos, note)) {
                candidates.add(note);
            }
        }
        return candidates;
    }

	/**
     * Applies the X-Wing deductive rule on both rows and columns.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
	static boolean XWing(Grid grid){
		return XWingRows(grid) || XWingCols(grid);
	}

	/**
     * Applies the X-Wing rule on rows to eliminate candidate notes in columns.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
	static boolean XWingRows(Grid grid) {
        boolean modified = false;
        int n = grid.getWidth();
        int nbRows = grid.getHeight();
        for (int i = 1; i <= n; i++) {
            int noteIndex = i - 1;
            for (int iR1 = 0; iR1 < nbRows - 1; iR1++) {
                List<Integer> colsR1 = getColumnsWithCandidate(grid, iR1, noteIndex, n);
                if (colsR1.size() == 2) {
                    int iC1 = colsR1.get(0);
                    int iC2 = colsR1.get(1);
                    for (int iR2 = iR1 + 1; iR2 < nbRows; iR2++) {
                        List<Integer> colsR2 = getColumnsWithCandidate(grid, iR2, noteIndex, n);
                        if (colsR2.size() == 2 && colsR2.get(0) == iC1 && colsR2.get(1) == iC2) {
                            for (int r = 0; r < nbRows; r++) {
                                if (r != iR1 && r != iR2) {
                                    int posC1 = r * n + iC1;
                                    int posC2 = r * n + iC2;
                                    if (grid.getNumber(posC1) == 0 && grid.isCellContaining(posC1, noteIndex)) {
                                        grid.removeNote(posC1, noteIndex);
                                        modified = true;
                                    }
                                    if (grid.getNumber(posC2) == 0 && grid.isCellContaining(posC2, noteIndex)) {
                                        grid.removeNote(posC2, noteIndex);
                                        modified = true;
                                    }
                                }
                            }
                            if (modified) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
/**
     * Finds column indices on a given row that can contain the candidate note.
     *
     * @param grid the Sudoku grid
     * @param r the row index
     * @param noteIndex the zero-based index of the candidate note
     * @param n the width of the grid
     * @return the list of column indices containing the candidate
     */
	static List<Integer> getColumnsWithCandidate(Grid grid, int r, int noteIndex, int n) {
        List<Integer> cols = new ArrayList<>();
        for (int c = 0; c < n; c++) {
            int pos = r * n + c;
            if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, noteIndex)) {
                cols.add(c);
            }
        }
        return cols;
    }

	/**
     * Applies the X-Wing rule on columns to eliminate candidate notes in rows.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
	static boolean XWingCols(Grid grid) {
        boolean modified = false;
        int n = grid.getWidth();
        int nbRows = grid.getHeight();
        for (int i = 1; i <= n; i++) {
            int noteIndex = i - 1;
            for (int iC1 = 0; iC1 < n - 1; iC1++) {
                List<Integer> rowsC1 = getRowsWithCandidate(grid, iC1, noteIndex, n, nbRows);
                if (rowsC1.size() == 2) {
                    int iR1 = rowsC1.get(0);
                    int iR2 = rowsC1.get(1);
                    for (int iC2 = iC1 + 1; iC2 < n; iC2++) {
                        List<Integer> rowsC2 = getRowsWithCandidate(grid, iC2, noteIndex, n, nbRows);
                        if (rowsC2.size() == 2 && rowsC2.get(0) == iR1 && rowsC2.get(1) == iR2) {
                            for (int c = 0; c < n; c++) {
                                if (c != iC1 && c != iC2) {
                                    int posR1 = iR1 * n + c;
                                    int posR2 = iR2 * n + c;
                                    if (grid.getNumber(posR1) == 0 && grid.isCellContaining(posR1, noteIndex)) {
                                        grid.removeNote(posR1, noteIndex);
                                        modified = true;
                                    }
                                    if (grid.getNumber(posR2) == 0 && grid.isCellContaining(posR2, noteIndex)) {
                                        grid.removeNote(posR2, noteIndex);
                                        modified = true;
                                    }
                                }
                            }
                            if (modified) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

	/**
     * Finds row indices on a given column that can contain the candidate note.
     *
     * @param grid the Sudoku grid
     * @param c the column index
     * @param noteIndex the zero-based index of the candidate note
     * @param n the width of the grid
     * @param nbRows the height of the grid
     * @return the list of row indices containing the candidate
     */
    static List<Integer> getRowsWithCandidate(Grid grid, int c, int noteIndex, int n, int nbRows) {
        List<Integer> rows = new ArrayList<>();
        for (int r = 0; r < nbRows; r++) {
            int pos = r * n + c;
            if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, noteIndex)) {
                rows.add(r);
            }
        }
        return rows;
    }

/**
     * Applies the Swordfish deductive rule on both rows and columns.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
    static boolean Swordfish(Grid grid) {
        return SwordfishRows(grid) || SwordfishCols(grid);
    }

    /**
     * Applies the Swordfish rule on rows to eliminate candidate notes in columns.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
    static boolean SwordfishRows(Grid grid) {
        boolean modified = false;
        int n = grid.getWidth();
        int nbRows = grid.getHeight();
        for (int i = 1; i <= n; i++) {
            int noteIndex = i - 1;
            for (int iR1 = 0; iR1 < nbRows - 2; iR1++) {
                List<Integer> colsR1 = getColumnsWithCandidate(grid, iR1, noteIndex, n);
                if (colsR1.size() >= 2 && colsR1.size() <= 3) {
                    for (int iR2 = iR1 + 1; iR2 < nbRows - 1; iR2++) {
                        List<Integer> colsR2 = getColumnsWithCandidate(grid, iR2, noteIndex, n);
                        if (colsR2.size() >= 2 && colsR2.size() <= 3) {
                            for (int iR3 = iR2 + 1; iR3 < nbRows; iR3++) {
                                List<Integer> colsR3 = getColumnsWithCandidate(grid, iR3, noteIndex, n);
                                if (colsR3.size() >= 2 && colsR3.size() <= 3) {
                                    Set<Integer> unionCols = new HashSet<>();
                                    unionCols.addAll(colsR1);
                                    unionCols.addAll(colsR2);
                                    unionCols.addAll(colsR3);
                                    if (unionCols.size() == 3) {
                                        for (int col : unionCols) {
                                            for (int r = 0; r < nbRows; r++) {
                                                if (r != iR1 && r != iR2 && r != iR3) {
                                                    int pos = r * n + col;
                                                    if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, noteIndex)) {
                                                        grid.removeNote(pos, noteIndex);
                                                        modified = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (modified) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Applies the Swordfish rule on columns to eliminate candidate notes in rows.
     *
     * @param grid the Sudoku grid to process
     * @return true if at least one candidate note was removed, false otherwise
     */
    static boolean SwordfishCols(Grid grid) {
        boolean modified = false;
        int n = grid.getWidth();
        int nbRows = grid.getHeight();
        for (int i = 1; i <= n; i++) {
            int noteIndex = i - 1;
            for (int iC1 = 0; iC1 < n - 2; iC1++) {
                List<Integer> rowsC1 = getRowsWithCandidate(grid, iC1, noteIndex, n, nbRows);
                if (rowsC1.size() >= 2 && rowsC1.size() <= 3) {
                    for (int iC2 = iC1 + 1; iC2 < n - 1; iC2++) {
                        List<Integer> rowsC2 = getRowsWithCandidate(grid, iC2, noteIndex, n, nbRows);
                        if (rowsC2.size() >= 2 && rowsC2.size() <= 3) {
                            for (int iC3 = iC2 + 1; iC3 < n; iC3++) {
                                List<Integer> rowsC3 = getRowsWithCandidate(grid, iC3, noteIndex, n, nbRows);
                                if (rowsC3.size() >= 2 && rowsC3.size() <= 3) {
                                    Set<Integer> unionRows = new HashSet<>();
                                    unionRows.addAll(rowsC1);
                                    unionRows.addAll(rowsC2);
                                    unionRows.addAll(rowsC3);
                                    if (unionRows.size() == 3) {
                                        for (int row : unionRows) {
                                            for (int c = 0; c < n; c++) {
                                                if (c != iC1 && c != iC2 && c != iC3) {
                                                    int pos = row * n + c;
                                                    if (grid.getNumber(pos) == 0 && grid.isCellContaining(pos, noteIndex)) {
                                                        grid.removeNote(pos, noteIndex);
                                                        modified = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (modified) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

	/**
	 * 
	 * @param grid
	 */
	static boolean FirstNaive(Grid grid){
		return true;
	}

}