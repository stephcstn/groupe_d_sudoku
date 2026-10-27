package fr.univpoitiers.core.rules;

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
		boolean changed = false;

		for (int startY = 0; startY < height; startY += blockH) {
			for (int startX = 0; startX < width; startX += blockW) {
				for (int value = 1; value <= nbValues; value++) {
					int note = value - 1; // convention : note[value - 1]

					// Cellules vides du bloc qui ont ce candidat
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
		return true;
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