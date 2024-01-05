package util.grid2d

enum class Direction(val degrees:Int) {

    NORTH(0),
    NORTHEAST (45),
    EAST (90),
    SOUTHEAST (135),
    SOUTH (180),
    SOUTHWEST (225),
    WEST (270),
    NORTHWEST(315);

    fun opposite() : Direction {
        return map[(this.degrees + 180) % 360]!!
    }

    companion object {
        private val map: Map<Int, Direction> = Direction.values().associateBy { it.degrees }
    }
}