package day16

import base.BaseSolver
import base.INPUT_VARIANT
import base.Part
import util.grid.*
import java.lang.Error
import java.lang.NullPointerException
import kotlin.math.max

fun main(args: Array<String>) {
    P16_Solver().solve(INPUT_VARIANT.REAL, Part.PART1)
    println("=============================================")
    P16_Solver().solve(INPUT_VARIANT.REAL, Part.PART2)
}

class P16_Solver : BaseSolver() {

    var moveBeamCounter = 0
//    var visitedCoordinates:MutableList<Coordinate> = mutableListOf()

    override fun getPuzzleName(): String {
        return "beam of light"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        (1..1).forEach {
            val grid = Grid2DFactory.initCharGrid(inputLines)
            val cursor: Cursor<Char> = Cursor(grid, Coordinate(7, 0), Direction.EAST, CursorPathStrategy.LATEST_ONLY)
            try {
                println("start cursor: $cursor")
                moveBeam(cursor, NEXT_DIRECTIONS[Pair(Direction.EAST, cursor.getValue())]!!.first())
            } catch (e:Error) {
                println(e)
            } finally {
                println("end cursor: $cursor")
                println("last visited coordinate: ${grid.visitedCoordinates[cursor.currentCoordinate]}")
                println("moveBeamCounter: $moveBeamCounter")
                println("visited coordinates: ${grid.visitedCoordinates.size}")
                println("visited coordinates 2: ${grid.visitedCoordinates.map{(_, details) -> details["count"]!! as Int}.sum()}")
//                println("last x visited coordinates: ${visitedCoordinates.subList(0, 100)} (${visitedCoordinates.subList(0, 100).toSet().size})")
                println("maxDepth: $maxDepth")
                println("currentDepth: $currentDepth")
            }
        }

        /* from Part 2:
                val grid = Grid2DFactory.initCharGrid(inputLines)
                ...
                val cursor:Cursor<Char> = Cursor(grid, Coordinate(rowNo, 0), Direction.EAST)
                println("cursor: $cursor")
                moveBeam(cursor, NEXT_DIRECTIONS[Pair(Direction.EAST, cursor.getValue())]!!.first())
         */

//        grid.print()
//        println (cursor.path)
//        return grid.visitedCoordinates.size
        return "out"
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val grid = Grid2DFactory.initCharGrid(inputLines)
//        grid.print()

//        val maxSouth = (0 until grid.colCount())
//            .maxOf { colNo ->
//                val cursor:Cursor<Char> = Cursor(grid, Coordinate(0, colNo), Direction.SOUTH, CursorPathStrategy.LATEST_ONLY)
//                println("cursor: $cursor")
//                moveBeam(cursor, NEXT_DIRECTIONS[Pair(Direction.SOUTH, cursor.getValue())]!!.first())
//                val result = grid.visitedCoordinates.size
//                grid.reset()
//                result
//            }

//        val maxNorth = (0 until grid.colCount())
//            .maxOf { colNo ->
//                val cursor:Cursor<Char> = Cursor(grid, Coordinate(grid.rowCount()-1, colNo), Direction.NORTH, CursorPathStrategy.LATEST_ONLY)
//                println("cursor: $cursor")
//                moveBeam(cursor, NEXT_DIRECTIONS[Pair(Direction.NORTH, cursor.getValue())]!!.first())
//                val result = grid.visitedCoordinates.size
//                grid.reset()
//                result
//            }

//        val maxEast = (0 until grid.rowCount())
//            .maxOf { rowNo ->
//                val cursor:Cursor<Char> = Cursor(grid, Coordinate(rowNo, 0), Direction.EAST)
//                println("cursor: $cursor")
//                moveBeam(cursor, NEXT_DIRECTIONS[Pair(Direction.EAST, cursor.getValue())]!!.first())
//                val result = grid.visitedCoordinates.size
//                grid.reset()
//                result
//            }

        val maxWest = (0 until grid.rowCount())
            .maxOf { rowNo ->
                val cursor:Cursor<Char> = Cursor(grid, Coordinate(rowNo, grid.colCount()-1), Direction.WEST, CursorPathStrategy.LATEST_ONLY)
                println("cursor: $cursor")
                moveBeam(cursor, NEXT_DIRECTIONS[Pair(Direction.WEST, cursor.getValue())]!!.first())
                val result = grid.visitedCoordinates.size
                grid.reset()
                result
            }

        return maxOf(maxWest)
//        return maxEast
    }

    var maxDepth = 0
    var currentDepth = 0
    fun moveBeam(cursorIn:Cursor<Char>, direction: Direction, depth:Int=0) {
        maxDepth = max(depth, maxDepth)
        currentDepth = depth
        moveBeamCounter++
//        visitedCoordinates.add(0, cursorIn.currentCoordinate)

        cursorIn.move(direction)
        var cursor = cursorIn

        val nextDirections = NEXT_DIRECTIONS.get(Pair(cursor.latestDirection(), cursor.getValue()))!!
            .filter { direction -> canCursorMoveInDirection(cursor, direction) }

        val cloneCursor = nextDirections.size > 1
        nextDirections.forEach{direction ->
//            if (cloneCursor) {
                cursor = cursorIn.clone()
//            }
            moveBeam(cursor, direction, depth+1)
        }
    }

    private fun canCursorMoveInDirection(cursor:Cursor<Char>, direction:Direction) : Boolean{
        return cursor.canMove(direction) && !cursor.grid.isVisited(
            cursor.currentCoordinate.move(direction),
            direction
        )
    }

    companion object {
        val NEXT_DIRECTIONS = mapOf(
            // NORTH
            Pair(Direction.NORTH, '|') to listOf(Direction.NORTH),
            Pair(Direction.NORTH, '-') to listOf(Direction.WEST, Direction.EAST),
            Pair(Direction.NORTH, '/') to listOf(Direction.EAST),
            Pair(Direction.NORTH, '\\') to listOf(Direction.WEST),
            Pair(Direction.NORTH, '.') to listOf(Direction.NORTH),
            // SOUTH
            Pair(Direction.SOUTH, '|') to listOf(Direction.SOUTH),
            Pair(Direction.SOUTH, '-') to listOf(Direction.WEST, Direction.EAST),
            Pair(Direction.SOUTH, '/') to listOf(Direction.WEST),
            Pair(Direction.SOUTH, '\\') to listOf(Direction.EAST),
            Pair(Direction.SOUTH, '.') to listOf(Direction.SOUTH),
            // WEST
            Pair(Direction.WEST, '|') to listOf(Direction.NORTH, Direction.SOUTH),
            Pair(Direction.WEST, '-') to listOf(Direction.WEST),
            Pair(Direction.WEST, '/') to listOf(Direction.SOUTH),
            Pair(Direction.WEST, '\\') to listOf(Direction.NORTH),
            Pair(Direction.WEST, '.') to listOf(Direction.WEST),
            // EAST
            Pair(Direction.EAST, '|') to listOf(Direction.NORTH, Direction.SOUTH),
            Pair(Direction.EAST, '-') to listOf(Direction.EAST),
            Pair(Direction.EAST, '/') to listOf(Direction.NORTH),
            Pair(Direction.EAST, '\\') to listOf(Direction.SOUTH),
            Pair(Direction.EAST, '.') to listOf(Direction.EAST))
    }
}






