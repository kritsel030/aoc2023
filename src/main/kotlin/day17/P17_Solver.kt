package day17

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid.Coordinate
import util.grid.Cursor
import util.grid.Direction
import util.grid.Grid2DFactory
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    P17_Solver().solve(INPUT_VARIANT.EXAMPLE)
}

class P17_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "heat loss"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initIntGrid(inputLines)
        val crucible = Cursor(grid, Coordinate(0, 0), Direction.EAST)
        crucible.grid.visitedCoordinates[crucible.currentCoordinate]!!["lowest_costs"] = 0
        val cheapestPath = crucible.getNeighbours().keys
            .map { moveCrusible(crucible.clone(), it) }
            .filterNotNull()
            .sortedBy { it.costs }
            .first()

        grid.print(cheapestPath)
        println(cheapestPath.path.map { it.coordinate }.toList().reversed())
        println("lowest costs: ${cheapestPath.costs}")

        return grid.visitedCoordinates[Coordinate(grid.rowCount()-1, grid.colCount()-1)]!!["lowest_costs"]!!
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun moveCrusible(crucible:Cursor<Int>, direction: Direction, depth:Int=0) : Cursor<Int>? {
        val prefix = (0..depth).map { " " }.joinToString("")
        val newCoord = crucible.currentCoordinate.move(direction)
//        print("$prefix moveCrucible(${crucible.currentCoordinate}, $direction) to $newCoord")

        // conditions to be met before we can actually move to the new coordinate
        // 1. the previous three moves were not all in this same direction
        // 2. we haven't visited the new coordinate before, or we did but the costs of this path are lower
        //    than the costs of the cheapest path that already touched this coordinate before
        if ((crucible.path.size <= 3 || (0..2).count {crucible.path[it].travelledDirection == direction} != 3)) {
            val costs = newPathCosts(crucible, newCoord)
            if (!crucible.grid.visitedCoordinates.containsKey(newCoord) ||
                    costs < (crucible.grid.visitedCoordinates[newCoord]!!["lowest_costs"] as Int)) {
//                println (" | new/lower costs $costs")
                crucible.move(direction)
                // set the lowest costs on the new coordinate on the grid
                crucible.grid.visitedCoordinates[newCoord]!!["lowest_costs"] = costs
                crucible.costs = costs

                // continue the journey when we haven't yet reached the final coordinate
                return if (!crucible.currentCoordinate.equals(Coordinate(crucible.grid.rowCount()-1,crucible.grid.colCount()-1))) {
                    crucible.getNeighbours().keys
                        .filter{it != oppositeDirection(direction)}
                        .map { moveCrusible(crucible.clone(), it, depth+1) }
                        .filterNotNull()
                        .sortedBy { it.costs }
                        .firstOrNull()
                } else {
                    crucible
                }
            } else {
//                println (" | higher costs $costs")
            }
        } else {
//            println(" | not allowed")
        }
        return null
    }

    fun oppositeDirection (direction:Direction) : Direction {
        return when (direction) {
            Direction.NORTH -> Direction.SOUTH
            Direction.SOUTH -> Direction.NORTH
            Direction.WEST -> Direction.EAST
            Direction.EAST -> Direction.WEST
            else -> throw IllegalArgumentException("oppositeDirection not supported for $direction")
        }
    }

    private fun newPathCosts(cursor: Cursor<Int>, newCoord: Coordinate? = null) : Int {
        // calculate the costs of the path, ignoring the costs of the start coordinate
        // (start coordinate is the last one in the path)
        var costs = (0 until cursor.path.size - 1).sumOf { cursor.path[it].value }
        if (newCoord != null) {
            costs += cursor.grid.getValue(newCoord)
        }
        return costs
    }
}


