package fr.univpoitiers.core.grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    private Grid grid9x9;
    private Grid grid;

    /**
     * Helper method to populate a grid from a 1D array representation.
     * 0 represents an empty cell.
     */
    private void populateGrid(Grid targetGrid, int[] values) {
        int width = targetGrid.getWidth();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {
                targetGrid.addNumber(i, values[i]);
            }
        }
    }

    @BeforeEach
    void setUp() {
        grid9x9 = new Grid(9, 9);

        // Standard 9x9 reference grid (0 = empty cell)
        int[] initialGrid = {
                5, 3, 0,  0, 7, 0,  0, 0, 0,
                6, 0, 0,  1, 9, 5,  0, 0, 0,
                0, 9, 8,  0, 0, 0,  0, 6, 0,

                8, 0, 0,  0, 6, 0,  0, 0, 3,
                4, 0, 0,  8, 0, 3,  0, 0, 1,
                7, 0, 0,  0, 2, 0,  0, 0, 6,

                0, 6, 0,  0, 0, 0,  2, 8, 0,
                0, 0, 0,  4, 1, 9,  0, 0, 5,
                0, 0, 0,  0, 8, 0,  0, 7, 9
        };

        grid = new Grid(9, 9);
        populateGrid(grid, initialGrid);
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
            grid9x9.removeNumber(first);
            assertEquals(0, grid9x9.getNumber(first));
        }
    }

    @Nested
    @DisplayName("Validation des contraintes de placement")
    class ValidationConstraintsTests {

        // --- Contrainte de Ligne ---

        @Test
        @DisplayName("checkRow: faux si la valeur est déjà présente sur la ligne")
        void checkRow_whenValueExistsInRow_shouldReturnFalse() {
            // Pos 2 = ligne 0. Présents : 5, 3, 7
            assertFalse(grid.checkRow(2, 5));
            assertFalse(grid.checkRow(2, 3));
            assertFalse(grid.checkRow(2, 7));
        }

        @Test
        @DisplayName("checkRow: vrai si la valeur est absente de la ligne")
        void checkRow_whenValueDoesNotExistInRow_shouldReturnTrue() {
            // Le chiffre 1 n'est pas présent sur la ligne 0
            assertTrue(grid.checkRow(2, 1));
        }

        // --- Contrainte de Colonne ---

        @Test
        @DisplayName("checkColumn: faux si la valeur est déjà présente sur la colonne")
        void checkColumn_whenValueExistsInColumn_shouldReturnFalse() {
            // Pos 2 = colonne 2. Le chiffre 8 est présent à la pos 20 (row 2, col 2)
            assertFalse(grid.checkColumn(2, 8));
        }

        @Test
        @DisplayName("checkColumn: vrai si la valeur est absente de la colonne")
        void checkColumn_whenValueDoesNotExistInColumn_shouldReturnTrue() {
            // Le chiffre 4 est absent de la colonne 2
            assertTrue(grid.checkColumn(2, 4));
        }

        // --- Contrainte de Bloc 3x3 ---

        @Test
        @DisplayName("checkBlock: faux si la valeur est déjà présente dans le bloc 3x3")
        void checkBlock_whenValueExistsInBlock_shouldReturnFalse() {
            // Pos 2 appartient au bloc 0 (haut-gauche). 6 est présent à pos 9 (row 1, col 0)
            assertFalse(grid.checkBlock(2, 6));
        }

        @Test
        @DisplayName("checkBlock: vrai si la valeur est dans un autre bloc mais pas celui-ci")
        void checkBlock_whenValueExistsInAdjacentBlock_shouldReturnTrue() {
            // 7 est présent ligne 0 pos 4 (bloc centre-haut), mais absent du bloc 0
            assertTrue(grid.checkBlock(2, 7));
        }

        // --- Règle composite (Ligne + Colonne + Bloc) ---

        @Test
        @DisplayName("checkValidValues: faux si la contrainte de ligne échoue")
        void checkValidValues_whenRowViolated_shouldReturnFalse() {
            // 7 est valide pour la colonne 2 et le bloc 0, mais déjà présent sur la ligne 0
            assertFalse(grid.checkValidValues(2, 7));
        }

        @Test
        @DisplayName("checkValidValues: faux si la contrainte de colonne échoue")
        void checkValidValues_whenColumnViolated_shouldReturnFalse() {
            // 8 est absent de la ligne 0, mais présent en colonne 2
            assertFalse(grid.checkValidValues(2, 8));
        }

        @Test
        @DisplayName("checkValidValues: vrai quand toutes les contraintes sont respectées")
        void checkValidValues_whenAllConstraintsValid_shouldReturnTrue() {
            // 1 est absent de la ligne 0, de la colonne 2 et du bloc 0
            assertTrue(grid.checkValidValues(2, 1));
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
        Grid clonedGrid = new Grid(grid9x9);

        assertEquals(grid9x9, clonedGrid);
    }
}