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
        move(cursor, Direction.SOUTH, Segment(start, Direction.SOUTH), grid, nodes, segments)
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
             segments:MutableMap<Pair<Coordinate, Direction>, Segment>) : Int {
        return jumpTo(cursor, cursor.currentCoordinate.move(direction), direction, segment, grid, nodes, segments)
    }

    fun jumpTo(cursor: HikeCursor,
               newCoordinate:Coordinate,
               direction: Direction,
               segment:Segment,
               grid:Grid2D<Char>,
               nodes:MutableList<Coordinate>,
               segments:MutableMap<Pair<Coordinate, Direction>, Segment>) : Int {
        cursor.moveTo(newCoordinate, direction)

        val currentPathLength = cursor.length()
        val target = Coordinate(grid.rowCount()-1, grid.colCount() - 2)

        if (cursor.currentCoordinate == target) {
            println("hit target at $currentPathLength")
            return currentPathLength
        }

        // are we new here? or have we been here before, but via a shorter path?
        if (! grid.isVisited(cursor.currentCoordinate) || currentPathLength > (grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"] as Int) ) {
            grid.visitedCoordinates[cursor.currentCoordinate]!!["longest"] = currentPathLength
            println("hit ${cursor.currentCoordinate} at $currentPathLength")

            val validNeighbours = validNeighbours(cursor, grid)
            if (validNeighbours.size > 1) {
                println("${cursor.currentCoordinate} is a node (${validNeighbours.size} valid neighbours)")
                // we've hit a node
                // end and process the current segment
                segment.oneway = if (cursor.getValue() != '.') true else segment.oneway
                segment.coordinates.add(cursor.currentCoordinate)
                segment.end = cursor.currentCoordinate
                segment.endDirection = direction
                segments[Pair(segment.start, segment.startDirection)] = segment
                // add the segment to the cursor
                cursor.segments.add(segment)
                // process the inverse of this segment
                val inverseSegment = segment.inverse()
                segments[Pair(inverseSegment.start, inverseSegment.startDirection)] = inverseSegment
                // add the node
                nodes.add(cursor.currentCoordinate)

                validNeighbours.maxOf {
                    // do we have segment data for this node which we can use?
                    if (segments.containsKey(Pair(cursor.currentCoordinate, it.key))) {
                        println("use segment data for ${cursor.currentCoordinate} and ${it.key}")
                        val segment = segments[(Pair(cursor.currentCoordinate, it.key))]!!
                        // add the segment to the cursor
                        cursor.segments.add(segment)
                        // jump to segment end
                        jumpTo(cursor, it.value, it.key, segment, grid, nodes, segments)
                    } else {
                        move(cursor, it.key, Segment(cursor.currentCoordinate, it.key), grid, nodes, segments)
                    }
                }
            } else if (validNeighbours.size == 1) {
                if (cursor.getValue() != '.') {
                    segment.oneway = true
                }
                segment.coordinates.add(cursor.currentCoordinate)
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

    fun validNeighbours (cursor: HikeCursor, grid: Grid2D<Char>) : Map<Direction, Coordinate> {
        return cursor.getNeighbours().filter {
                    // do not revisit a tile we've visited before with this cursor
                    cursor.path.none { visCoord -> visCoord.coordinate == it.value } &&
                    cursor.segments.none { seg -> seg.coordinates.none { coord -> coord == it.value } } &&
                    // do not visit a tile which represents forest
                    grid.getValue(it.value) != '#' &&
                    (grid.getValue(it.value) == '.' ||
                    // adhere to the forced directions
                     grid.getValue(it.value) == '>' && it.key == Direction.EAST ||
                     grid.getValue(it.value) == 'v' && it.key == Direction.SOUTH ||
                     grid.getValue(it.value) == '<' && it.key == Direction.WEST ||
                     grid.getValue(it.value) == '^' && it.key == Direction.NORTH)

                    // do not visit any tiles which will force you to go back the way you came
//                    (it.key == Direction.EAST && grid.getValue(it.value) != '<') &&
//                    (it.key == Direction.WEST && grid.getValue(it.value) != '>') &&
//                    (it.key == Direction.NORTH && grid.getValue(it.value) != 'v') &&
//                    (it.key == Direction.SOUTH && grid.getValue(it.value) != '^')
        }
    }
}

class HikeCursor(grid: Grid2D<Char>, startCoordinate: Coordinate) : GridCursor<Char>(grid, startCoordinate) {
    val segments = mutableListOf<Segment>()

    fun length() : Int{
        return path.size + segments.sumOf { it.length }
    }
}

class Segment(
    val start:Coordinate,
    val startDirection: Direction,
    var oneway: Boolean? = null,
    val coordinates:MutableSet<Coordinate> = mutableSetOf(start),
    var end:Coordinate? = null,
    var endDirection: Direction? = null,
    var length:Int = 1
) {
    fun inverse() : Segment {
        return Segment(
                this.end!!,
                this.endDirection!!.opposite(),
                if (this.oneway == null) null else !this.oneway!!,
                this.coordinates,
                this.start,
                this.startDirection.opposite(),
                this.length)

    }
}


