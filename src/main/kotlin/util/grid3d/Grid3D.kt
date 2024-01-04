package util.grid3d

import java.lang.IllegalArgumentException

open class Grid3D<T>(var gridValues:List<List<List<T>>>, val indexBase:Int = 0) {
    init {
        if (indexBase > 1)
            throw IllegalArgumentException("indexBase must be 1 or lower")
    }

    // initialize a grid of the supplied dimensions where each cell has value <initialValue>
    constructor(xCount: Int, yCount: Int, zCount: Int, initialValue: T, indexBase: Int = 0) :
            this(List(xCount) { List(yCount) {List(zCount) { initialValue } } }, indexBase) {
    }

    var visitedCoordinates: MutableMap<Coordinate, MutableMap<Any, Any>> = mutableMapOf()

    open fun size(orientation: ORIENTATION): Int {
        return when (orientation) {
            ORIENTATION.X -> return gridValues.size
            ORIENTATION.Y -> return gridValues[0].size
            ORIENTATION.Z -> return gridValues[0][0].size
        }
    }

    open fun dimensions(): Map<ORIENTATION, Int> {
        return ORIENTATION.values().associateWith { size(it) }
    }

    fun isValidPosition(coordinate: Coordinate): Boolean {
        return isValidPosition(coordinate.x, coordinate.y, coordinate.z)
    }

    open fun isValidPosition(x: Int, y: Int, z: Int): Boolean {
        val effectiveX = x - indexBase
        val effectiveY = y - indexBase
        val effectiveZ = z - indexBase
        if (effectiveX !in 0 until size(ORIENTATION.X)) {
            return false
        }
        if (effectiveY !in 0 until size(ORIENTATION.Y)) {
            return false
        }
        if (effectiveZ !in 0 until size(ORIENTATION.Z)) {
            return false
        }
        return true
    }

    open fun getValue(coordinate: Coordinate): T {
        return getValue(coordinate.x, coordinate.y, coordinate.z)
    }

    open fun getValue(x: Int, y: Int, z: Int): T {
        if (isValidPosition(x, y, z)) {
            val effectiveX = x - indexBase
            val effectiveY = y - indexBase
            val effectiveZ = z - indexBase
            return gridValues[effectiveX][effectiveY][effectiveZ]
        } else {
            throw IllegalArgumentException("coordinate $x, $y, $z is outside the bounds of this grid")
        }
    }

    open fun getSlice(orientation: ORIENTATION, id: Int): List<List<T>> {
        return when (orientation) {
            ORIENTATION.X -> gridValues[id]
            ORIENTATION.Y ->
                (0 until size(ORIENTATION.X))
                    .map { x ->
                        (0 until size(ORIENTATION.Z))
                            .map { z -> getValue(x, id, z)}
                            .toList()
                    }
                    .toList()
            ORIENTATION.Z ->
                (0 until size(ORIENTATION.X))
                    .map { x ->
                        (0 until size(ORIENTATION.Y))
                            .map { y -> getValue(x, y, id)}
                            .toList()
                    }
                    .toList()
        }
    }

    fun isVisited(coordinate: Coordinate, direction: Direction?): Boolean {
        return if (direction == null) {
            visitedCoordinates.containsKey(coordinate)
        } else {
            var result = visitedCoordinates.containsKey(coordinate) && (visitedCoordinates[coordinate]!![direction] == true)
            result
        }
    }

    fun reset() {
        visitedCoordinates = mutableMapOf()
    }

    open fun find(value: T): List<Coordinate> {
        return (0 until size(ORIENTATION.X))
            .flatMap { x ->
                (0 until size(ORIENTATION.Y))
                    .flatMap { y ->
                        (0 until size(ORIENTATION.Z))
                            .map { z ->
                                if (getValue(
                                        x + indexBase,
                                        y + indexBase,
                                        z + indexBase
                                    ) == value
                                ) Coordinate(x + indexBase, y + indexBase, z + indexBase) else null
                            }
                            .filterNotNull()
                            .toList()
                    }
            }
    }

    open fun count(value: T): Int {
        return (0 until size(ORIENTATION.X))
            .sumOf { x ->
                (0 until size(ORIENTATION.Y))
                    .sumOf { y ->
                        (0 until size(ORIENTATION.Z))
                            .count { z -> getValue(x + indexBase, y + indexBase, z + indexBase) == value}
                    }
            }
    }
}

enum class ORIENTATION {
    X,
    Y,
    Z
}