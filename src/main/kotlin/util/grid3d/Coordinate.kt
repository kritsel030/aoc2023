package util.grid3d

import kotlin.math.absoluteValue

open class Coordinate(val x:Int, val y:Int, val z:Int) {

    fun move(direction:Direction, distance:Int = 1) : Coordinate{
        return Coordinate(this.x + direction.xDelta, y + direction.yDelta, z + direction.zDelta)
    }

    fun findNeighbours(directions:List<Direction>, distance:Int = 1) : Map<Direction, Coordinate> {
        return directions.map { it to move(it, distance) }.toMap()
    }

    fun shortestDistance(otherCoordinate:Coordinate) : Int {
        return (this.x-otherCoordinate.x).absoluteValue +
                (this.y - otherCoordinate.y).absoluteValue +
                (this.z - otherCoordinate.z).absoluteValue
    }

    override fun toString() : String {
        return "{x=$x, y=$y, z=$z}"
    }

    fun print() {
        println(this.toString())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinate

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }
}
