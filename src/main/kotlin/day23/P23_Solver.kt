package day23

import base.BaseSolver
import base.INPUT_VARIANT
import day23.P23_Solver.Companion.jumpTo
import util.grid2d.*
import java.lang.StringBuilder
import java.util.PriorityQueue
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    P23_Solver().solve(INPUT_VARIANT.REAL)
}

class P23_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "long walk"
    }

    // answer: 2114
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initCharGrid(inputLines)

        // key: start coordinate of the section + direction to the next tile
        // value: end coordinate of the section + plus the distance from start to finish (excluding the end coordinate)
        val segments = mutableMapOf<Pair<Coordinate, Direction>, Segment>()
        val start = Coordinate(0, 1)
        val cursor = HikeCursor(grid, start)
        val queue = PriorityQueue<Command>()
        val endReachedCursors = mutableListOf<HikeCursor>()
        queue.add(Command(cursor, start, Direction.SOUTH,  Segment(start, Direction.SOUTH), grid, segments, queue))
        while(!queue.isEmpty()) {
            val command = queue.poll()
            val endReached = command.execute()
            if (endReached) {
                endReachedCursors.add(command.cursor)
            }
        }

        println("complete path lengths: ${endReachedCursors.map {it.length()}}")

        val longestPathCursor = endReachedCursors.maxByOrNull { it.length() }
        val answer = if (longestPathCursor != null) longestPathCursor.length() else "null result"
        if (inputVariant== INPUT_VARIANT.EXAMPLE && longestPathCursor != null) {
            printGrid(grid, longestPathCursor)
        }
        return answer
    }


    // 3763 too low
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val grid = Grid2DFactory.initMutableCharGrid(inputLines)
        // replace all slopes with normal tiles
        listOf('>', 'v', '<', '^').forEach { slope ->
            grid.find(slope).forEach { coordinate -> grid.setValue(coordinate, '.') }
        }

        // key: start coordinate of the section + direction to the next tile
        // value: end coordinate of the section + plus the distance from start to finish (excluding the end coordinate)
        val segments = mutableMapOf<Pair<Coordinate, Direction>, Segment>()
        val start = Coordinate(0, 1)
        val cursor = HikeCursor(grid, start)
        val queue = PriorityQueue<Command>()
        val endReachedCursors = mutableListOf<HikeCursor>()
        queue.add(Command(cursor, start, Direction.SOUTH,  Segment(start, Direction.SOUTH), grid, segments, queue))
        while(!queue.isEmpty()) {
            val command = queue.poll()
            val endReached = command.execute()
            if (endReached) {
                endReachedCursors.add(command.cursor)
            }
        }

        println("complete path lengths: ${endReachedCursors.map {it.length()}}")

        val longestPathCursor = endReachedCursors.maxByOrNull { it.length() }
        val answer = if (longestPathCursor != null) longestPathCursor.length() else "null result"
        if (inputVariant== INPUT_VARIANT.EXAMPLE && longestPathCursor != null) {
            printGrid(grid, longestPathCursor)
        }
        return answer
    }

    companion object {
        fun move(cursor: HikeCursor,
                 direction: Direction,
                 segment:Segment,
                 grid:Grid2D<Char>,
                 segments:MutableMap<Pair<Coordinate, Direction>, Segment>,
                 queue:PriorityQueue<Command>,
                 slipperySlopes: Boolean,
                 depth: Int = 0) : Boolean {
            return jumpTo(cursor, cursor.currentCoordinate.move(direction), direction, segment, grid, segments, queue, slipperySlopes, depth)
        }

        fun jumpTo(cursor: HikeCursor,
                   newCoordinate:Coordinate,
                   direction: Direction,
                   segment:Segment,
                   grid:Grid2D<Char>,
                   segments:MutableMap<Pair<Coordinate, Direction>, Segment>,
                   queue:PriorityQueue<Command>,
                   slipperySlopes: Boolean,
                   depth: Int) : Boolean {
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
                cursor.pathLengthAtNodes.forEach { (coordinate, direction, pathLength)  ->
                    val longestKey = "longest_$direction"
                    if (! grid.visitedCoordinates[coordinate]!!.containsKey(longestKey) ||
                        pathLength > (grid.visitedCoordinates[coordinate]!![longestKey] as Int)) {
                        grid.visitedCoordinates[coordinate]!![longestKey] = pathLength
                    }
                }
                return true
            }

            // are we new here? or have we been here before, but via a shorter path?
            val longestKey = "longest_$direction"
            if (! grid.visitedCoordinates[cursor.currentCoordinate]!!.containsKey(longestKey) ||
                currentPathLength > (grid.visitedCoordinates[cursor.currentCoordinate]!![longestKey] as Int) ) {
//                grid.visitedCoordinates[cursor.currentCoordinate]!![longestKey] = currentPathLength
    //            println("hit ${cursor.currentCoordinate} at $currentPathLength")

                val validNeighbours = validNeighbours(cursor, grid, slipperySlopes)
                if (validNeighbours.size > 1) {
                    // we've hit a node
//                    println("$prefix ${cursor.currentCoordinate} is a node (${validNeighbours.size} valid neighbours)")
                    cursor.pathLengthAtNodes.add(Triple(cursor.currentCoordinate, direction, currentPathLength))

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
                        val newSegment = Segment(cursor.currentCoordinate, it.key, cursor.getValue() != '.')
                        // do we have segment data for this node which we can use?
                        if (segments.containsKey(Pair(cursor.currentCoordinate, it.key))) {
                            val segment = segments[(Pair(cursor.currentCoordinate, it.key))]!!
//                            println("$prefix going ${it.key} | use segment data for ${cursor.currentCoordinate} to jump to ${segment.end}")
                            // add the segment to the cursor
                            cursor.segments.add(segment)
                            // jump to segment end
                            //jumpTo(cursor.clone(), segment.end!!, it.key, segment, grid, segments, depth+1)
//                            queue.add(Command(cursor.clone(), segment.end!!, it.key, segment, grid, segments, queue, slipperySlopes, depth+1))
                            queue.add(Command(cursor.clone(), segment.end!!, it.key, newSegment, grid, segments, queue, slipperySlopes, depth+1))
                        } else {
//                            println("$prefix going ${it.key} | simply move to ${it.value}")
//                            move(cursor.clone(), it.key, Segment(cursor.currentCoordinate, it.key, cursor.getValue() != '.'), grid, segments, depth+1)
                            val clonedCursor = cursor.clone()
                            queue.add(Command(clonedCursor, it.value, it.key, newSegment, grid, segments, queue, slipperySlopes, depth+1))
                        }
                    }
                    return false
                } else if (validNeighbours.size == 1) {
                    if (cursor.getValue() != '.') {
                        segment.oneway = true
                    }
                    segment.coordinates.add(cursor.currentCoordinate)
                    return move(cursor, validNeighbours.keys.first(), segment, grid, segments, queue, slipperySlopes, depth)
                } else {
                    // we've reached a dead end
//                    println("$prefix [STOP] hit dead end at ${cursor.currentCoordinate}")
                }
            } else {
                // we were here before, via a longer path, so no need to further explore our current shorter path
//                println("$prefix [STOP] been at ${cursor.currentCoordinate} before via longer path ${grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"]} (compared to current $currentPathLength)")
            }
            return false
        }

        fun validNeighbours (cursor: HikeCursor, grid: Grid2D<Char>, slipperySlopes:Boolean) : Map<Direction, Coordinate> {
            return cursor.getNeighbours().filter {
                        // do not revisit a tile we've visited before with this cursor
                        cursor.path.none { visCoord -> visCoord.coordinate == it.value } &&
                        cursor.segments.none { seg -> !seg.coordinates.none { coord -> coord == it.value } } &&
                        // do not visit a tile which represents forest
                        grid.getValue(it.value) != '#' &&
                        // adhere to the forced directions
                        (!slipperySlopes ||
                         ((grid.getValue(cursor.currentCoordinate) == '>' && it.key == Direction.EAST) ||
                                 (grid.getValue(cursor.currentCoordinate) == 'v' && it.key == Direction.SOUTH) ||
                                 (grid.getValue(cursor.currentCoordinate) == '<' && it.key == Direction.WEST) ||
                                 (grid.getValue(cursor.currentCoordinate) == '^' && it.key == Direction.NORTH) ||
                                 grid.getValue(cursor.currentCoordinate) == '.'))
            }
        }

        fun printGrid(grid:Grid2D<Char>, cursor:HikeCursor?) {
                val indexBase = grid.indexBase
                val gridValues = grid.gridValues
                val visitedCoordinates = grid.visitedCoordinates
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
    var pathLengthAtNodes = mutableListOf<Triple<Coordinate, Direction, Int>>()

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
        clone.pathLengthAtNodes = this.pathLengthAtNodes.toMutableList()
        return clone
    }
}

class Segment(
    val start:Coordinate,
    val startDirection: Direction,
    var oneway: Boolean = false,
    val coordinates:MutableSet<Coordinate> = mutableSetOf(start),
    var end:Coordinate? = null,
    var endDirection: Direction? = null
) {
    fun inverse() : Segment {
        return Segment(
                this.end!!,
                this.endDirection!!.opposite(),
                this.oneway,
                this.coordinates,
                this.start,
                this.startDirection.opposite())

    }
}

class Command(val cursor: HikeCursor,
              val newCoordinate:Coordinate,
              val direction: Direction,
              val segment:Segment,
              val grid:Grid2D<Char>,
              val segments:MutableMap<Pair<Coordinate, Direction>, Segment>,
              val queue:PriorityQueue<Command>,
              val slipperySlopes: Boolean = true,
              val depth: Int = 1) : Comparable<Command>{
    fun execute() : Boolean {
        return jumpTo(cursor, newCoordinate, direction, segment, grid, segments, queue, slipperySlopes, depth)
    }

    override fun compareTo(other: Command): Int {
        return this.cursor.segments.size.compareTo(other.cursor.segments.size)
    }

}


