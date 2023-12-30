package day18

import base.BaseSolver
import base.INPUT_VARIANT
import base.Part
import util.grid.*
import java.lang.Error
import kotlin.IllegalArgumentException

fun main(args: Array<String>) {
    P18_Solver().solve(INPUT_VARIANT.REAL)
}

class P18_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "lagoon dig"
    }

    // answer: 68115
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
//        val answer_v1 = solvePart1_rowFill(inputLines, inputVariant)
//        println("answer v1: $answer_v1")
//        println("----------------")
        return solvePart1_shoeLace(inputLines, inputVariant)
    }

    fun solvePart1_rowFill(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
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

        val grid = Grid2D<Char>(maxRows-minRows+1, maxColumns-minColumns+1, '.')
        try {
            val cursor = GridCursor(grid, Coordinate(-minRows, -minColumns), instructions[0].direction, CursorPathStrategy.FULL_PATH, CursorGridStrategy.REGISTER_VISITED)
            instructions.forEach{
                cursor.move(it.direction, it.distance, true, '#')
            }

            grid.borderFill(cursor, true, '#', '%')
        } catch(e:Error) {
            throw e
        } finally {
            grid.print()
        }

        val context = mapOf("method" to "rowFill")
        return Pair(grid.count('#') + grid.count('%'), context)
    }

    fun solvePart1_shoeLace(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val cursor = Cursor()
        inputLines.forEach {
            val (directionRaw, distanceRaw, _) = it.split(" ")
            val direction = when (directionRaw) {
                "U" -> Direction.NORTH
                "D" -> Direction.SOUTH
                "L" -> Direction.WEST
                "R" -> Direction.EAST
                else -> throw IllegalArgumentException("$directionRaw is an unknown direction")
            }
            val distance = distanceRaw.toInt()
            cursor.move(direction, distance)
        }

//        println(cursor.getGridDimensions())

        val borderLength = cursor.pathLength()
        val surfaceWithinBorder = cursor.pathArea()

//        if (inputVariant == INPUT_VARIANT.EXAMPLE)
//            println("expected answer for test: 62")
//        else
//            println("expected answer for real: 68115")
        val context = mapOf("method" to "shoeLace")
        return Pair(surfaceWithinBorder + borderLength/2 + 1, context)
    }

    // answer: 71262565063800
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val cursor = Cursor()
        inputLines.forEach {
            val distanceHex = it.substring(it.length-7, it.length-2)
            val distance = Integer.decode("0x$distanceHex")
            val direction = when (it[it.length-2]) {
                '3' -> Direction.NORTH
                '1' -> Direction.SOUTH
                '2' -> Direction.WEST
                '0' -> Direction.EAST
                else -> throw IllegalArgumentException("${it[it.length-2]} is an unknown direction")
            }
            cursor.move(direction, distance)
        }

//        println(cursor.getGridDimensions())

        val borderLength = cursor.pathLength()
        val surfaceWithinBorder = cursor.pathArea()

//        if (inputVariant == INPUT_VARIANT.EXAMPLE)
//            println("expected for test: 952408144115")

        return surfaceWithinBorder + borderLength/2 + 1
    }
}

data class Instruction(val direction:Direction, val distance:Int)

