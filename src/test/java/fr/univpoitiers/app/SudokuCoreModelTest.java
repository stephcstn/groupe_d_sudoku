package fr.univpoitiers.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SudokuCoreModelTest {

	/*
	@Test
	void entriesNotesAndResetUseTheSameGrid() {
		SudokuCoreModel model = new SudokuCoreModel();
		model.createGrid(9, 9);
		model.toggleNote(2, 0, 0);
		assertTrue(model.getCell(0, 0).hasNote(2));
		assertFalse(model.getCell(1, 0).hasNote(2));
		model.toggleNote(2, 0, 0);
		assertFalse(model.getCell(0, 0).hasNote(2));
		model.toggleNote(7, 0, 0);
		assertTrue(model.input(2, 0, 0));
		assertFalse(model.getCell(0, 0).hasNote(7));
		model.toggleNote(7, 0, 0);
		assertFalse(model.getCell(0, 0).hasNote(7));
		model.getCell(0, 0).lock();
		assertFalse(model.input(0, 0, 0));
		assertEquals(2, model.getCell(0, 0).getValue());
		model.resetGrid();
		assertEquals(0, model.getCell(0, 0).getValue());
		assertFalse(model.getCell(0, 0).isLocked());
	}

	@Test
	void dimensionsAndValuesAreValidated() {
		SudokuCoreModel model = new SudokuCoreModel();
		assertThrows(IllegalArgumentException.class, () -> model.createGrid(0, 9));
		model.createGrid(4, 4);
		assertThrows(IllegalArgumentException.class, () -> model.input(5, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> model.toggleNote(0, 0, 0));
	}

	 */
}