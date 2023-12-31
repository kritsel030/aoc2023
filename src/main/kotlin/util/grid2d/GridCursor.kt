package util.grid2d

import java.lang.IllegalStateException

open class GridCursor<T>(
    val grid:Grid2D<T>,
    var currentCoordinate:Coordinate,
    startDirection:Direction? = null,
    var pathStrategy:CursorPathStrategy = CursorPathStrategy.FULL_PATH,
    var gridStrategy: CursorGridStrategy = CursorGridStrategy.REGISTER_VISITED) {

    var path = mutableListOf<VisitedGridCoordinate<T>>()
    var costs = 0

    init {
        when (pathStrategy) {
            CursorPathStrategy.FULL_PATH -> path.add(VisitedGridCoordinate<T>(currentCoordinate, startDirection, null, grid.getValue(currentCoordinate)))
            CursorPathStrategy.LATEST_ONLY -> path.add(VisitedGridCoordinate<T>(currentCoordinate, startDirection, null, grid.getValue(currentCoordinate)))
            else -> {}
        }
        when (gridStrategy) {
            CursorGridStrategy.REGISTER_VISITED -> {
                // inform the grid that one of its coordinates has been visited
                val visitedBefore = grid.visitedCoordinates[currentCoordinate]
                if (visitedBefore != null) {
                    // we've been here before
                    visitedBefore["count"] = (visitedBefore["count"]!! as Int) + 1
                    if (startDirection != null) {
                        visitedBefore[startDirection] = true
                    }
                } else {
                    // we're new here
                    val visitedCoordinateDetails: MutableMap<Any, Any> = mutableMapOf("count" to 1)
                    if (startDirection != null) {
                        visitedCoordinateDetails[startDirection] = true
                    }
                    grid.visitedCoordinates[currentCoordinate] = visitedCoordinateDetails
                }
            }
            else -> {}
        }

    }

    open fun clone() : GridCursor<T> {
        var clone = GridCursor(grid, currentCoordinate, latestDirection())
        if (pathStrategy == CursorPathStrategy.FULL_PATH) {
            clone.path = path.toMutableList()
        }
        return clone
    }

    open fun canMove(direction:Direction, distance:Int = 1) : Boolean {
//        println("Cursor.canMove ${position.rowNo} + ${position.colNo}")
        val newPos = currentCoordinate.move(direction, distance)
        return grid.isValidPosition(newPos.rowNo, newPos.colNo)
    }

    fun peek(direction:Direction, distance: Int = 1):T {
        if (canMove(direction, distance)) {
            val newPos = currentCoordinate.move(direction, distance)
            return grid.getValue(newPos.rowNo, newPos.colNo)
        }
        throw IllegalStateException("sorry, the coordinate $distance steps in $direction direction is out of bounds")
    }

    fun move(direction:Direction, distance: Int = 1, visitCoordinatesInBetween: Boolean = true, visitedValue:T? = null) {
//        println("Cursor.move ${position.rowNo} + ${position.colNo}")
        if (visitCoordinatesInBetween) {
            (1 .. distance).forEach { _ -> move(direction, 1, false, visitedValue) }
        } else {
            if (canMove(direction, distance)) {
                // move returns a new Coordinate instance
                val newCoordinate = currentCoordinate.move(direction, distance)
                moveTo(newCoordinate, direction, distance, visitedValue)

            } else {
                throw IllegalStateException("sorry, the coordinate $distance steps in $direction direction is out of bounds (current coordinate is ${currentCoordinate.rowNo}, ${currentCoordinate.colNo})")
            }
        }
    }

    fun moveTo(
        newCoordinate: Coordinate,
        direction: Direction,
        distance: Int = 1,
        visitedValue: T? = null,
    ) {
        currentCoordinate = newCoordinate
        if (visitedValue != null) {
            if (grid is MutableGrid2D) {
                grid.setValue(currentCoordinate, visitedValue!!)
            }
        }
        when (gridStrategy) {
            CursorGridStrategy.REGISTER_VISITED -> {
                // inform the grid that one of its coordinates has been visited
                val visitedBefore = grid.visitedCoordinates[currentCoordinate]
                if (visitedBefore != null) {
                    // we've been here before
                    visitedBefore[direction] = true
                    visitedBefore["count"] = (visitedBefore["count"]!! as Int) + 1
                } else {
                    // we're new here
                    val visitedCoordinateDetails: MutableMap<Any, Any> =
                        mutableMapOf(
                            direction to true,
                            "count" to 1
                        )
                    grid.visitedCoordinates[currentCoordinate] = visitedCoordinateDetails
                }
            }

            else -> {}
        }
        when (pathStrategy) {
            CursorPathStrategy.FULL_PATH ->
                // add to the start of the path, so the path starts at the most recent element and reads backwards
                path.add(
                    0,
                    VisitedGridCoordinate<T>(currentCoordinate, direction, distance, grid.getValue(currentCoordinate))
                )

            CursorPathStrategy.LATEST_ONLY ->
                // overwrite the current single path entry
                path.set(
                    0,
                    VisitedGridCoordinate<T>(currentCoordinate, direction, distance, grid.getValue(currentCoordinate))
                )

            else -> {}
        }
    }


    // determine the neighbours you can actually move to from the current cursor position in the grid
    fun getNeighbours(includeDiagonalNeighbours:Boolean? = false, distance:Int = 1) : Map<Direction, Coordinate> {
        return currentCoordinate.findNeighbours(includeDiagonalNeighbours, distance).filter{grid.isValidPosition(it.value)}
    }

    fun getNeighbours(directions:List<Direction>, distance: Int = 1) : Map<Direction, Coordinate> {
        return currentCoordinate.findNeighbours(directions, distance).filter{grid.isValidPosition(it.value)}
    }

    override fun toString(): String {
        return "{value=${grid.getValue(currentCoordinate)}, currentCoordinate=$currentCoordinate, latestDirection=${latestDirection()}, pathLength=${path.size}"
    }

    fun print(printPath:Boolean? = false) {
        println(this)
        if (printPath == true) {
            path.forEachIndexed{ index, value -> println("$index | $ value")}
        }
    }

    fun latestDirection() : Direction?{
        return if (!path.isEmpty()) {
            path[0].travelledDirection
        } else {
            null
        }
    }

    fun latestDistance() : Int?{
        return path[0].travelledDistance
    }

    fun getValue() : T{
        return grid.getValue(currentCoordinate)
    }

    fun isAt(rowNo:Int, colNo:Int) : Boolean {
        return this.currentCoordinate.rowNo == rowNo && this.currentCoordinate.colNo == colNo
    }

    open fun hasVisited(rowNo:Int, colNo:Int) : Boolean {

        return this.path.map{it.coordinate}.contains(Coordinate(rowNo, colNo))
    }

}

enum class CursorGridStrategy {
    REGISTER_VISITED,
    NO_VISITED
}


