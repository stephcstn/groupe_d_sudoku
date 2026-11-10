package fr.univpoitiers.app;

import fr.univpoitiers.core.grid.Cell;
import fr.univpoitiers.core.grid.Grid;
import fr.univpoitiers.core.solver.Solver;

import java.util.ArrayList;
import java.util.List;

public class SudokuCoreModel {

    private Solver solver;
    private Grid currentGrid;
    private boolean gridLocked;

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
        gridLocked = false;
    }

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
     */
    public boolean isCellContainingNote(int noteValue, int x, int y) {
        Grid grid = requireGrid();
        return grid.isCellContaining(grid.cellToIndex(x, y), noteValue);
    }

    private Grid requireGrid() {
        if (currentGrid == null) {
            throw new IllegalStateException("La grille doit être créée avant d'être utilisée.");
        }
        return currentGrid;
    }
}