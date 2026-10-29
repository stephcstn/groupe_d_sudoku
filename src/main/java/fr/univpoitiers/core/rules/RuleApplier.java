package fr.univpoitiers.core.rules;

import java.util.ArrayList;
import java.util.List;

import fr.univpoitiers.core.grid.Grid;

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
	 * 
	 * @param grid
	 */
	static boolean PointingPair(Grid grid){
		return true;
	}

	/**
	 * 
	 * @param grid
	 */
	static boolean PointingTriple(Grid grid){
		return true;
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