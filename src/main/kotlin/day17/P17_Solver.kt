package day17

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid.Coordinate
import util.grid.GridCursor
import util.grid.Direction
import util.grid.Grid2DFactory
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    P17_Solver().solve(INPUT_VARIANT.REAL)
}

class P17_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "heat loss"
    }

    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val grid = Grid2DFactory.initIntGrid(inputLines)
        val crucibleCursor = GridCursor(grid, Coordinate(0, 0))

        var cheapestPath:GridCursor<Int>? = null
        try {
            cheapestPath = visitAllowedNeighbours(crucibleCursor)
        } finally {
            grid.print(cheapestPath)
            if (cheapestPath != null) {
                println("lowest costs: ${cheapestPath.costs}")
            }
        }
        if (cheapestPath != null) {
            return cheapestPath.costs
        } else {
            return "cheapest path is null"
        }
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun moveCrusible(crucibleCursor:GridCursor<Int>, direction: Direction, depth:Int=0) : GridCursor<Int>? {
        try {
            val prefix = (0..depth).map { " " }.joinToString("")
//            println ("$prefix moveCrucible2(${crucibleCursor.currentCoordinate}, $direction) to ${crucibleCursor.currentCoordinate.move(direction)}")
            crucibleCursor.move(direction)

            val pathCosts = pathCosts(crucibleCursor)

            val maxCosts = ( (crucibleCursor.grid.rowCount() - 1)
                    + (crucibleCursor.grid.colCount() - 1) ) * 9
//            val maxAdditionalCosts = ( ( (crucibleCursor.grid.rowCount() - crucibleCursor.currentCoordinate.rowNo) - 1)
//            + ( (crucibleCursor.grid.colCount() - crucibleCursor.currentCoordinate.colNo) - 1) ) * 9
//            val maxCurrentCosts = ( crucibleCursor.currentCoordinate.rowNo + crucibleCursor.currentCoordinate.colNo) * 9

            val theEnd = Coordinate(crucibleCursor.grid.rowCount() - 1, crucibleCursor.grid.colCount() - 1)
            if (pathCosts <= maxCosts ||(crucibleCursor.grid.visitedCoordinates.containsKey(theEnd) &&
                        crucibleCursor.grid.visitedCoordinates[theEnd]!!.containsKey("lowests_costs") &&
                        crucibleCursor.grid.visitedCoordinates[theEnd]!!["lowests_costs"] as Int > pathCosts) ) {

                // have we reached the end?
                if (crucibleCursor.currentCoordinate.equals(theEnd)) {
                    // we've hit our target, return the current cursor when it is the cheapest path found so far
                    // (meaning: we haven't reached the target before at all,
                    //  or we did, but the lowest_costs path found so far was more expensive than our current path costs)
                    if (!crucibleCursor.grid.visitedCoordinates.containsKey(crucibleCursor.currentCoordinate) ||
                        !crucibleCursor.grid.visitedCoordinates[crucibleCursor.currentCoordinate]!!.containsKey("lowest_costs") ||
                        pathCosts < (crucibleCursor.grid.visitedCoordinates[crucibleCursor.currentCoordinate]!!["lowest_costs"] as Int)
                    ) {
//                    println("$prefix (target reached via first or cheaper path: $pathCosts)")
                        println("target reached via first or cheaper path, costs = $pathCosts, depth = $depth")
                        crucibleCursor.costs = pathCosts
                        crucibleCursor.grid.visitedCoordinates[crucibleCursor.currentCoordinate]!!["lowest_costs"] =
                            pathCosts
                        return crucibleCursor
                    } else {
//                println("$prefix (target reached via costlier path)")
                        return null
                    }
                }

                // have we not changed direction?
                // (in that case we're not interested in optimizing for cheapest path in this step)
//            else if (latestStraightSegmentSummary(crucibleCursor).length > 1) {
////            println("$prefix (no direction change, continue with neighbours)")
//
//                // simply continue to try a cheaper path by branching out to all allowed neighbours
//                return visitAllowedNeighbours(crucibleCursor, depth)
//            }

                // we will only continue when
                // - we haven't yet visited this coordinate from this direction
                // - or we have, but our current path via the same direction is cheaper
                else if (newOrCheaperPathFound(crucibleCursor, pathCosts)) {
                    val latestSSS = latestStraightSegmentSummary(crucibleCursor)
//                println("$prefix (new or cheaper path via $latestSSS)")

                    crucibleCursor.grid.visitedCoordinates[crucibleCursor.currentCoordinate]!!["lowest_costs_${latestSSS}"] =
                        pathCosts
                    crucibleCursor.costs = pathCosts

                    // continue the journey when we haven't yet reached the final coordinate
                    return visitAllowedNeighbours(crucibleCursor, depth)

                } else {
//                println("$prefix (none)")
                }
            }
        } catch (e:Error) {
//            println("error caught, depth = $depth")
            throw e
        }
        return null
    }

    private fun visitAllowedNeighbours(
        crucibleCursor: GridCursor<Int>,
        depth: Int = 0
    ) : GridCursor<Int>? {
        return crucibleCursor.getNeighbours().keys
            .filter { crucibleCursor.latestDirection() == null || it != oppositeDirection(crucibleCursor.latestDirection()!!) }
            .filter{ crucibleCursor.latestDirection() == null ||
                    it != crucibleCursor.latestDirection() ||
                    latestStraightSegmentSummary(crucibleCursor).length <= 2}
            .map { moveCrusible(crucibleCursor.clone(), it, depth + 1) }
            .filterNotNull()
            .sortedBy { it.costs }
            .firstOrNull()
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

    /**
     * Calculate the costs of the path already travelled by the cursor, plus the new coordinate
     * we plan to visit
     */
    private fun pathCosts(crucibleCursor: GridCursor<Int>, newCoordinate: Coordinate? = null) : Int {
        // calculate the costs of the path, by summing the values of all coordinates of the path
        // while ignoring the costs of the start coordinate
        // (the start coordinate is the LAST one in the path)
        var costs = (0 until crucibleCursor.path.size - 1).sumOf { crucibleCursor.path[it].value }
        if (newCoordinate != null) {
            // add the value of the new coordinate we plan to visit
            costs += crucibleCursor.grid.getValue(newCoordinate)
        }
        return costs
    }

    private fun newOrCheaperPathFound(crucibleCursor:GridCursor<Int>, newPathCosts: Int) : Boolean {
        val latestSSS = latestStraightSegmentSummary(crucibleCursor)
        val lowestCostsKey = "lowest_costs_$latestSSS"
        if (crucibleCursor.grid.visitedCoordinates.containsKey(crucibleCursor.currentCoordinate)) {
            if (crucibleCursor.grid.visitedCoordinates[crucibleCursor.currentCoordinate]!!.containsKey(lowestCostsKey) &&
                (newPathCosts > (crucibleCursor.grid.visitedCoordinates[crucibleCursor.currentCoordinate]!![lowestCostsKey] as Int))
            ) return false

        }
        return true
    }

    private fun latestStraightSegmentSummary (cursor: GridCursor<Int> ) : String{
        if (cursor.latestDirection() == null) {
            return ""
        }
        var summary = StringBuilder()
        for (i in (0 until cursor.path.size)) {
            if (cursor.path[i].travelledDirection == cursor.latestDirection()) {
                summary.append(cursor.latestDirection().toString()[0])
            } else {
                break
            }
        }
        return summary.toString()
    }

}


