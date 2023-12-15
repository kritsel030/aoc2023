package day10

import base.BaseSolver
import base.INPUT_VARIANT
import day07.CardType
import util.grid.*
import java.lang.IllegalStateException

fun main(args: Array<String>) {
    P10_Solver().solve(INPUT_VARIANT.REAL)
}

class P10_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "pipe maze"
    }

    // answer: 6757
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        // initialized the grid
        val pipeGrid = Grid2DFactory.initCharGrid(inputLines)

        // find the start position
        var startCoordinate:Coordinate? = null
        for (rowNo in 0 until pipeGrid.gridValues.size) {
            for (colNo in 0 until pipeGrid.gridValues[0].size) {
                if (pipeGrid.getValue(rowNo, colNo) == 'S') {
                    startCoordinate = Coordinate(rowNo, colNo)
                    break
                }
            }
            if (startCoordinate != null)
                break
        }

        // check if we've found the start coordinate
        if (startCoordinate == null) {
            throw IllegalStateException("we haven't found the start coordinate yet")
        } else {
//            println("start coordinate: $startCoordinate")
        }

        // now start moving until we're back at Start again
        val cursor = PipeCursor(pipeGrid, startCoordinate!!)
        while (true) {
            cursor.move()
            if (cursor.getValue() == 'S')
                break
        }

//        println("path length: ${cursor.path.size}")

        return cursor.path.size/2

    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }
}

//class PipeGrid(input:MutableList<MutableList<Char>>) : Grid2D<Char>(input) {
//
//}

class PipeCursor(grid:Grid2D<Char>, startCoordinate:Coordinate) : Cursor<Char>(grid, startCoordinate) {

    fun move() {
//        println("move")
        val latestDirection = latestDirection()
        val moveDirection = when (latestDirection) {
            null -> directionFromStart()
            Direction.NORTH -> TileSymbol[getValue()]!!.directions.filter{!it.equals(Direction.SOUTH)}.first()
            Direction.SOUTH -> TileSymbol[getValue()]!!.directions.filter{!it.equals(Direction.NORTH)}.first()
            Direction.WEST -> TileSymbol[getValue()]!!.directions.filter{!it.equals(Direction.EAST)}.first()
            Direction.EAST -> TileSymbol[getValue()]!!.directions.filter{!it.equals(Direction.WEST)}.first()
            else -> throw IllegalStateException("we shouldn't get here (move function) ")
        }
        if (okMove(moveDirection)) {
            move(moveDirection)
//            println("  moved $moveDirection to ${getValue()} at ${currentCoordinate.rowNo},${currentCoordinate.colNo}")
        } else {
            throw IllegalStateException("We cannot move to $moveDirection, now we're stuck")
        }
    }

    fun directionFromStart(): Direction {
        var result:Direction? = null
        for (direction in DIRECTIONS) {
            if( super.canMove(direction, 1) &&
                    when (direction) {
                        Direction.NORTH -> TileSymbol[peek(direction)]!!.directions.contains(Direction.SOUTH)
                        Direction.SOUTH -> TileSymbol[peek(direction)]!!.directions.contains(Direction.NORTH)
                        Direction.WEST -> TileSymbol[peek(direction)]!!.directions.contains(Direction.EAST)
                        Direction.EAST -> TileSymbol[peek(direction)]!!.directions.contains(Direction.WEST)
                        else -> throw IllegalStateException("we shouldn't get here")
                    }) {
                result = direction
                break
            }
        }
        return result!!
    }

    fun okMove(direction: Direction, distance: Int? = 1): Boolean {
        val canLeaveCurrent = TileSymbol[getValue()]!!.directions.contains(direction)
        val canEnterNext = super.canMove(direction, distance) &&
        when (direction) {
            Direction.NORTH -> TileSymbol[peek(direction)]!!.directions.contains(Direction.SOUTH)
            Direction.SOUTH -> TileSymbol[peek(direction)]!!.directions.contains(Direction.NORTH)
            Direction.WEST -> TileSymbol[peek(direction)]!!.directions.contains(Direction.EAST)
            Direction.EAST -> TileSymbol[peek(direction)]!!.directions.contains(Direction.WEST)
            else -> throw IllegalStateException("we shouldn't get here")
        }
        return canLeaveCurrent && canEnterNext
    }

    companion object {
        val DIRECTIONS = listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    }

}

enum class TileSymbol(val char:Char, val directions:List<Direction>) {
    VERT  ('|', listOf(Direction.NORTH, Direction.SOUTH)),
    HOR   ('-', listOf(Direction.WEST, Direction.EAST)),
    NE    ('L', listOf(Direction.NORTH, Direction.EAST)),
    NW    ('J', listOf(Direction.NORTH, Direction.WEST)),
    SW    ('7', listOf(Direction.SOUTH, Direction.WEST)),
    SE    ('F', listOf(Direction.SOUTH, Direction.EAST)),
    GR    ('.', emptyList()),
    START ('S', listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
    /*
| is a vertical pipe connecting north and south.
- is a horizontal pipe connecting east and west.
L is a 90-degree bend connecting north and east.
J is a 90-degree bend connecting north and west.
7 is a 90-degree bend connecting south and west.
F is a 90-degree bend connecting south and east.
. is ground; there is no pipe in this tile.
S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
     */

    companion object {
        private val map: Map<Char, TileSymbol> = TileSymbol.values().associateBy { it.char }
        operator fun get(char: Char) = map[char]
    }
}


