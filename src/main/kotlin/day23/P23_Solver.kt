package day23

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid2d.*
import java.lang.StringBuilder
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    P23_Solver().solve(INPUT_VARIANT.REAL)
}

class P23_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "long walk"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initCharGrid(inputLines)

        // key: start coordinate of the section + direction to the next tile
        // value: end coordinate of the section + plus the distance from start to finish (excluding the end coordinate)
        val segments = mutableMapOf<Pair<Coordinate, Direction>, Segment>()

        val start = Coordinate(0, 1)
        val cursor = HikeCursor(grid, start)
        val longestPathCursor = move(cursor, Direction.SOUTH, Segment(start, Direction.SOUTH), grid, segments)
        val answer = if (longestPathCursor != null) longestPathCursor.length() else "null result"
//        if (longestPathCursor != null) {
//            printGrid(longestPathCursor)
//        }
        return answer
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun move(cursor: HikeCursor,
             direction: Direction,
             segment:Segment,
             grid:Grid2D<Char>,
             segments:MutableMap<Pair<Coordinate, Direction>, Segment>, depth: Int = 0) : HikeCursor? {
        return jumpTo(cursor, cursor.currentCoordinate.move(direction), direction, segment, grid, segments, depth)
    }

    fun jumpTo(cursor: HikeCursor,
               newCoordinate:Coordinate,
               direction: Direction,
               segment:Segment,
               grid:Grid2D<Char>,
               segments:MutableMap<Pair<Coordinate, Direction>, Segment>, depth: Int) : HikeCursor? {
        val prefixBuilder = StringBuilder()
        for (i in (0 until depth)) {
            prefixBuilder.append(" ")
        }
        val prefix = prefixBuilder.toString()
        cursor.moveTo(newCoordinate, direction)

        val currentPathLength = cursor.length()
        val target = Coordinate(grid.rowCount()-1, grid.colCount() - 2)

        if (cursor.currentCoordinate == target) {
            println("$prefix hit target at $currentPathLength")
//            return currentPathLength
            return cursor
        }

        // are we new here? or have we been here before, but via a shorter path?
        if (! grid.isVisited(cursor.currentCoordinate) ||
            ! grid.visitedCoordinates[cursor.currentCoordinate]!!.containsKey("longest") ||
            currentPathLength > (grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"] as Int) ) {
            grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"] = currentPathLength
//            println("hit ${cursor.currentCoordinate} at $currentPathLength")

            val validNeighbours = validNeighbours(cursor, grid)
            if (validNeighbours.size > 1) {
                println("$prefix ${cursor.currentCoordinate} is a node (${validNeighbours.size} valid neighbours)")
                // we've hit a node
                // end and process the current segment
                segment.oneway = if (cursor.getValue() != '.') true else segment.oneway
                segment.coordinates.add(cursor.currentCoordinate)
                segment.end = cursor.currentCoordinate
                segment.endDirection = direction
                segments[Pair(segment.start, segment.startDirection)] = segment
                // add the segment to the cursor
                cursor.segments.add(segment)
                // reset the cursor
                cursor.path = mutableListOf()
                // when the segment was not one-way, process the inverse of this segment
                if (!segment.oneway) {
                    val inverseSegment = segment.inverse()
                    segments[Pair(inverseSegment.start, inverseSegment.startDirection)] = inverseSegment
                }

                val cursors = validNeighbours.map {
                    // do we have segment data for this node which we can use?
                    if (segments.containsKey(Pair(cursor.currentCoordinate, it.key))) {
                        val segment = segments[(Pair(cursor.currentCoordinate, it.key))]!!
                        println("$prefix going ${it.key} | use segment data for ${cursor.currentCoordinate} to jump to ${segment.end}")
                        // add the segment to the cursor
                        cursor.segments.add(segment)
                        // jump to segment end
                        jumpTo(cursor.clone(), segment.end!!, it.key, segment, grid, segments, depth+1)
                    } else {
                        println("$prefix going ${it.key} | simply move")
                        move(cursor.clone(), it.key, Segment(cursor.currentCoordinate, it.key, cursor.getValue() != '.'), grid, segments, depth+1)
                    }
                }
                return cursors.filterNotNull().maxByOrNull { it.length() }
            } else if (validNeighbours.size == 1) {
                if (cursor.getValue() != '.') {
                    segment.oneway = true
                }
                segment.coordinates.add(cursor.currentCoordinate)
                segment.length++
                return move(cursor, validNeighbours.keys.first(), segment, grid, segments, depth)
            } else {
                // we've reached a dead end
                println("$prefix hit dead end at ${cursor.currentCoordinate}")
            }
        } else {
            // we were here before, via a longer path, so no need to further explore our current path
        }
        return null
    }

    fun validNeighbours (cursor: HikeCursor, grid: Grid2D<Char>) : Map<Direction, Coordinate> {
        return cursor.getNeighbours().filter {
                    // do not revisit a tile we've visited before with this cursor
                    cursor.path.none { visCoord -> visCoord.coordinate == it.value } &&
                    cursor.segments.none { seg -> !seg.coordinates.none { coord -> coord == it.value } } &&
                    // do not visit a tile which represents forest
                    grid.getValue(it.value) != '#' &&
                    // adhere to the forced directions
                     ((grid.getValue(cursor.currentCoordinate) == '>' && it.key == Direction.EAST) ||
                             (grid.getValue(cursor.currentCoordinate) == 'v' && it.key == Direction.SOUTH) ||
                             (grid.getValue(cursor.currentCoordinate) == '<' && it.key == Direction.WEST) ||
                             (grid.getValue(cursor.currentCoordinate) == '^' && it.key == Direction.NORTH) ||
                             grid.getValue(cursor.currentCoordinate) == '.')

                    // do not visit any tiles which will force you to go back the way you came
//                    (it.key == Direction.EAST && grid.getValue(it.value) != '<') &&
//                    (it.key == Direction.WEST && grid.getValue(it.value) != '>') &&
//                    (it.key == Direction.NORTH && grid.getValue(it.value) != 'v') &&
//                    (it.key == Direction.SOUTH && grid.getValue(it.value) != '^')
        }
    }

    companion object {
        fun printGrid(cursor:HikeCursor) {
                val indexBase = cursor.grid.indexBase
                val gridValues = cursor.grid.gridValues
                val visitedCoordinates = cursor.grid.visitedCoordinates
                // column index line
                print("    ")
                (0+indexBase..gridValues[0].size-1+indexBase).forEach {  print(it.absoluteValue.toString().padStart(2, ' ') + " ") }
                println()
                // dash line
                print("   ")
                (0+indexBase..gridValues[0].size-1+indexBase).forEach{print("---")}
                println()

                // row lines
                gridValues.forEachIndexed { rowIndex, colValues ->
                    val rowNo = rowIndex+indexBase
                    print(rowNo.absoluteValue.toString().padStart(2, ' ') + "| ")
                    colValues.forEachIndexed { colIndex, value ->
                        val colNo = colIndex + indexBase
                        if (cursor?.isAt(rowNo, colNo) == true) {
                            print(">$value<")
                        } else if (cursor != null && cursor.hasVisited(rowNo, colNo)) {
                            print("[$value]")
                        } else if (cursor == null && visitedCoordinates.containsKey(Coordinate(rowNo, colNo))) {
                            print("($value)")
                        } else {
                            print(" $value ")
                        }
                    }
                    println()
                }
            }

        }
}

class HikeCursor(grid: Grid2D<Char>, startCoordinate: Coordinate) : GridCursor<Char>(grid, startCoordinate) {
    var segments = mutableListOf<Segment>()

    fun length() : Int{
//        return path.size + segments.sumOf { it.length }
        val allCoordinates = path.map { it.coordinate }.toMutableSet()
        segments.forEach { allCoordinates.addAll(it.coordinates) }
        return allCoordinates.size - 1
    }

    override fun hasVisited(rowNo:Int, colNo:Int) : Boolean{
        return super.hasVisited(rowNo, colNo) ||
                segments.sumOf { seg -> seg.coordinates.count { coord -> coord == Coordinate(rowNo, colNo) }} > 0
    }

    override fun clone() : HikeCursor {
        var clone = HikeCursor(grid, currentCoordinate)
        if (pathStrategy == CursorPathStrategy.FULL_PATH) {
            clone.path = path.toMutableList()
        }
        clone.segments = this.segments.toMutableList()
        return clone
    }
}

class Segment(
    val start:Coordinate,
    val startDirection: Direction,
    var oneway: Boolean = false,
    val coordinates:MutableSet<Coordinate> = mutableSetOf(start),
    var end:Coordinate? = null,
    var endDirection: Direction? = null,
    var length:Int = 1
) {
    fun inverse() : Segment {
        return Segment(
                this.end!!,
                this.endDirection!!.opposite(),
                this.oneway,
                this.coordinates,
                this.start,
                this.startDirection.opposite(),
                this.length)

    }
}


