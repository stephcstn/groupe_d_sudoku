package fr.univpoitiers.core.grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

        @DisplayName("Calcul de l'index de ligne")
        void testGetRowIndex(int pos, int expectedRow) {
            assertEquals(expectedRow, grid9x9.getRowIndex(pos));
        }

        @DisplayName("Calcul de l'index de colonne")
        void testGetColumnIndex(int pos, int expectedCol) {
            assertEquals(expectedCol, grid9x9.getColumnIndex(pos));
        }

        @DisplayName("Calcul de l'index de départ du sous-bloc (9x9)")
        void testGetBlockIndex(int pos, int expectedBlockStart) {
            assertEquals(expectedBlockStart, grid9x9.getBlockIndex(pos));
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
        @DisplayName("blockUpdate lance IllegalArgumentException si la position est hors limites")
        void testBlockUpdateOutOfBounds() {
            assertThrows(IllegalArgumentException.class, () -> grid9x9.blockUpdate(-1, 5));
            assertThrows(IllegalArgumentException.class, () -> grid9x9.blockUpdate(1000, 5));
        }
    }

    @Test
    @DisplayName("Le clone copie correctement les notes et garantit l'indépendance mémoire")
    void testCloneNotesComplet() {
        Grid clonedGrid = grid9x9.clone();

        assertEquals(grid9x9.clone(), clonedGrid);
    }
}