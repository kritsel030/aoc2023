package util.grid3d

import kotlin.math.abs

class Cursor(
    var currentCoordinate:Coordinate = Coordinate(0,0, 0),
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
        var xCount:Long = 0
        var xUp:Long = 0
        var xDown:Long = 0
        reversedPath
            .filter{it.travelledDirection == Direction.X_UP || it.travelledDirection == Direction.X_DOWN}
            .forEach {
                xCount += if (it.travelledDirection == Direction.X_UP) it.travelledDistance else -it.travelledDistance
                xUp = maxOf(xUp, xCount)
                xDown = minOf(xDown, xCount)
            }

        var yCount:Long = 0
        var yUp:Long = 0
        var yDown:Long = 0
        reversedPath
            .filter{it.travelledDirection == Direction.Y_UP || it.travelledDirection == Direction.Y_DOWN}
            .forEach {
                yCount += if (it.travelledDirection == Direction.Y_UP) it.travelledDistance else -it.travelledDistance
                yUp = maxOf(yUp, yCount)
                yDown = minOf(yDown, yCount)
            }

        var zCount:Long = 0
        var zUp:Long = 0
        var zDown:Long = 0
        reversedPath
            .filter{it.travelledDirection == Direction.Z_UP || it.travelledDirection == Direction.Z_DOWN}
            .forEach {
                zCount += if (it.travelledDirection == Direction.Z_UP) it.travelledDistance else -it.travelledDistance
                zUp = maxOf(zUp, zCount)
                zDown = minOf(zDown, zCount)
            }

        return mapOf(
            Direction.X_DOWN to -xDown,
            Direction.X_UP to xUp,
            Direction.Y_DOWN to -yDown,
            Direction.Y_UP to yUp,
            Direction.Z_DOWN to -zDown,
            Direction.Z_UP to zUp)
    }

    fun pathLength() : Long {
        return path.sumOf{it.travelledDistance.toLong()}
    }
}

enum class CursorPathStrategy {
    FULL_PATH,
    LATEST_ONLY,
    NO_PATH
}