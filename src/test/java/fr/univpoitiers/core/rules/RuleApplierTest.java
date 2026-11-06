package fr.univpoitiers.core.rules;

import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests des règles Pointing (Pair / Triple) et Box/Line Reduction (Pair / Triple).
 * Chaque test part d'une grille 9x9 vide sans aucune note, puis place à la main
 * les notes nécessaires au scénario.
 */
class RuleApplierTest {

    private Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid(9, 9);
    }

    /** Convertit (ligne, colonne) en index 1D. */
    private static int pos(int row, int col) {
        return row * 9 + col;
    }

    /** Ajoute la note dans chaque case donnée sous forme {ligne, colonne}. */
    private void addNote(int note, int[]... cells) {
        for (int[] rc : cells) {
            grid.setNote(pos(rc[0], rc[1]), note);
        }
    }

    private boolean has(int row, int col, int note) {
        return grid.isCellContaining(pos(row, col), note);
    }

    @Nested
    @DisplayName("Pointing Pair / Triple")
    class PointingTests {

        @Test
        @DisplayName("Pair sur une ligne : retire la note du reste de la ligne, hors du bloc")
        void pointingPair_onRow_removesNoteOutsideBlock() {
            int note = 4;
            // Bloc 0 : la note n'est que dans (0,0) et (0,1), alignées sur la ligne 0
            addNote(note, new int[]{0, 0}, new int[]{0, 1});
            // Autres occurrences : sur la ligne 0 (à retirer) et sur la ligne 1 (à garder)
            addNote(note, new int[]{0, 5}, new int[]{0, 8}, new int[]{1, 5});

            assertTrue(RuleApplier.PointingPair(grid));

            assertTrue(has(0, 0, note));
            assertTrue(has(0, 1, note));
            assertFalse(has(0, 5, note));
            assertFalse(has(0, 8, note));
            assertTrue(has(1, 5, note), "une case hors de la ligne ne doit pas être touchée");
        }

        @Test
        @DisplayName("Pair sur une colonne : retire la note du reste de la colonne, hors du bloc")
        void pointingPair_onColumn_removesNoteOutsideBlock() {
            int note = 2;
            // Bloc 0 : la note n'est que dans (0,0) et (2,0), alignées sur la colonne 0
            addNote(note, new int[]{0, 0}, new int[]{2, 0});
            addNote(note, new int[]{5, 0}, new int[]{8, 0}, new int[]{5, 1});

            assertTrue(RuleApplier.PointingPair(grid));

            assertTrue(has(0, 0, note));
            assertTrue(has(2, 0, note));
            assertFalse(has(5, 0, note));
            assertFalse(has(8, 0, note));
            assertTrue(has(5, 1, note), "une case hors de la colonne ne doit pas être touchée");
        }

        @Test
        @DisplayName("Pair : renvoie false si les 2 cases du bloc ne sont pas alignées")
        void pointingPair_whenNotAligned_returnsFalse() {
            int note = 4;
            addNote(note, new int[]{0, 0}, new int[]{1, 1}, new int[]{0, 5});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.PointingPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Pair : renvoie false s'il n'y a rien à retirer hors du bloc")
        void pointingPair_whenNothingToRemove_returnsFalse() {
            addNote(4, new int[]{0, 0}, new int[]{0, 1});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.PointingPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Pair : ignore les cases déjà remplies")
        void pointingPair_ignoresFilledCells() {
            int note = 4;
            addNote(note, new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 5});
            // (0,5) est remplie : sa note ne doit pas être modifiée
            grid.addNumber(pos(0, 5), 7);

            assertFalse(RuleApplier.PointingPair(grid));
            assertTrue(has(0, 5, note));
        }

        @Test
        @DisplayName("Triple : seul PointingTriple s'applique à 3 cases alignées")
        void pointingTriple_onRow_removesNoteOutsideBlock() {
            int note = 6;
            addNote(note, new int[]{0, 0}, new int[]{0, 1}, new int[]{0, 2});
            addNote(note, new int[]{0, 5});

            assertFalse(RuleApplier.PointingPair(grid), "Pair ne doit pas traiter 3 cases");
            assertTrue(RuleApplier.PointingTriple(grid));

            assertTrue(has(0, 0, note));
            assertTrue(has(0, 1, note));
            assertTrue(has(0, 2, note));
            assertFalse(has(0, 5, note));
        }
    }

    @Nested
    @DisplayName("Box/Line Reduction Pair / Triple")
    class BoxLineReductionTests {

        @Test
        @DisplayName("Pair sur une ligne : retire la note du reste du bloc")
        void boxLinePair_onRow_removesNoteInRestOfBlock() {
            int note = 2;
            // Ligne 4 : la note n'est que dans (4,3) et (4,4), toutes deux dans le bloc central
            addNote(note, new int[]{4, 3}, new int[]{4, 4});
            // Autres cases du bloc central (à retirer)
            addNote(note, new int[]{3, 3}, new int[]{5, 5});

            assertTrue(RuleApplier.boxLineReductionPair(grid));

            assertTrue(has(4, 3, note));
            assertTrue(has(4, 4, note));
            assertFalse(has(3, 3, note));
            assertFalse(has(5, 5, note));
        }

        @Test
        @DisplayName("Pair sur une colonne : retire la note du reste du bloc")
        void boxLinePair_onColumn_removesNoteInRestOfBlock() {
            int note = 6;
            // Colonne 4 : la note n'est que dans (3,4) et (5,4), toutes deux dans le bloc central
            addNote(note, new int[]{3, 4}, new int[]{5, 4});
            // Autres cases du bloc central (à retirer) + une case hors du bloc (à garder)
            addNote(note, new int[]{4, 3}, new int[]{4, 5}, new int[]{4, 0});

            assertTrue(RuleApplier.boxLineReductionPair(grid));

            assertTrue(has(3, 4, note));
            assertTrue(has(5, 4, note));
            assertFalse(has(4, 3, note));
            assertFalse(has(4, 5, note));
            assertTrue(has(4, 0, note), "une case hors du bloc ne doit pas être touchée");
        }

        @Test
        @DisplayName("Pair : renvoie false si les 2 cases de la ligne sont dans des blocs différents")
        void boxLinePair_whenDifferentBlocks_returnsFalse() {
            addNote(1, new int[]{0, 0}, new int[]{0, 5}, new int[]{1, 1});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.boxLineReductionPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Pair : renvoie false s'il n'y a rien à retirer dans le bloc")
        void boxLinePair_whenNothingToRemove_returnsFalse() {
            addNote(2, new int[]{4, 3}, new int[]{4, 4});
            Grid before = new Grid(grid);

            assertFalse(RuleApplier.boxLineReductionPair(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Triple : seul boxLineReductionTriple s'applique à 3 cases d'une ligne")
        void boxLineTriple_onRow_removesNoteInRestOfBlock() {
            int note = 8;
            // Ligne 4 : la note n'est que dans (4,3), (4,4), (4,5), toutes dans le bloc central
            addNote(note, new int[]{4, 3}, new int[]{4, 4}, new int[]{4, 5});
            // (3,3) dans le bloc (à retirer), (0,3) hors du bloc (à garder)
            addNote(note, new int[]{3, 3}, new int[]{0, 3});

            assertFalse(RuleApplier.boxLineReductionPair(grid), "Pair ne doit pas traiter 3 cases");
            assertTrue(RuleApplier.boxLineReductionTriple(grid));

            assertTrue(has(4, 3, note));
            assertTrue(has(4, 4, note));
            assertTrue(has(4, 5, note));
            assertFalse(has(3, 3, note));
            assertTrue(has(0, 3, note), "une case hors du bloc ne doit pas être touchée");
        }
    }
}
