package fr.univpoitiers.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SudokuCoreModelTest {

    private SudokuCoreModel model;

    @BeforeEach
    void setUp() {
        model = new SudokuCoreModel();
        model.createGrid(9, 9);
    }

    @Test
    @DisplayName("input puis getNumber relisent la même case (x = colonne, y = ligne)")
    void inputAndGetNumberUseBothCoordinates() {
        assertTrue(model.input(5, 2, 7));

        assertEquals(5, model.getNumber(2, 7));
        assertEquals(0, model.getNumber(7, 2), "(x, y) et (y, x) sont deux cases différentes");
        assertEquals(0, model.getNumber(2, 0), "le second argument (ligne) ne doit pas être ignoré");
    }

    @Test
    @DisplayName("Chaque case de la grille est indépendante")
    void everyCellIsDistinct() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                model.input((x + y) % 9 + 1, x, y);
            }
        }
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                assertEquals((x + y) % 9 + 1, model.getNumber(x, y), "case (" + x + ", " + y + ")");
            }
        }
    }

    @Test
    @DisplayName("Des coordonnées hors de la grille sont refusées")
    void rejectsOutOfGridCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> model.getNumber(9, 0));
        assertThrows(IllegalArgumentException.class, () -> model.getNumber(0, 9));
        assertThrows(IllegalArgumentException.class, () -> model.input(1, -1, 0));
    }

    @Test
    @DisplayName("unlockGrid rend de nouveau modifiables les cases verrouillées")
    void unlockGridMakesLockedCellsEditableAgain() {
        model.input(4, 0, 0);
        model.lockFilledCells();
        assertTrue(model.isCellLocked(0, 0));

        model.unlockGrid();

        assertFalse(model.isGridLocked());
        assertFalse(model.isCellLocked(0, 0));
        assertTrue(model.input(7, 0, 0));
        assertEquals(7, model.getNumber(0, 0));
    }

    @Test
    @DisplayName("toggleNote ajoute puis retire la note du chiffre demandé, de 1 à 9")
    void toggleNoteAddsAndRemovesTheRequestedDigit() {
        for (int digit = 1; digit <= 9; digit++) {
            model.toggleNote(digit, 3, 4);

            for (int other = 1; other <= 9; other++) {
                assertEquals(other == digit, model.getNote(other, 3, 4),
                        "après ajout de " + digit + ", note " + other);
            }

            model.toggleNote(digit, 3, 4);

            for (int other = 1; other <= 9; other++) {
                assertFalse(model.getNote(other, 3, 4),
                        "après retrait de " + digit + ", note " + other);
            }
        }
    }
}
