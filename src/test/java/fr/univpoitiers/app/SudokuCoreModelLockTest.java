package fr.univpoitiers.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SudokuCoreModelLockTest {

    @Test
    void lockingProtectsExistingValuesButKeepsEmptyCellsEditable() {
        SudokuCoreModel model = new SudokuCoreModel();
        model.createGrid(9, 9);
        assertTrue(model.input(4, 0, 0));

        model.lockFilledCells();

        assertTrue(model.isGridLocked());
        assertFalse(model.input(7, 0, 0));
        assertEquals(4, model.getNumber(0, 0));
        assertTrue(model.input(7, 1, 0));

        model.unlockGrid();

        assertFalse(model.isGridLocked());
        assertTrue(model.input(7, 0, 0));
        assertEquals(7, model.getNumber(0, 0));
    }

    @Test
    void resetClearsAndUnlocksTheGrid() {
        SudokuCoreModel model = new SudokuCoreModel();
        model.createGrid(9, 9);
        assertTrue(model.input(3, 0, 0));
        model.lockFilledCells();

        model.resetGrid();

        assertFalse(model.isGridLocked());
        assertEquals(0, model.getNumber(0, 0));
        assertTrue(model.input(8, 0, 0));
    }
}
