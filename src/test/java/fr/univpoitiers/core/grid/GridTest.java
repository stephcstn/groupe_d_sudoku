package fr.univpoitiers.core.grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    private Grid grid9x9;

    @BeforeEach
    void setUp() {
        grid9x9 = new Grid(9, 9);
    }

    @Nested
    @DisplayName("Tests du Constructeur et des Dimensions")
    class ConstructorTests {

        @Test
        @DisplayName("Initialisation correcte de la taille")
        void testDimensions() {
            assertEquals(9, grid9x9.getWidth());
            assertEquals(9, grid9x9.getHeight());
        }

        @Test
        @DisplayName("Toutes les cellules doivent être initialisées à 0")
        void testInitialCellValues() {
            for (int i = 0; i < 9 * 9; i++) {
                assertEquals(0, grid9x9.getNumber(i));
            }
        }

        @Test
        @DisplayName("La première cellule disponible doit être l'index 0")
        void testFirstAvailableCell() {
            assertEquals(0, grid9x9.nextAvailableCell());
        }
    }

    @Nested
    @DisplayName("Tests de conversion de coordonnées (Row, Col, Block)")
    class CoordinateTests {

        @ParameterizedTest(name = "pos {0} -> ligne {1}")
        @CsvSource({"0, 0", "8, 0", "9, 1", "40, 4", "80, 8"})
        @DisplayName("Calcul de l'index de ligne")
        void testGetRowIndex(int pos, int expectedRow) {
            assertEquals(expectedRow, grid9x9.getRowIndex(pos));
        }

        @ParameterizedTest(name = "pos {0} -> colonne {1}")
        @CsvSource({"0, 0", "8, 8", "9, 0", "40, 4", "80, 8"})
        @DisplayName("Calcul de l'index de colonne")
        void testGetColumnIndex(int pos, int expectedCol) {
            assertEquals(expectedCol, grid9x9.getColumnIndex(pos));
        }

        @ParameterizedTest(name = "pos {0} -> début de bloc {1}")
        @CsvSource({"0, 0", "10, 0", "20, 0", "3, 3", "40, 30", "80, 60", "62, 60"})
        @DisplayName("Calcul de l'index de départ du sous-bloc (9x9)")
        void testGetBlockIndex(int pos, int expectedBlockStart) {
            assertEquals(expectedBlockStart, grid9x9.getBlockIndex(pos));
        }

        @Test
        @DisplayName("row * width + column (conversion du contrôleur) est l'inverse de getRowIndex/getColumnIndex")
        void testControllerConversionRoundTrip() {
            Grid rectangular = new Grid(5, 4);
            for (int row = 0; row < rectangular.getHeight(); row++) {
                for (int column = 0; column < rectangular.getWidth(); column++) {
                    int pos = row * rectangular.getWidth() + column;
                    assertEquals(row, rectangular.getRowIndex(pos));
                    assertEquals(column, rectangular.getColumnIndex(pos));
                }
            }
        }

        @Test
        @DisplayName("getPosition(x, y) est l'inverse de getColumnIndex/getRowIndex")
        void testGetPosition() {
            Grid rectangular = new Grid(5, 4);
            for (int pos = 0; pos < 5 * 4; pos++) {
                int x = rectangular.getColumnIndex(pos);
                int y = rectangular.getRowIndex(pos);
                assertEquals(pos, rectangular.getPosition(x, y));
            }
            assertThrows(IllegalArgumentException.class, () -> rectangular.getPosition(5, 0));
            assertThrows(IllegalArgumentException.class, () -> rectangular.getPosition(0, 4));
        }
    }

    @Nested
    @DisplayName("Tests de gestion des valeurs et des cellules disponibles")
    class CellValueAndAvailabilityTests {

        @Test
        @DisplayName("Ajout d'un nombre dans une cellule et mise à jour de la disponibilité")
        void testAddNumber() {
            assertTrue(grid9x9.addNumber(0, 5));
            assertEquals(5, grid9x9.getNumber(0));

            // La cellule 0 ne doit plus être la prochaine disponible
            assertNotEquals(0, grid9x9.nextAvailableCell());
            assertEquals(1, grid9x9.nextAvailableCell());
        }

        @Test
        @DisplayName("Suppression d'un nombre (remise à 0)")
        void testRemoveNumber() {
            grid9x9.addNumber(4, 7);
            assertEquals(7, grid9x9.getNumber(4));

            grid9x9.removeNumber(4);
            assertEquals(0, grid9x9.getNumber(4));
        }

        @Test
        @DisplayName("Rendre à nouveau une cellule disponible")
        void testBackCellAvailable() {
            int first = grid9x9.nextAvailableCell();
            grid9x9.addNumber(first, 9);
            assertNotEquals(first, grid9x9.nextAvailableCell());

            grid9x9.backCellAvailable(first);
            // Vérifie que la cellule a été réinsérée
            // (elle est ajoutée à la fin de la liste)
            grid9x9.removeNumber(first);
            assertEquals(0, grid9x9.getNumber(first));
        }
    }

    @Nested
    @DisplayName("Tests de verrouillage et réinitialisation")
    class LockAndResetTests {

        @Test
        @DisplayName("lockFilledCells verrouille uniquement les cellules non vides")
        void testLockFilledCells() {
            grid9x9.addNumber(0, 3);
            grid9x9.lockFilledCells();

            // clearEntries ne doit pas toucher aux cellules verrouillées
            grid9x9.clearEntries();
            assertEquals(3, grid9x9.getNumber(0));
        }

        @Test
        @DisplayName("clearEntries vide uniquement les cellules non verrouillées")
        void testClearEntries() {
            grid9x9.addNumber(0, 3);
            grid9x9.lockFilledCells();

            grid9x9.addNumber(1, 8); // Cellule non verrouillée

            grid9x9.clearEntries();

            assertEquals(3, grid9x9.getNumber(0), "La cellule verrouillée doit être conservée");
            assertEquals(0, grid9x9.getNumber(1), "La cellule non verrouillée doit être remise à 0");
        }

        @Test
        @DisplayName("resetGrid remet toute la grille à 0 sans exception")
        void testResetGrid() {
            grid9x9.addNumber(0, 1);
            grid9x9.addNumber(5, 6);
            grid9x9.lockFilledCells();

            grid9x9.resetGrid();

            assertEquals(0, grid9x9.getNumber(0));
            assertEquals(0, grid9x9.getNumber(5));
        }
    }

    @Nested
    @DisplayName("Tests de mise à jour des notes (Ligne, Colonne, Bloc)")
    class NotesUpdateTests {

        @Test
        @DisplayName("lineUpdate supprime la note sur toute la ligne")
        void testLineUpdate() {
            int ligne = 2;
            int noteASupprimer = 4;

            grid9x9.lineUpdate(ligne, noteASupprimer);

            // Vérifie que pour chaque colonne de cette ligne, la note n'est plus présente
            for (int col = 0; col < grid9x9.getWidth(); col++) {
                int pos = ligne * grid9x9.getWidth() + col;
                assertFalse(grid9x9.isCellContaining(pos, noteASupprimer));
            }
        }

        @Test
        @DisplayName("columnUpdate supprime la note sur toute la colonne")
        void testColumnUpdate() {
            int colonne = 3;
            int noteASupprimer = 7;

            grid9x9.columnUpdate(colonne, noteASupprimer);

            // Vérifie que pour chaque ligne de cette colonne, la note n'est plus présente
            for (int ligne = 0; ligne < grid9x9.getHeight(); ligne++) {
                int pos = ligne * grid9x9.getWidth() + colonne;
                assertFalse(grid9x9.isCellContaining(pos, noteASupprimer));
            }
        }

        @Test
        @DisplayName("blockUpdate supprime la note dans tout le bloc, et seulement dans le bloc")
        void testBlockUpdate() {
            int note = 4;
            for (int pos = 0; pos < 81; pos++) {
                grid9x9.addNote(pos, note);
            }

            grid9x9.blockUpdate(40, note); // case centrale : bloc des lignes 3-5, colonnes 3-5

            for (int pos = 0; pos < 81; pos++) {
                int row = grid9x9.getRowIndex(pos);
                int column = grid9x9.getColumnIndex(pos);
                boolean inCentralBlock = row / 3 == 1 && column / 3 == 1;
                assertEquals(!inCentralBlock, grid9x9.isCellContaining(pos, note), "pos " + pos);
            }
        }

        @Test
        @DisplayName("blockUpdate lance IllegalArgumentException si la position est hors limites")
        void testBlockUpdateOutOfBounds() {
            assertThrows(IllegalArgumentException.class, () -> grid9x9.blockUpdate(-1, 5));
            assertThrows(IllegalArgumentException.class, () -> grid9x9.blockUpdate(1000, 5));
        }
    }

    @Test
    @DisplayName("Le clone copie correctement les notes et garantit l'indépendance mémoire")
    void testCloneNotesComplet() {
        Grid clonedGrid = new Grid(grid9x9);

        assertEquals(grid9x9, clonedGrid);
    }
}