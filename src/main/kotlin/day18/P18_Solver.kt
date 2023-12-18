package day18

import base.BaseSolver
import base.INPUT_VARIANT
import base.Part
import util.grid.*
import java.lang.Error
import kotlin.IllegalArgumentException
import kotlin.math.min

fun main(args: Array<String>) {
    P18_Solver().solve(INPUT_VARIANT.EXAMPLE, Part.PART1)
}

class P18_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "lagoon dig"
    }

    // answer: 68115
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val instructions = inputLines.map {
            val (directionRaw, distanceRaw, _) = it.split(" ")
            val direction = when (directionRaw) {
                "U" -> Direction.NORTH
                "D" -> Direction.SOUTH
                "L" -> Direction.WEST
                "R" -> Direction.EAST
                else -> throw IllegalArgumentException("$directionRaw is an unknown direction")
            }
            val distance = distanceRaw.toInt()
            Instruction(direction, distance)
        }
        var columnCount = 0
        var maxColumns = 0
        var minColumns = 0
        instructions
            .filter{it.direction == Direction.WEST || it.direction == Direction.EAST}
            .forEach {
                columnCount += if (it.direction == Direction.EAST) it.distance else -it.distance
                maxColumns = maxOf(maxColumns, columnCount)
                minColumns = minOf(minColumns, columnCount)
            }

        var rowCount = 0
        var maxRows = 0
        var minRows = 0
        instructions
            .filter{it.direction == Direction.NORTH || it.direction == Direction.SOUTH}
            .forEach{
                rowCount += if (it.direction == Direction.SOUTH) it.distance  else -it.distance
                maxRows = maxOf(maxRows, rowCount)
                minRows = minOf(minRows, rowCount)
            }

        println("maxRows: $maxRows, maxColumns: $maxColumns")
        println("minRows: $minRows, minColumns: $minColumns")
//        listOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
//            .map{it to instructions.filter { instr -> instr.direction == it}.map{instr -> instr.distance}}

        val grid = Grid2D<Char>(maxRows-minRows+1, maxColumns-minColumns+1, '.')
        try {
            val cursor = Cursor(grid, Coordinate(-minRows, -minColumns), instructions[0].direction, CursorPathStrategy.FULL_PATH, CursorGridStrategy.REGISTER_VISITED)
            instructions.forEach{
                cursor.move(it.direction, it.distance, true, '#')
            }

//            grid.print()

            markInner(grid, cursor.path.reversed(), '#','%')
        } catch(e:Error) {
            throw e
        } finally {
            grid.print()
        }

        return grid.count('#') + grid.count('%')
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val instructions = inputLines.map {
            val distanceHex = it.substring(it.length-7, it.length-2)
            val distance = Integer.decode("0x$distanceHex")
            val direction = when (it[it.length-2]) {
                '3' -> Direction.NORTH
                '1' -> Direction.SOUTH
                '2' -> Direction.WEST
                '0' -> Direction.EAST
                else -> throw IllegalArgumentException("${it[it.length-2]} is an unknown direction")
            }
            Instruction(direction, distance)
        }
        var columnCount = 0
        var maxColumns = 0
        var minColumns = 0
        instructions
            .filter{it.direction == Direction.WEST || it.direction == Direction.EAST}
            .forEach {
                columnCount += if (it.direction == Direction.EAST) it.distance else -it.distance
                maxColumns = maxOf(maxColumns, columnCount)
                minColumns = minOf(minColumns, columnCount)
            }

        var rowCount = 0
        var maxRows = 0
        var minRows = 0
        instructions
            .filter{it.direction == Direction.NORTH || it.direction == Direction.SOUTH}
            .forEach{
                rowCount += if (it.direction == Direction.SOUTH) it.distance  else -it.distance
                maxRows = maxOf(maxRows, rowCount)
                minRows = minOf(minRows, rowCount)
            }

        println("maxRows: $maxRows, maxColumns: $maxColumns")
        println("minRows: $minRows, minColumns: $minColumns")
        println("dimensions: ${maxRows-minRows+1}, ${maxColumns-minColumns+1}")

        val grid = Grid2D<Char>(maxRows-minRows+1, maxColumns-minColumns+1, '.')
        try {
            val cursor = Cursor(grid, Coordinate(-minRows, -minColumns), instructions[0].direction, CursorPathStrategy.FULL_PATH, CursorGridStrategy.NO_VISITED)
            instructions.forEach{
                cursor.move(it.direction, it.distance, true, '#')
            }

//            grid.print()

            println("path size: ${cursor.path.size}")
            markInner(grid, cursor.path.reversed(), '#','%')
        } catch(e:Error) {
            throw e
        } finally {
//            grid.print()
        }

        return grid.count('#') + grid.count('%')
    }

    fun markInner(grid:Grid2D<Char>, path:List<VisitedCoordinate<Char>>, borderValue: Char, innerValue:Char) {
        path.forEachIndexed { index, pathElem ->
            if (index == path.size - 1 || !sharpCornerComingUp(path[index], path[index+1])) {
                val look:Direction = when (pathElem.travelledDirection) {
                    Direction.NORTH -> Direction.EAST
                    Direction.SOUTH -> Direction.WEST
                    Direction.EAST -> Direction.SOUTH
                    Direction.WEST -> Direction.NORTH
                    else -> throw IllegalArgumentException("${pathElem.travelledDirection} not supported")
                }

                var nextCoordinate = pathElem.coordinate
                while (true) {
                    nextCoordinate = nextCoordinate.move(look)
                    if (grid.isValidPosition(nextCoordinate) && grid.getValue(nextCoordinate) != borderValue) {
                        grid.setValue(nextCoordinate, innerValue)
                    } else {
                        break
                    }
                }
            }

            if (index != path.size-1 && bluntCornerComingUp(path[index], path[index+1])) {
                val look:Direction = when (path[index+1].travelledDirection) {
                    Direction.NORTH -> Direction.EAST
                    Direction.SOUTH -> Direction.WEST
                    Direction.EAST -> Direction.SOUTH
                    Direction.WEST -> Direction.NORTH
                    else -> throw IllegalArgumentException("${path[index+1].travelledDirection} not supported")
                }
                var nextCoordinate = pathElem.coordinate
                while (true) {
                    nextCoordinate = nextCoordinate.move(look)
                    if (grid.isValidPosition(nextCoordinate) && grid.getValue(nextCoordinate) != borderValue) {
                        grid.setValue(nextCoordinate, innerValue)
                    } else {
                        break
                    }
                }
            }
        }
    }
//    companion object {
//        val INNER_DIRECTION:Map<Pair<Direction, Direction>, Direction> = mapOf(
//            Pair(Direction.EAST, Direction.NORTH) to
//        )
//    }

    fun sharpCornerComingUp(pathElem1:VisitedCoordinate<Char>, pathElem2: VisitedCoordinate<Char>) : Boolean {
        return ( (pathElem1.travelledDirection == Direction.EAST && pathElem2.travelledDirection == Direction.SOUTH)
                || (pathElem1.travelledDirection == Direction.SOUTH && pathElem2.travelledDirection == Direction.WEST)
                || (pathElem1.travelledDirection == Direction.WEST && pathElem2.travelledDirection == Direction.NORTH)
                || (pathElem1.travelledDirection == Direction.NORTH && pathElem2.travelledDirection == Direction.EAST))
    }

    fun bluntCornerComingUp(pathElem1:VisitedCoordinate<Char>, pathElem2: VisitedCoordinate<Char>) : Boolean {
        return ( (pathElem1.travelledDirection == Direction.EAST && pathElem2.travelledDirection == Direction.NORTH)
                || (pathElem1.travelledDirection == Direction.SOUTH && pathElem2.travelledDirection == Direction.EAST)
                || (pathElem1.travelledDirection == Direction.WEST && pathElem2.travelledDirection == Direction.SOUTH)
                || (pathElem1.travelledDirection == Direction.NORTH && pathElem2.travelledDirection == Direction.WEST))
    }
}

data class Instruction(val direction:Direction, val distance:Int)

