package fr.univpoitiers.core.grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private static final int NB_VARIABLES = 9;
    private Cell cell;

    @BeforeEach
    void setUp() {
        cell = new Cell(NB_VARIABLES);
    }

    @Nested
    @DisplayName("Tests d'initialisation")
    class InitializationTests {

        @Test
        @DisplayName("La valeur initiale doit être 0")
        void testDefaultValue() {
            assertEquals(0, cell.getValue());
        }

        @Test
        @DisplayName("La cellule ne doit pas être verrouillée à la création")
        void testDefaultLockedState() {
            assertFalse(cell.isLocked());
        }

        @Test
        @DisplayName("Toutes les notes doivent être initialisées à 0")
        void testDefaultNotes() {
            assertEquals(0, cell.count());
            for (int i = 0; i < NB_VARIABLES; i++) {
                assertEquals(0, cell.getNote(i));
            }
        }
    }

    @Nested
    @DisplayName("Tests de gestion des valeurs")
    class ValueTests {

        @Test
        @DisplayName("setValue et getValue fonctionnent correctement")
        void testSetAndGetValue() {
            cell.setValue(5);
            assertEquals(5, cell.getValue());
        }

        @Test
        @DisplayName("Remplacement d'une valeur existante")
        void testOverwriteVal() {
            cell.setValue(2);
            cell.setValue(8);
            assertEquals(8, cell.getValue());
        }
    }

    @Nested
    @DisplayName("Tests de verrouillage (lock)")
    class LockTests {

        @Test
        @DisplayName("Verrouillage de la cellule")
        void testLock() {
            cell.lock();
            assertTrue(cell.isLocked());
        }

        @Test
        @DisplayName("Appels successifs à lock() conservent l'état verrouillé")
        void testIdempotentLock() {
            cell.lock();
            cell.lock();
            assertTrue(cell.isLocked());
        }
    }

    @Nested
    @DisplayName("Tests des notes")
    class NoteTests {

        @Test
        @DisplayName("setNote active la note et la rend lisible")
        void testSetAndGetNote() {
            cell.setNote(3);
            assertEquals(1, cell.getNote(3));
            assertEquals(0, cell.getNote(4));
        }

        @Test
        @DisplayName("removeNote remet la note à 0")
        void testRemoveNote() {
            cell.setNote(5);
            assertEquals(1, cell.getNote(5));

            cell.removeNote(5);
            assertEquals(0, cell.getNote(5));
        }

        @Test
        @DisplayName("removeNote sur une note inactive ne provoque pas d'erreur")
        void testRemoveNoteWhenNotSet() {
            cell.removeNote(2);
            assertEquals(0, cell.getNote(2));
        }

        @Test
        @DisplayName("count compte précisément le nombre de notes actives")
        void testCountNotes() {
            assertEquals(0, cell.count());

            cell.setNote(0);
            cell.setNote(3);
            cell.setNote(8);
            assertEquals(3, cell.count());

            // Réassigner une note déjà présente ne change pas le décompte
            cell.setNote(0);
            assertEquals(3, cell.count());

            cell.removeNote(3);
            assertEquals(2, cell.count());
        }

        @Test
        @DisplayName("Débordement d'indice lève IndexOutOfBoundsException")
        void testNoteOutOfBounds() {
            // Indice négatif
            assertThrows(IndexOutOfBoundsException.class, () -> cell.getNote(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> cell.setNote(-1));

            // Indice égal à la taille (rappel : indices de 0 à NB_VARIABLES - 1)
            assertThrows(IndexOutOfBoundsException.class, () -> cell.getNote(NB_VARIABLES));
            assertThrows(IndexOutOfBoundsException.class, () -> cell.setNote(NB_VARIABLES));
        }
    }
}