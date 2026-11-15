package fr.univpoitiers.app;

import fr.univpoitiers.core.grid.Cell;
import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.solver.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de l'application : fait le lien entre l'interface et la grille du cœur
 * ({@link Grid}), en manipulant les cases par leurs coordonnées (x = colonne, y = ligne).
 */
public class SudokuCoreModel {

    private Solver solver;
    private Grid currentGrid;
    private boolean gridLocked;

    /**
     * Génère une nouvelle grille à résoudre. Pas encore implémenté.
     *
     * @throws UnsupportedOperationException toujours, en attendant le générateur
     */
    public void generateGrid() {
        // TODO : Dépendra de l'implémentation de Solver / Generator
        throw new UnsupportedOperationException();
    }

    /**
     * Crée et initialise une nouvelle grille aux dimensions spécifiées.
     *
     * @param width  largeur
     * @param height hauteur
     */
    public void createGrid(int width, int height) {
        currentGrid = new Grid(width, height);
        gridLocked = false;
    }

    /**
     * Saisit une valeur dans la case de coordonnées {@code (x, y)}.
     *
     * @param value valeur à saisir (1 à 9), ou 0 pour vider la case
     * @param x     colonne de la case, à partir de 0
     * @param y     ligne de la case, à partir de 0
     * @return vrai si la saisie a été acceptée
     * @throws IllegalArgumentException si les coordonnées sont hors de la grille
     */
    public boolean input(int value, int x, int y) {
        Grid grid = requireGrid();
        int pos = grid.cellToIndex(x, y);

        if (value == 0) {
            grid.removeNumber(pos);
            return true;
        }

        return grid.addNumber(pos, value);
    }

    /**
     * Vérifie les cases remplies et renvoie la liste des 1D indices des cases invalides / en conflit.
     *
     * @return un tableau d'indices 1D des cases comportant une erreur
     */
    public int[] checkEntries() {
        Grid grid = requireGrid();
        int totalCells = grid.getWidth() * grid.getHeight();
        List<Integer> invalidPositions = new ArrayList<>();

        for (int pos = 0; pos < totalCells; pos++) {
            int val = grid.getNumber(pos);
            if (val != 0) {
                // checkValidValues vérifie la cohérence ligne, colonne, bloc
                if (!grid.checkValidValues(pos, val)) {
                    invalidPositions.add(pos);
                }
            }
        }

        return invalidPositions.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Résout la grille courante selon les règles fournies.
     *
     * @param rules tableau d'identifiants de règles à appliquer
     * @return vrai si une solution a été trouvée
     */
    public boolean solve(int[] rules) {
        // TODO : à brancher sur le Solver une fois disponible
        throw new UnsupportedOperationException();
    }

    /**
     * Réinitialise complètement la grille (toutes les cases redeviennent vides).
     */
    public void resetGrid() {
        requireGrid().resetGrid();
        gridLocked = false;
    }

    /**
     * Lit la valeur de la case de coordonnées {@code (x, y)}.
     *
     * @param x colonne de la case, à partir de 0
     * @param y ligne de la case, à partir de 0
     * @return la valeur, ou 0 si la case est vide
     * @throws IllegalArgumentException si les coordonnées sont hors de la grille
     */
    public int getNumber(int x, int y) {
        Grid grid = requireGrid();
        return grid.getNumber(grid.cellToIndex(x, y));
    }

    /**
     * Verrouille toutes les cases non vides (pour marquer les indices initiaux de la grille).
     */
    public void lockFilledCells() {
        requireGrid().lockFilledCells();
        gridLocked = true;
    }

    /**
     * Déverrouille la grille.
     */
    public void unlockGrid() {
        requireGrid().unlockGrid();
        gridLocked = false;
    }

    /**
     * Indique si la grille est verrouillée.
     *
     * @return vrai si les cases remplies ont été verrouillées
     */
    public boolean isGridLocked() {
        return gridLocked;
    }

    /**
     * Efface uniquement les chiffres saisis par l'utilisateur sans supprimer les cases verrouillées.
     */
    public void clearGrid() {
        requireGrid().clearEntries();
    }

    // -------------------------------------------------------------------------
    // Méthodes pour relier la gestion des notes (candidats 1 à 9)
    // -------------------------------------------------------------------------

    /**
     * Vérifie si la case (x, y) contient une note candidate donnée.
     *
     * @param noteValue indice de la note, à partir de 0 (contrairement à {@link #getNote})
     * @param x         colonne de la case, à partir de 0
     * @param y         ligne de la case, à partir de 0
     * @return vrai si la note est active
     */
    public boolean isCellContainingNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        return grid.isCellContaining(grid.cellToIndex(x, y), noteValue);
    }

    /**
     * Retourne la grille courante.
     *
     * @return la grille courante
     * @throws IllegalStateException si aucune grille n'a été créée
     */
    private Grid requireGrid() {
        if (currentGrid == null) {
            throw new IllegalStateException("La grille doit être créée avant d'être utilisée.");
        }
        return currentGrid;
    }

    /**
     * Indique si la case (x, y) est verrouillée.
     *
     * @param x colonne de la case, à partir de 0
     * @param y ligne de la case, à partir de 0
     * @return vrai si la case est verrouillée
     */
    public boolean isCellLocked(int x, int y) {
        Grid grid = requireGrid();
        return grid.isLocked(grid.cellToIndex(x, y));
    }

    /**
     * Indique si la case (x, y) contient la note d'un chiffre.
     *
     * @param noteValue chiffre de la note, à partir de 1
     * @param x         colonne de la case, à partir de 0
     * @param y         ligne de la case, à partir de 0
     * @return vrai si la note est active
     */
    public boolean getNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        return grid.isCellContaining(grid.cellToIndex(x, y), noteValue - 1);
    }

    /**
     * Ajoute la note d'un chiffre à la case (x, y), ou la retire si elle y est déjà.
     *
     * @param noteValue chiffre de la note, à partir de 1
     * @param x         colonne de la case, à partir de 0
     * @param y         ligne de la case, à partir de 0
     */
    public void toggleNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        int pos = grid.cellToIndex(x, y);
        int index = noteValue - 1;
        if (grid.isCellContaining(pos, index)) {
            grid.removeNote(pos, index);
        } else {
            grid.setNote(pos, index);
        }
    }

    /**
     * Retire toutes les notes de la case (x, y), sans toucher à sa valeur.
     *
     * @param x colonne de la case, à partir de 0
     * @param y ligne de la case, à partir de 0
     */
    public void clearCellNotes(int x, int y) {
        Grid grid = requireGrid();
        grid.clearNotes(grid.cellToIndex(x, y));
    }


}