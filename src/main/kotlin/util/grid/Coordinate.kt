package util.grid

open class Coordinate(val rowNo:Int, val colNo:Int) {

    companion object {
        val NESW = arrayOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    }

    fun move(direction:Direction, distance:Int? = 1) : Coordinate{
//        println("Position.move ${rowNo} + ${colNo}")
        val dist = distance?:0
        return when(direction) {
            Direction.NORTHWEST -> Coordinate(rowNo-dist, colNo-dist)
            Direction.NORTH -> Coordinate(rowNo-dist, colNo)
            Direction.NORTHEAST -> Coordinate(rowNo-dist, colNo+dist)
            Direction.EAST  -> Coordinate(rowNo, colNo+dist)
            Direction.SOUTHEAST -> Coordinate(rowNo+dist, colNo+dist)
            Direction.SOUTH -> Coordinate(rowNo+dist, colNo)
            Direction.SOUTHWEST -> Coordinate(rowNo+dist, colNo-dist)
            Direction.WEST  -> Coordinate(rowNo, colNo-dist)
        }
    }

    fun findNeighbours(includeDiagonalNeighbours:Boolean? = false, distance:Int? = 1) : Map<Direction, Coordinate> {
        val directions = if (includeDiagonalNeighbours == true) Direction.values() else NESW
        return findNeighbours(directions.toList(), distance)
    }

    fun findNeighbours(directions:List<Direction>, distance:Int? = 1) : Map<Direction, Coordinate> {
        return directions.map { it to move(it, distance) }.toMap()
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