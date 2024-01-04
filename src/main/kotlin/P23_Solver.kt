import base.BaseSolver
import base.INPUT_VARIANT
import util.grid2d.*

fun main(args: Array<String>) {
    P23_Solver().solve(INPUT_VARIANT.EXAMPLE)
}

class P23_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "long walk"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initCharGrid(inputLines)
        val nodes = mutableListOf<Coordinate>()

        // key: start coordinate of the section + direction to the next tile
        // value: end coordinate of the section + plus the distance from start to finish (excluding the end coordinate)
        val segments = mutableMapOf<Pair<Coordinate, Direction>, Segment>()

        val start = Coordinate(0, 1)
        val cursor = HikeCursor(grid, start)
        nodes.add(start)
        move(cursor, Direction.SOUTH, Segment(start, Direction.SOUTH, null), grid, nodes, segments)
        return "TODO"
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun move(cursor: HikeCursor,
             direction: Direction,
             segment:Segment,
             grid:Grid2D<Char>,
             nodes:MutableList<Coordinate>,
             segments:MutableMap<Pair<Coordinate, Direction>, Segment>) : Int{
        cursor.move(direction)
        // TODO: make cursor smarter with segment lengths
        val currentPathLength = cursor.length()
        val target = Coordinate(grid.rowCount()-1, grid.colCount() - 2)

        if (cursor.currentCoordinate == target) {
            return currentPathLength
        }

        // are we new here? or have we been here before, but via a shorter path?
        if (! grid.isVisited(cursor.currentCoordinate) || currentPathLength > (grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"] as Int) ) {
            grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"] = currentPathLength

            val validNeighbours = validNeighbours(cursor, grid)
            if (validNeighbours.size > 1) {
                // we've hit a node
                // end the current segment
                segment.oneway = if (cursor.getValue() != '.') true else segment.oneway
                segment.end = cursor.currentCoordinate
                segments[Pair(segment.start, segment.startDirection)] = segment
                // TODO: inverse latestDirection, oneway
//                val inverseSegment =
//                    Segment(segment.end, cursor.latestDirection(), segment.oneway, segment.start, segment.length)
//                segments[Pair(inverseSegment.start, inverseSegment.startDirection)] = inverseSegment
                nodes.add(cursor.currentCoordinate)

                // add the segment to the cursor
                cursor.segments.add(segment)

                // do we have segment data for this node which we can use?
                // TODO
                validNeighbours.maxOf {
                    if (segments.containsKey(Pair(cursor.currentCoordinate, it.key))) {
                        val segment = segments[(Pair(cursor.currentCoordinate, it.key))]!!
                        cursor.segments.add(segment)
                        // jump to segment end
                        // TODO
                    } else {
                        return move(cursor, it.key, Segment(cursor.currentCoordinate, it.key, null), grid, nodes, segments)
                    }
                }
            } else if (validNeighbours.size == 1) {

                if (cursor.getValue() != '.') {
                    segment.oneway = true
                }
                segment.length++
                return move(cursor, validNeighbours.keys.first(), segment, grid, nodes, segments)
            } else {
                // we've reached a dead end
            }
        } else {
            // we were here before, via a longer path, so no need to further explore our current path
        }
        return -1
    }

    fun validNeighbours (cursor: GridCursor<Char>, grid: Grid2D<Char>) : Map<Direction, Coordinate> {
        return cursor.getNeighbours().filter {
            grid.getValue(it.value) != '#' &&
                    (it.key == Direction.EAST && grid.getValue(it.value) != '<') &&
                    (it.key == Direction.WEST && grid.getValue(it.value) != '>') &&
                    (it.key == Direction.NORTH && grid.getValue(it.value) != 'v') &&
                    (it.key == Direction.SOUTH && grid.getValue(it.value) != '^')
            // TODO filter out where we came from
        }
    }
}

class HikeCursor(grid: Grid2D<Char>, startCoordinate: Coordinate) : GridCursor<Char>(grid, startCoordinate) {
    val segments = mutableListOf<Segment>()

    fun length() : Int{
        return path.size + segments.sumOf { it.length }
    }
}

data class Segment(
    val start:Coordinate,
    val startDirection: Direction,
    var oneway: Boolean?,
    var end:Coordinate? = null,
    var length:Int = 1
)


