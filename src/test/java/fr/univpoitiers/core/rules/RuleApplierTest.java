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

    @Nested
    @DisplayName("X-Wing")
    class XWingTests {

        @Test
        @DisplayName("X-Wing sur lignes : retire la note du reste des deux colonnes")
        void xWing_onRows_removesNoteInColumns() {
            int note = 3;
            // Lignes 1 et 4 : la note n'est présente que dans les colonnes 2 et 5
            addNote(note, new int[]{1, 2}, new int[]{1, 5});
            addNote(note, new int[]{4, 2}, new int[]{4, 5});

            // Autres occurrences sur ces colonnes (à retirer) et une hors colonnes (à garder)
            addNote(note, new int[]{7, 2}, new int[]{8, 5}, new int[]{7, 0});

            assertTrue(RuleApplier.XWing(grid));
            assertTrue(has(1, 2, note));
            assertTrue(has(1, 5, note));
            assertTrue(has(4, 2, note));
            assertTrue(has(4, 5, note));
            assertFalse(has(7, 2, note));
            assertFalse(has(8, 5, note));
            assertTrue(has(7, 0, note), "une case hors des colonnes cibles ne doit pas être touchée");
        }

        @Test
        @DisplayName("X-Wing sur colonnes : retire la note du reste des deux lignes")
        void xWing_onColumns_removesNoteInRows() {
            int note = 6;
            // Colonnes 1 et 6 : la note n'est présente que dans les lignes 2 et 7
            addNote(note, new int[]{2, 1}, new int[]{7, 1});
            addNote(note, new int[]{2, 6}, new int[]{7, 6});

            // Autres occurrences sur ces lignes (à retirer) et une hors lignes (à garder)
            addNote(note, new int[]{2, 3}, new int[]{7, 8}, new int[]{0, 3});

            assertTrue(RuleApplier.XWing(grid));
            assertTrue(has(2, 1, note));
            assertTrue(has(7, 1, note));
            assertTrue(has(2, 6, note));
            assertTrue(has(7, 6, note));
            assertFalse(has(2, 3, note));
            assertFalse(has(7, 8, note));
            assertTrue(has(0, 3, note), "une case hors des lignes cibles ne doit pas être touchée");
        }

        @Test
        @DisplayName("X-Wing : renvoie false s'il n'y a rien à retirer")
        void xWing_whenNothingToRemove_returnsFalse() {
            int note = 2;
            addNote(note, new int[]{1, 2}, new int[]{1, 5});
            addNote(note, new int[]{4, 2}, new int[]{4, 5});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.XWing(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("X-Wing : ignore les cases déjà remplies")
        void xWing_ignoresFilledCells() {
            int note = 3;
            addNote(note, new int[]{1, 2}, new int[]{1, 5});
            addNote(note, new int[]{4, 2}, new int[]{4, 5});
            addNote(note, new int[]{7, 2});

            // (7,2) est remplie : sa note ne doit pas être touchée
            grid.addNumber(pos(7, 2), 4);

            assertFalse(RuleApplier.XWing(grid));
            assertTrue(has(7, 2, note));
        }
    }

    @Nested
    @DisplayName("Y-Wing")
    class YWingTests {

        @Test
        @DisplayName("Y-Wing classique : retire le candidat des cases voyant les deux pinces")
        void yWing_removesTargetCandidate() {
            int candA = 0;
            int candB = 1;
            int candC = 2;

            // Pivot en (0,0) avec {A, B}
            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});

            // Pince 1 en (0,5) avec {A, C} (voit le pivot par la ligne 0)
            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});

            // Pince 2 en (5,0) avec {B, C} (voit le pivot par la colonne 0)
            addNote(candB, new int[]{5, 0});
            addNote(candC, new int[]{5, 0});

            // Case (5,5) : voit Pince 1 (colonne 5) et Pince 2 (ligne 5) -> C à retirer
            addNote(candC, new int[]{5, 5});
            addNote(candA, new int[]{5, 5});

            // Case témoin (7,5) voyant seulement Pince 1
            addNote(candC, new int[]{7, 5});

            assertTrue(RuleApplier.YWing(grid));
            assertTrue(has(0, 0, candA));
            assertTrue(has(0, 5, candC));
            assertTrue(has(5, 0, candC));
            assertFalse(has(5, 5, candC));
            assertTrue(has(5, 5, candA));
            assertTrue(has(7, 5, candC), "une case ne voyant qu'une seule pince ne doit pas être touchée");
        }

        @Test
        @DisplayName("Y-Wing : renvoie false si le pivot a plus de 2 candidats")
        void yWing_whenPivotNotBivalue_returnsFalse() {
            int candA = 0;
            int candB = 1;
            int candC = 2;

            // Pivot à 3 candidats
            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});
            addNote(candC, new int[]{0, 0});

            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});
            addNote(candB, new int[]{5, 0});
            addNote(candC, new int[]{5, 0});
            addNote(candC, new int[]{5, 5});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.YWing(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Y-Wing : renvoie false si les pinces ne voient pas le pivot")
        void yWing_whenWingsDoNotSeePivot_returnsFalse() {
            int candA = 0;
            int candB = 1;
            int candC = 2;

            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});

            // Pince 1 voit le pivot
            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});

            // Pince 2 en (7,8) ne partage ni ligne, ni colonne, ni bloc avec (0,0)
            addNote(candB, new int[]{7, 8});
            addNote(candC, new int[]{7, 8});
            addNote(candC, new int[]{7, 5});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.YWing(grid));
            assertEquals(before, grid);
        }

        @Test
        @DisplayName("Y-Wing : renvoie false s'il n'y a rien à retirer")
        void yWing_whenNothingToRemove_returnsFalse() {
            int candA = 0;
            int candB = 1;
            int candC = 2;

            addNote(candA, new int[]{0, 0});
            addNote(candB, new int[]{0, 0});
            addNote(candA, new int[]{0, 5});
            addNote(candC, new int[]{0, 5});
            addNote(candB, new int[]{5, 0});
            addNote(candC, new int[]{5, 0});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.YWing(grid));
            assertEquals(before, grid);
        }
    }

    @Nested
    @DisplayName("Swordfish")
    class SwordfishTests {

        @Test
        @DisplayName("Swordfish sur lignes : retire la note du reste des 3 colonnes")
        void swordfish_onRows_removesNoteInColumns() {
            int note = 5;

            // 3 lignes (0, 3, 6) restreintes à 3 colonnes (1, 4, 7)
            addNote(note, new int[]{0, 1}, new int[]{0, 4});
            addNote(note, new int[]{3, 4}, new int[]{3, 7});
            addNote(note, new int[]{6, 1}, new int[]{6, 7});

            // Occurrences à retirer sur les colonnes 1, 4, 7 hors de ces 3 lignes
            addNote(note, new int[]{1, 1}, new int[]{8, 4}, new int[]{5, 7});

            // Case témoin sur la colonne 0 (à garder)
            addNote(note, new int[]{1, 0});

            assertTrue(RuleApplier.Swordfish(grid));
            assertTrue(has(0, 1, note));
            assertTrue(has(0, 4, note));
            assertTrue(has(3, 4, note));
            assertTrue(has(3, 7, note));
            assertTrue(has(6, 1, note));
            assertTrue(has(6, 7, note));

            assertFalse(has(1, 1, note));
            assertFalse(has(8, 4, note));
            assertFalse(has(5, 7, note));
            assertTrue(has(1, 0, note), "une case hors des colonnes cibles ne doit pas être touchée");
        }

        @Test
        @DisplayName("Swordfish sur colonnes : retire la note du reste des 3 lignes")
        void swordfish_onColumns_removesNoteInRows() {
            int note = 1;

            // 3 colonnes (0, 4, 8) restreintes à 3 lignes (1, 2, 7)
            addNote(note, new int[]{1, 0}, new int[]{2, 0});
            addNote(note, new int[]{2, 4}, new int[]{7, 4});
            addNote(note, new int[]{1, 8}, new int[]{7, 8});

            // Occurrences à retirer sur les lignes 1, 2, 7 hors de ces 3 colonnes
            addNote(note, new int[]{1, 3}, new int[]{7, 5});

            // Case témoin
            addNote(note, new int[]{5, 3});

            assertTrue(RuleApplier.Swordfish(grid));
            assertTrue(has(1, 0, note));
            assertTrue(has(2, 0, note));
            assertTrue(has(2, 4, note));
            assertTrue(has(7, 4, note));
            assertTrue(has(1, 8, note));
            assertTrue(has(7, 8, note));

            assertFalse(has(1, 3, note));
            assertFalse(has(7, 5, note));
            assertTrue(has(5, 3, note), "une case hors des lignes cibles ne doit pas être touchée");
        }

        @Test
        @DisplayName("Swordfish : renvoie false s'il n'y a rien à retirer")
        void swordfish_whenNothingToRemove_returnsFalse() {
            int note = 4;
            addNote(note, new int[]{0, 1}, new int[]{0, 4});
            addNote(note, new int[]{3, 4}, new int[]{3, 7});
            addNote(note, new int[]{6, 1}, new int[]{6, 7});

            Grid before = new Grid(grid);
            assertFalse(RuleApplier.Swordfish(grid));
            assertEquals(before, grid);
        }
    }
}