package util.grid

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GridCursorTest {

    val INPUT = listOf(
        "abcde",
        "ghijk",
        "lmnop",
        "qrstu"
    )
    val GRID_1_INDEX = Grid2DFactory.initCharGrid(INPUT, 1)
    val GRID_0_INDEX = Grid2DFactory.initCharGrid(INPUT)
    val GRID_MINUS2_INDEX = Grid2DFactory.initCharGrid(INPUT, -2)

    @Test
    fun constructorTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        assertEquals(1, cursor.path.size)
    }

    @Test
    fun canMoveTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        assertFalse(cursor.canMove(Direction.NORTH, 1))
        assertTrue(cursor.canMove(Direction.SOUTH, 2))
    }

    @Test
    fun peekTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        assertEquals('l', cursor.peek(Direction.SOUTH, 2))
    }

    @Test
    fun moveTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        cursor.move(Direction.SOUTH, 2)
        assertEquals(3, cursor.currentCoordinate.rowNo)
        assertEquals(1, cursor.currentCoordinate.colNo)
        assertEquals(2, cursor.path.size)
    }

    @Test
    fun getValueTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        assertEquals('a', cursor.getValue())
    }

    @Test
    fun isAtTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        assertTrue(cursor.isAt(1, 1))
        assertFalse(cursor.isAt(1, 2))
    }

    @Test
    fun hasVisitedTest() {
        var cursor = GridCursor(GRID_1_INDEX, Coordinate(1, 1))
        cursor.move(Direction.SOUTH, 2)
        assertTrue(cursor.hasVisited(1, 1))
        assertTrue(cursor.hasVisited(3, 1))
        assertFalse(cursor.hasVisited(2, 2))
    }
}