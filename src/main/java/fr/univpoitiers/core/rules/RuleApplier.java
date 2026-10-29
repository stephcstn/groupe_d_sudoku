package fr.univpoitiers.core.rules;

import java.util.ArrayList;
import java.util.List;

import fr.univpoitiers.core.grid.Grid;

import java.util.ArrayList;
import java.util.List;

public interface RuleApplier {

	/**
	 * 
	 * @param grid
	 */
	static boolean KObvious(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean KHidden(Grid grid){
		return true;
	}

	/**
	 * Pointing pair : dans un bloc, si un candidat n'apparaît que dans 2 cellules
	 * et que ces cellules sont sur la même ligne (ou colonne), on retire ce candidat
	 * des autres cellules de cette ligne (ou colonne) hors du bloc.
	 * @param grid grille courante
	 * @return true si au moins une note a été supprimée
	 */
	static boolean PointingPair(Grid grid) {
		return pointing(grid, 2);
	}

	/**
	 * Pointing triple : même principe avec 3 cellules.
	 * @param grid grille courante
	 * @return true si au moins une note a été supprimée
	 */
	static boolean PointingTriple(Grid grid) {
		return pointing(grid, 3);
	}

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

	static boolean boxLineReductionPair(Grid grid) {
		return boxLine(grid, 2);
	}

	static boolean boxLineReductionTriple(Grid grid) {
		return boxLine(grid, 3);
	}

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
	 * Retire la note des cellules du bloc, sauf celles de la ligne (skipRow)
	 * ou de la colonne (skipCol) concernée. Passer -1 pour ignorer un des deux.
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
	 * 
	 * @param grid
	 */
	static boolean YWing(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean XWing(Grid grid){
		return XWingRows(grid) || XWingCols(grid);
	}

	/**
	 * 
	 * @param grid
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
	 * 
	 * @param grid
	 * @param r
	 * @param noteIndex
	 * @param n
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
	 * 
	 * @param grid
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
	 * 
	 * @param grid
	 * @param r
	 * @param noteIndex
	 * @param n
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
	 * 
	 * @param grid
	 */
	static boolean SwordFish(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean FirstNaive(Grid grid){
		return true;
	}

}