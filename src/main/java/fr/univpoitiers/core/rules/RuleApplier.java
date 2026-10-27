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