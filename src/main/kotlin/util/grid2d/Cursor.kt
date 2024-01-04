package util.grid2d

import kotlin.math.abs

class Cursor(
    var currentCoordinate:Coordinate = Coordinate(0,0),
    startDirection:Direction? = null,
    var pathStrategy:CursorPathStrategy = CursorPathStrategy.FULL_PATH) {

    var path = mutableListOf<VisitedCoordinate>()

    init {
        when (pathStrategy) {
            CursorPathStrategy.FULL_PATH -> path.add(VisitedCoordinate(currentCoordinate, null, 0))
            CursorPathStrategy.LATEST_ONLY -> path.add(VisitedCoordinate(currentCoordinate, null, 0))
            else -> {}
        }
    }

    fun move(direction:Direction, distance: Int = 1) {
//        println("Cursor.move ${position.rowNo} + ${position.colNo}")

        // move returns a new Coordinate instance
        currentCoordinate = currentCoordinate.move(direction, distance)
        when (pathStrategy) {
            CursorPathStrategy.FULL_PATH ->
                // add to the start of the path, so the path starts at the most recent element and reads backwards
                path.add(
                    0,
                    VisitedCoordinate(
                        currentCoordinate,
                        direction,
                        distance
                    )
                )

            CursorPathStrategy.LATEST_ONLY ->
                // overwrite the current single path entry
                path.set(
                    0,
                    VisitedCoordinate(
                        currentCoordinate,
                        direction,
                        distance
                    )
                )
            else -> {}
        }
    }

    // returns the number of rows/columns to the south/norths/east/west of the start coordinate of this cursor's path
    fun getGridDimensions() : Map<Direction,Long>{
        val reversedPath = path.reversed()
        var columnCount:Long = 0
        var eastColumns:Long = 0
        var westColumns:Long = 0
        reversedPath
            .filter{it.travelledDirection == Direction.WEST || it.travelledDirection == Direction.EAST}
            .forEach {
                columnCount += if (it.travelledDirection == Direction.EAST) it.travelledDistance else -it.travelledDistance
                eastColumns = maxOf(eastColumns, columnCount)
                westColumns = minOf(westColumns, columnCount)
            }

        var rowCount:Long = 0
        var southRows:Long = 0
        var northRows:Long = 0
        reversedPath
            .filter{it.travelledDirection== Direction.NORTH || it.travelledDirection == Direction.SOUTH}
            .forEach{
                rowCount += if (it.travelledDirection == Direction.SOUTH) it.travelledDistance  else -it.travelledDistance
                southRows = maxOf(southRows, rowCount)
                northRows = minOf(northRows, rowCount)
            }

        return mapOf(Direction.NORTH to -northRows, Direction.SOUTH to southRows, Direction.WEST to -westColumns, Direction.EAST to eastColumns)
    }

    fun pathLength() : Long {
        return path.sumOf{it.travelledDistance.toLong()}
    }

    // https://gamedev.stackexchange.com/questions/151034/how-to-compute-the-area-of-an-irregular-shape
    // https://en.m.wikipedia.org/wiki/Shoelace_formula
    /*
    A = 0
    for (i = 0; i < points.length; i++) do
        A += points[i].x * points[(i + 1) % points.length].y - points[i].y * points[(i + 1) % points.length].x
    end
    A /= 2
    */
    fun pathArea() : Long {
        val newPath = path.reversed().toMutableList()
        if (newPath.first().coordinate != newPath.last().coordinate) newPath.add(path.first())
        val area =  abs(
            (0 until newPath.size-1)
                .sumOf {
                    val point = newPath[it].coordinate
                    val nextPoint = newPath[(it + 1) ].coordinate
                    val result = (point.rowNo.toLong() * nextPoint.colNo.toLong() - point.colNo.toLong() * nextPoint.rowNo.toLong()).toLong()
//                    println("shoelace element: $point to $nextPoint -> $result")
                    result
                }
        ) / 2
        return area
    }
}

enum class CursorPathStrategy {
    FULL_PATH,
    LATEST_ONLY,
    NO_PATH
}