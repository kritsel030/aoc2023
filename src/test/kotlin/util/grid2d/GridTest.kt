package util.grid2d

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GridTest {

    val INPUT = listOf(
        "abcde",
        "ghijk",
        "lmnop",
        "qrstu"
    )
    val GRID_1_INDEX = Grid2DFactory.initMutableCharGrid(INPUT, 1)
    val GRID_0_INDEX = Grid2DFactory.initMutableCharGrid(INPUT)
    val GRID_MINUS2_INDEX = Grid2DFactory.initMutableCharGrid(INPUT, -2)

    @Test
    fun initCharGridWithDefaultValue() {
        val rowCount = 3
        val colCount = 4
        val initialValue = 'a'
        val grid = Grid2D(rowCount, colCount, initialValue)
        assertEquals(3, grid.gridValues.size)
        grid.gridValues.forEach { line ->
            assertEquals(colCount, line.size)
            line.forEach { cell -> assertEquals(initialValue, cell) } }
    }

    @Test
    fun initIntGridWithDefaultValue() {
        val rowCount = 3
        val colCount = 4
        val initialValue = 3
        val grid = Grid2D(rowCount, colCount, initialValue)
        assertEquals(3, grid.gridValues.size)
        grid.gridValues.forEach { line ->
            assertEquals(colCount, line.size)
            line.forEach { cell -> assertEquals(initialValue, cell) } }
    }

    @Test
    fun getByPositionTest() {
        assertEquals('a', GRID_0_INDEX.getValue(Coordinate(0, 0)))
        assertEquals('a', GRID_1_INDEX.getValue(Coordinate(1,1)))
    }

    @Test
    fun getByCoordinatesTest() {
        assertEquals('a', GRID_0_INDEX.getValue(0, 0))
        assertEquals('a', GRID_1_INDEX.getValue(1,1))
    }

    @Test
    fun getRowValuesTest() {
        assertEquals(listOf('g', 'h', 'i', 'j', 'k'), GRID_0_INDEX.getRowValues(1))
        assertEquals(listOf('g', 'h', 'i', 'j', 'k'), GRID_1_INDEX.getRowValues(2))
    }

    @Test
    fun getColumnValuesTest() {
        assertEquals(listOf('b', 'h', 'm', 'r'), GRID_0_INDEX.getColumnValues(1))
        assertEquals(listOf('b', 'h', 'm', 'r'), GRID_1_INDEX.getColumnValues(2))
    }

    @Test
    fun isValidPositionByPositionTest() {
        assertTrue(GRID_1_INDEX.isValidPosition(Coordinate(1,1)))
        assertFalse(GRID_1_INDEX.isValidPosition(Coordinate(0,0)))
    }

    @Test
    fun isValidPositionByCoordinatesTest() {
        assertTrue(GRID_1_INDEX.isValidPosition(1,1))
        assertFalse(GRID_1_INDEX.isValidPosition(0,0))
    }

    // test that it doesn't throw an exception
    @Test
    fun printWithoutCursorTest() {
        GRID_1_INDEX.print()
        println()
        GRID_MINUS2_INDEX.print()
        println()
    }

    // test that it doesn't throw an exception
    @Test
    fun printWithCursorTest() {
        val cursor = GridCursor(GRID_1_INDEX, Coordinate (2, 3))
        GRID_1_INDEX.print(cursor)
        println()
    }

    // test that it doesn't throw an exception
    @Test
    fun printWithCursorAndPathTest() {
        val cursor = GridCursor(GRID_1_INDEX, Coordinate (1, 3))
        cursor.move(Direction.SOUTH, 1)
        cursor.move(Direction.WEST, 1)
        GRID_1_INDEX.print(cursor)
        println()
    }

}