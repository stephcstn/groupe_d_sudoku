package fr.univpoitiers.acceptance;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de validation (recette) des scénarios du backlog liés à la vérification
 * des contraintes : T03, T04 (US02) et T12 (US06).
 *
 * <p>L'interface ne permet pas encore de saisir des chiffres : ces scénarios sont
 * donc joués au niveau du modèle ({@link Grid}), qui porte la règle métier.
 * Les coordonnées du backlog, (ligne, colonne) à partir de 1, sont converties
 * en position avec {@link #pos(int, int)}.</p>
 */
class ConstraintAcceptanceTest {

    private Grid grid;

    /** Grille de départ 9x9 (0 = case vide). */
    private static final int[] INITIAL = {
            5, 3, 0, 0, 7, 0, 0, 0, 0,
            6, 0, 0, 1, 9, 5, 0, 0, 0,
            0, 9, 8, 0, 0, 0, 0, 6, 0,
            8, 0, 0, 0, 6, 0, 0, 0, 3,
            4, 0, 0, 8, 0, 3, 0, 0, 1,
            7, 0, 0, 0, 2, 0, 0, 0, 6,
            0, 6, 0, 0, 0, 0, 2, 8, 0,
            0, 0, 0, 4, 1, 9, 0, 0, 5,
            0, 0, 0, 0, 8, 0, 0, 7, 9
    };

    @BeforeEach
    void setUp() {
        grid = new Grid(9, 9);
        for (int i = 0; i < INITIAL.length; i++) {
            if (INITIAL[i] != 0) {
                grid.addNumber(i, INITIAL[i]);
            }
        }
    }

    /** Position d'une case à partir de (ligne, colonne) numérotées à partir de 1. */
    private static int pos(int row, int col) {
        return (row - 1) * 9 + (col - 1);
    }

    /** Saisie joueur : la valeur est placée, et on renvoie si elle respecte les règles. */
    private boolean input(int row, int col, int value) {
        int p = pos(row, col);
        boolean valid = grid.checkValidValues(p, value);
        grid.addNumber(p, value);
        return valid;
    }

    /** Vérification (bouton « Check ») : positions des saisies joueur en conflit. */
    private List<Integer> check(List<Integer> playerEntries) {
        List<Integer> invalid = new ArrayList<>();
        for (int p : playerEntries) {
            if (!grid.checkValidValues(p, grid.getNumber(p))) {
                invalid.add(p);
            }
        }
        return invalid;
    }

    @Test
    @DisplayName("T03 (US02) : un chiffre en double sur la même ligne et le même bloc est détecté")
    void t03_duplicatedNumberIsDetected() {
        // Grille vide pour reproduire exactement le scénario : 4 en (1,1) puis 4 en (1,2)
        grid = new Grid(9, 9);

        assertTrue(input(1, 1, 4), "premier 4 : aucune alerte attendue");
        assertFalse(input(1, 2, 4), "second 4 : alerte attendue (même ligne et même bloc)");
        assertFalse(grid.checkRow(pos(1, 2), 4));
        assertFalse(grid.checkBlock(pos(1, 2), 4));
    }

    @Test
    @DisplayName("T04 (US02) : une valeur non autorisée est refusée")
    void t04_unauthorizedValueIsRejected() {
        int p = pos(1, 3); // case vide
        // La saisie d'une lettre est filtrée par l'interface ; le modèle refuse
        // toute valeur hors de l'intervalle 1..9.
        assertFalse(grid.checkValidValues(p, 0));
        assertFalse(grid.checkValidValues(p, 10));
        assertFalse(grid.checkValidValues(p, -4));
        assertEquals(0, grid.getNumber(p), "la case reste vide");
    }

    @Test
    @DisplayName("T12 (US06) : la vérification signale uniquement les saisies du joueur en erreur")
    void t12_checkHighlightsOnlyInvalidPlayerEntries() {
        List<Integer> entries = new ArrayList<>();

        // Saisies correctes
        input(1, 3, 4);  entries.add(pos(1, 3));
        input(2, 2, 7);  entries.add(pos(2, 2));
        // Saisie fausse : un 5 est déjà en (1,1) sur la ligne 1
        input(1, 4, 5);  entries.add(pos(1, 4));

        List<Integer> invalid = check(entries);

        assertEquals(List.of(pos(1, 4)), invalid, "seule la case (1,4) doit être signalée");
        // La vérification ne bloque pas le reste de la grille : les autres saisies restent en place
        assertEquals(4, grid.getNumber(pos(1, 3)));
        assertEquals(7, grid.getNumber(pos(2, 2)));
    }

    @Test
    @DisplayName("T12 (US06) : aucune case signalée quand toutes les saisies sont correctes")
    void t12_checkReportsNothingWhenEntriesAreValid() {
        List<Integer> entries = new ArrayList<>();
        input(1, 3, 4);  entries.add(pos(1, 3));
        input(2, 2, 7);  entries.add(pos(2, 2));

        assertTrue(check(entries).isEmpty());
    }
}
