package fr.univpoitiers.core;

import fr.univpoitiers.core.grid.Cell;
import fr.univpoitiers.core.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    private Grid grid;

    /**
     * Instancie Grid sans appeler son constructeur et injecte les champs.
     */
    private Grid createGridWithoutConstructor(int width, int height, int[] initialValues) throws Exception {
        // 1. Instanciation sans appel au constructeur de Grid
        ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
        Constructor<?> objectConstructor = Object.class.getDeclaredConstructor();
        Constructor<?> gridConstructor = rf.newConstructorForSerialization(Grid.class, objectConstructor);
        Grid instance = (Grid) gridConstructor.newInstance();

        setField(instance, "WIDTH", width);
        setField(instance, "HEIGHT", height);

        Cell[] cells = new Cell[width * height];
        for (int i = 0; i < initialValues.length; i++) {
            if (initialValues[i] != 0) {
                Cell cell = new Cell();
                cell.setValue(initialValues[i]);
                cells[i] = cell;
            }
        }
        setField(instance, "cells", cells);

        return instance;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = Grid.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @BeforeEach
    void setUp() throws Exception {
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

        grid = createGridWithoutConstructor(9, 9, initialGrid);
    }

    @Test
    void checkRow_whenValueAlreadyExistsInRow_shouldReturnFalse() {
        assertFalse(grid.checkRow(2, 5));
        assertFalse(grid.checkRow(2, 3));
    }

    @Test
    void checkRow_whenValueDoesNotExistInRow_shouldReturnTrue() {
        assertTrue(grid.checkRow(2, 1));
    }

    @Test
    void checkColumn_whenValueAlreadyExistsInColumn_shouldReturnFalse() {
        assertFalse(grid.checkColumn(2, 8));
    }

    @Test
    void checkBlock_whenValueAlreadyExistsInBlock_shouldReturnFalse() {
        assertFalse(grid.checkBlock(2, 6));
    }

    @Test
    void checkValidValues_whenAllConstraintsAreSatisfied_shouldReturnTrue() {
        assertTrue(grid.checkValidValues(2, 1));
    }
}