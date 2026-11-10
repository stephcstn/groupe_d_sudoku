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
    private Grid grid;

    private void populateGrid(Grid targetGrid, int[] values) {
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

        @Test
        @DisplayName("Calcul de l'index de ligne")
        void testGetRowIndex() {
            assertEquals(0, grid9x9.getRowIndex(0));
            assertEquals(0, grid9x9.getRowIndex(8));
            assertEquals(1, grid9x9.getRowIndex(9));
            assertEquals(2, grid9x9.getRowIndex(20));
            assertEquals(8, grid9x9.getRowIndex(80));
        }

        @Test
        @DisplayName("Calcul de l'index de colonne")
        void testGetColumnIndex() {
            assertEquals(0, grid9x9.getColumnIndex(0));
            assertEquals(8, grid9x9.getColumnIndex(8));
            assertEquals(0, grid9x9.getColumnIndex(9));
            assertEquals(2, grid9x9.getColumnIndex(20));
            assertEquals(8, grid9x9.getColumnIndex(80));
        }

        @Test
        @DisplayName("Calcul de l'index de départ du sous-bloc (9x9)")
        void testGetBlockIndex() {
            assertEquals(0, grid9x9.getBlockIndex(0));
            assertEquals(0, grid9x9.getBlockIndex(2));
            assertEquals(0, grid9x9.getBlockIndex(10));
            assertEquals(3, grid9x9.getBlockIndex(4));
            assertEquals(0, grid9x9.getBlockIndex(20));
            assertEquals(30, grid9x9.getBlockIndex(40));
            assertEquals(60, grid9x9.getBlockIndex(80));
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
            assertNotEquals(0, grid9x9.nextAvailableCell());
            assertEquals(1, grid9x9.nextAvailableCell());
        }

        @Test
        @DisplayName("Suppression d'un nombre et réinsertion ordonnée")
        void testRemoveNumber() {
            grid9x9.addNumber(4, 7);
            assertEquals(7, grid9x9.getNumber(4));

            grid9x9.removeNumber(4);
            assertEquals(0, grid9x9.getNumber(4));
            // La cellule 4 doit être redevenue la suivante disponible (avant les cellules > 4)
            assertEquals(0, grid9x9.nextAvailableCell());
        }

        @Test
        @DisplayName("Rendre à nouveau une cellule disponible sans doublons")
        void testBackCellAvailable() {
            int first = grid9x9.nextAvailableCell();
            grid9x9.addNumber(first, 9);
            assertNotEquals(first, grid9x9.nextAvailableCell());

            grid9x9.backCellAvailable(first);
            assertEquals(first, grid9x9.nextAvailableCell());
        }
    }

    @Nested
    @DisplayName("Validation des contraintes de placement")
    class ValidationConstraintsTests {

        @Test
        @DisplayName("checkRow: faux si la valeur est déjà présente sur la ligne")
        void checkRow_whenValueExistsInRow_shouldReturnFalse() {
            assertFalse(grid.checkRow(2, 5));
            assertFalse(grid.checkRow(2, 3));
            assertFalse(grid.checkRow(2, 7));
        }

        @Test
        @DisplayName("checkRow: vrai si la valeur est absente de la ligne")
        void checkRow_whenValueDoesNotExistInRow_shouldReturnTrue() {
            assertTrue(grid.checkRow(2, 1));
        }

        @Test
        @DisplayName("checkColumn: faux si la valeur est déjà présente sur la colonne")
        void checkColumn_whenValueExistsInColumn_shouldReturnFalse() {
            assertFalse(grid.checkColumn(2, 8));
        }

        @Test
        @DisplayName("checkColumn: vrai si la valeur est absente de la colonne")
        void checkColumn_whenValueDoesNotExistInColumn_shouldReturnTrue() {
            assertTrue(grid.checkColumn(2, 4));
        }

        @Test
        @DisplayName("checkBlock: faux si la valeur est déjà présente dans le bloc 3x3")
        void checkBlock_whenValueExistsInBlock_shouldReturnFalse() {
            assertFalse(grid.checkBlock(2, 6));
        }

        @Test
        @DisplayName("checkBlock: vrai si la valeur est dans un autre bloc mais pas celui-ci")
        void checkBlock_whenValueExistsInAdjacentBlock_shouldReturnTrue() {
            assertTrue(grid.checkBlock(2, 7));
        }

        @Test
        @DisplayName("checkValidValues: faux si seule la ligne est en conflit")
        void checkValidValues_whenOnlyRowViolated_shouldReturnFalse() {
            // Case 2 : 7 est sur la ligne 0, mais ni dans la colonne 2 ni dans le bloc
            assertFalse(grid.checkValidValues(2, 7));
        }

        @Test
        @DisplayName("checkValidValues: faux si seule la colonne est en conflit")
        void checkValidValues_whenOnlyColumnViolated_shouldReturnFalse() {
            // Case 22 = (x=4, y=2) : 2 est dans la colonne 4, mais ni sur la ligne 2 ni dans le bloc
            assertTrue(grid.checkBlock(22, 2));
            assertTrue(grid.checkRow(22, 2));
            assertFalse(grid.checkValidValues(22, 2));
        }

        @Test
        @DisplayName("checkValidValues: faux si le bloc est en conflit")
        void checkValidValues_whenBlockViolated_shouldReturnFalse() {
            // Case 2 : 8 est dans le bloc (et dans la colonne 2)
            assertFalse(grid.checkValidValues(2, 8));
        }

        @Test
        @DisplayName("checkValidValues: vrai si ligne, colonne et bloc sont respectés")
        void checkValidValues_whenAllConstraintsSatisfied_shouldReturnTrue() {
            assertTrue(grid.checkValidValues(2, 1));
        }

        @Test
        @DisplayName("checkValidValues: faux pour une valeur hors bornes (0 = case vide, > 9)")
        void checkValidValues_whenValueOutOfRange_shouldReturnFalse() {
            assertFalse(grid.checkValidValues(2, 0));
            assertFalse(grid.checkValidValues(2, 10));
            assertFalse(grid.checkValidValues(2, -1));
        }

        @Test
        @DisplayName("checkValidValues: une case remplie n'entre pas en conflit avec elle-même")
        void checkValidValues_whenCheckingOwnValue_shouldIgnoreTheCellItself() {
            // Case 0 contient 5, absent du reste de la ligne 0, de la colonne 0 et du bloc
            assertTrue(grid.checkValidValues(0, 5));
        }

        @Test
        @DisplayName("checkValidValues: fonctionne sur une grille 4x4 à blocs 2x2")
        void checkValidValues_onSmallGrid_shouldUseGridAndBlockSize() {
            Grid small = new Grid(4, 4);
            small.addNumber(0, 1);

            assertFalse(small.checkValidValues(5, 1), "même bloc 2x2");
            assertFalse(small.checkValidValues(3, 1), "même ligne");
            assertFalse(small.checkValidValues(12, 1), "même colonne");
            assertTrue(small.checkValidValues(10, 1), "autre ligne, colonne et bloc");
            assertFalse(small.checkValidValues(10, 5), "5 est hors bornes en 4x4");
            assertTrue(small.checkValidValues(10, 4), "4 est la valeur max en 4x4");
        }
    }

    @Nested
    @DisplayName("Tests de verrouillage et réinitialisation")
    class LockAndResetTests {

        @Test
        @DisplayName("clearEntries vide uniquement les cellules non verrouillées et met à jour availableCells")
        void testClearEntries() {
            grid9x9.addNumber(0, 3);
            grid9x9.lockFilledCells();

            grid9x9.addNumber(1, 8);
            grid9x9.clearEntries();

            assertEquals(3, grid9x9.getNumber(0), "La cellule verrouillée doit être conservée");
            assertEquals(0, grid9x9.getNumber(1), "La cellule non verrouillée doit être remise à 0");
            assertEquals(1, grid9x9.nextAvailableCell(), "La cellule 1 doit redevenir disponible");
        }

        @Test
        @DisplayName("resetGrid réinitialise tout le tableau et recharge availableCells")
        void testResetGrid() {
            grid9x9.addNumber(0, 1);
            grid9x9.addNumber(5, 6);
            grid9x9.lockFilledCells();

            grid9x9.resetGrid();

            assertEquals(0, grid9x9.getNumber(0));
            assertEquals(0, grid9x9.getNumber(5));
            assertEquals(0, grid9x9.nextAvailableCell(), "Index 0 doit être disponible après reset");
        }
    }

    @Nested
    @DisplayName("Tests de mise à jour des notes et contraintes diagonales")
    class NotesUpdateTests {

        @Test
        @DisplayName("lineUpdate supprime la note sur toute la ligne")
        void testLineUpdate() {
            int ligne = 2;
            int noteASupprimer = 4;
            grid9x9.lineUpdate(ligne, noteASupprimer);

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

            for (int ligne = 0; ligne < grid9x9.getHeight(); ligne++) {
                int pos = ligne * grid9x9.getWidth() + colonne;
                assertFalse(grid9x9.isCellContaining(pos, noteASupprimer));
            }
        }

        @Test
        @DisplayName("blockUpdate supprime la note sur tout le bloc")
        void testBlockUpdate() {
            grid9x9.blockUpdate(0, 5); // bloc haut-gauche
            assertFalse(grid9x9.isCellContaining(0, 5));
            assertFalse(grid9x9.isCellContaining(1, 5));
            assertFalse(grid9x9.isCellContaining(10, 5));
        }

        @Test
        @DisplayName("blockUpdate sur le bloc central retire la note seulement dans ce bloc")
        void testBlockUpdateOnCenterBlock() throws Exception {
            // Toutes les cases reçoivent la note 4 (champ privé : accès par réflexion)
            java.lang.reflect.Field f = Grid.class.getDeclaredField("cells");
            f.setAccessible(true);
            for (Cell c : (Cell[]) f.get(grid9x9)) {
                c.setNote(4);
            }

            grid9x9.blockUpdate(40, 4); // (x=4, y=4) : bloc central

            for (int pos = 0; pos < 81; pos++) {
                boolean inCenterBlock = grid9x9.getBlockIndex(pos) == 30;
                assertEquals(!inCenterBlock, grid9x9.isCellContaining(pos, 4), "pos " + pos);
            }
        }

        @Test
        @DisplayName("blockUpdate lance IllegalArgumentException si la position est hors limites")
        void testBlockUpdateOutOfBounds() {
            assertThrows(IllegalArgumentException.class, () -> grid9x9.blockUpdate(-1, 5));
            assertThrows(IllegalArgumentException.class, () -> grid9x9.blockUpdate(1000, 5));
        }

        @Test
        @DisplayName("diagonalUpdate supprime la note sur la diagonale principale et anti-diagonale")
        void testDiagonalUpdate() {
            grid9x9.diagonalUpdate(0, 3); // (0,0) diagonale principale
            assertFalse(grid9x9.isCellContaining(0, 3));
            assertFalse(grid9x9.isCellContaining(10, 3)); // (1,1)

            grid9x9.diagonalUpdate(8, 4); // (8,0) anti-diagonale
            assertFalse(grid9x9.isCellContaining(8, 4));
            assertFalse(grid9x9.isCellContaining(16, 4)); // (7,1)
        }
    }

    @Test
    @DisplayName("Le clone copie correctement les notes et garantit l'indépendance mémoire")
    void testCloneNotesComplet() {
        Grid clonedGrid = new Grid(grid9x9);

        assertEquals(grid9x9, clonedGrid);
        assertEquals(grid9x9.hashCode(), clonedGrid.hashCode());

        // Modification de la grille d'origine : ne doit pas impacter le clone
        grid9x9.addNumber(0, 9);
        assertNotEquals(grid9x9.getNumber(0), clonedGrid.getNumber(0), "Le clone doit être indépendant en mémoire");
    }
}