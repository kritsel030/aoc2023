package util.grid2d

import kotlin.math.absoluteValue

open class Coordinate(val rowNo:Int, val colNo:Int) {

    companion object {
        val ESWN = arrayOf(Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH)
    }

    fun move(direction:Direction, distance:Int = 1) : Coordinate{
//        println("Position.move ${rowNo} + ${colNo}")
        return when(direction) {
            Direction.NORTHWEST -> Coordinate(rowNo-distance, colNo-distance)
            Direction.NORTH -> Coordinate(rowNo-distance, colNo)
            Direction.NORTHEAST -> Coordinate(rowNo-distance, colNo+distance)
            Direction.EAST  -> Coordinate(rowNo, colNo+distance)
            Direction.SOUTHEAST -> Coordinate(rowNo+distance, colNo+distance)
            Direction.SOUTH -> Coordinate(rowNo+distance, colNo)
            Direction.SOUTHWEST -> Coordinate(rowNo+distance, colNo-distance)
            Direction.WEST  -> Coordinate(rowNo, colNo-distance)
        }
    }

    fun findNeighbours(includeDiagonalNeighbours:Boolean? = false, distance:Int = 1) : Map<Direction, Coordinate> {
        val directions = if (includeDiagonalNeighbours == true) Direction.values() else ESWN
        return findNeighbours(directions.toList(), distance)
    }

    fun findNeighbours(directions:List<Direction>, distance:Int = 1) : Map<Direction, Coordinate> {
        return directions.map { it to move(it, distance) }.toMap()
    }

    fun shortestDistance(otherCoordinate:Coordinate) : Int {
        return (this.rowNo-otherCoordinate.rowNo).absoluteValue + (this.colNo - otherCoordinate.colNo).absoluteValue
    }

    override fun toString() : String {
        return "{rowNo=$rowNo, colNo=$colNo}"
    }

    fun print() {
        println(this.toString())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinate

        if (rowNo != other.rowNo) return false
        if (colNo != other.colNo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rowNo
        result = 31 * result + colNo
        return result
    }


}