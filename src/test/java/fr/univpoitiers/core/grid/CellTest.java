package fr.univpoitiers.core.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CellTest {

	@Test
	void notesAreIndependentAndCanBeRemoved() {
		Cell cell = new Cell();

		cell.setNote(2);
		cell.setNote(7);
		cell.removeNote(2);

		assertFalse(cell.hasNote(2));
		assertTrue(cell.hasNote(7));
	}

	@Test
	void notesMustBeWithinSudokuValueRange() {
		Cell cell = new Cell();

		assertThrows(IllegalArgumentException.class, () -> cell.setNote(0));
		assertThrows(IllegalArgumentException.class, () -> cell.setNote(10));
		assertThrows(IllegalArgumentException.class, () -> cell.removeNote(0));
		assertThrows(IllegalArgumentException.class, () -> cell.hasNote(10));
	}

	@Test
	void candidateRangeCanMatchGridSize() {
		Cell cell = new Cell(25);
		cell.setNote(25);
		assertTrue(cell.hasNote(25));
		assertThrows(IllegalArgumentException.class, () -> cell.setNote(26));
		cell.setValue(25);
		assertEquals(25, cell.getValue());
		assertFalse(cell.hasNote(25));
		assertThrows(IllegalArgumentException.class, () -> new Cell(0));
	}

	@Test
	void settingAndClearingValueRemovesNotes() {
		Cell cell = new Cell();
		cell.setNote(4);

		cell.setValue(6);

		assertEquals(6, cell.getValue());
		assertFalse(cell.hasNote(4));
		cell.setNote(8);
		assertFalse(cell.hasNote(8));

		cell.setValue(0);
		cell.setNote(3);
		cell.setValue(0);

		assertEquals(0, cell.getValue());
		assertFalse(cell.hasNote(3));
	}

	@Test
	void lockedCellCannotBeChanged() {
		Cell cell = new Cell();
		cell.setValue(5);
		cell.lock();

		cell.setValue(2);
		cell.setValue(0);
		cell.setNote(4);

		assertTrue(cell.isLocked());
		assertEquals(5, cell.getValue());
		assertFalse(cell.hasNote(4));

		Cell notesCell = new Cell();
		notesCell.setNote(4);
		notesCell.lock();
		notesCell.removeNote(4);
		notesCell.setNote(6);
		assertTrue(notesCell.hasNote(4));
		assertFalse(notesCell.hasNote(6));
	}

	@Test
	void resetClearsAndUnlocksCell() {
		Cell cell = new Cell();
		cell.setNote(4);
		cell.setValue(5);
		cell.lock();

		cell.reset();

		assertEquals(0, cell.getValue());
		assertFalse(cell.hasNote(4));
		assertFalse(cell.isLocked());
		cell.setNote(6);
		assertTrue(cell.hasNote(6));
	}
}
