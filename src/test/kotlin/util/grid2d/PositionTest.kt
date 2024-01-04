package util.grid2d

import java.lang.Exception
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionTest {

    @Test
    fun moveNorthWestTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.NORTHWEST, 2)
        assertEquals(0, newPosition.rowNo)
        assertEquals(0, newPosition.colNo)
    }

    @Test
    fun moveNorthTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.NORTH, 2)
        assertEquals(0, newPosition.rowNo)
        assertEquals(2, newPosition.colNo)
    }

    @Test
    fun moveNorthEastWest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.NORTHEAST, 2)
        assertEquals(0, newPosition.rowNo)
        assertEquals(4, newPosition.colNo)
    }

    @Test
    fun moveEastTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.EAST, 2)
        assertEquals(2, newPosition.rowNo)
        assertEquals(4, newPosition.colNo)
    }

    @Test
    fun moveSouthEastTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.SOUTHEAST, 2)
        assertEquals(4, newPosition.rowNo)
        assertEquals(4, newPosition.colNo)
    }

    @Test
    fun moveSouthTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.SOUTH, 2)
        assertEquals(4, newPosition.rowNo)
        assertEquals(2, newPosition.colNo)
    }

    @Test
    fun moveSouthWestTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.SOUTHWEST, 2)
        assertEquals(4, newPosition.rowNo)
        assertEquals(0, newPosition.colNo)
    }


    @Test
    fun moveWestTest() {
        var coordinate = Coordinate(2,2)
        val newPosition = coordinate.move(Direction.WEST, 2)
        assertEquals(2, newPosition.rowNo)
        assertEquals(0, newPosition.colNo)
    }

    @Test
    fun getNeighboursWithoutDiagonalsTest() {
        var coordinate = Coordinate(2,2)
        val neighbours = coordinate.findNeighbours(false, 2)
        assertEquals(4, neighbours.size)
        neighbours.forEach { when(it.key) {
            Direction.NORTH -> {
                assertEquals(0, it.value.rowNo)
                assertEquals(2, it.value.colNo)
            }
            Direction.EAST -> {
                assertEquals(2, it.value.rowNo)
                assertEquals(4, it.value.colNo)
            }
            Direction.SOUTH -> {
                assertEquals(4, it.value.rowNo)
                assertEquals(2, it.value.colNo)
            }
            Direction.WEST -> {
                assertEquals(2, it.value.rowNo)
                assertEquals(0, it.value.colNo)
            }
            else -> throw Exception("we should not get here")
        }}
    }

    @Test
    fun getNeighboursWithDiagonalsTest() {
        var coordinate = Coordinate(2,2)
        val neighbours = coordinate.findNeighbours(true, 2)
        assertEquals(8, neighbours.size)
        neighbours.forEach { when(it.key) {
            Direction.NORTHWEST -> {
                assertEquals(0, it.value.rowNo)
                assertEquals(0, it.value.colNo)
            }
            Direction.NORTH -> {
                assertEquals(0, it.value.rowNo)
                assertEquals(2, it.value.colNo)
            }
            Direction.NORTHEAST-> {
                assertEquals(0, it.value.rowNo)
                assertEquals(4, it.value.colNo)
            }
            Direction.EAST -> {
                assertEquals(2, it.value.rowNo)
                assertEquals(4, it.value.colNo)
            }
            Direction.SOUTHEAST -> {
                assertEquals(4, it.value.rowNo)
                assertEquals(4, it.value.colNo)
            }
            Direction.SOUTH -> {
                assertEquals(4, it.value.rowNo)
                assertEquals(2, it.value.colNo)
            }
            Direction.SOUTHWEST -> {
                assertEquals(4, it.value.rowNo)
                assertEquals(0, it.value.colNo)
            }
            Direction.WEST -> {
                assertEquals(2, it.value.rowNo)
                assertEquals(0, it.value.colNo)
            }
        }}
    }

}