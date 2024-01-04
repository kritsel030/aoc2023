package day22

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid3d.Coordinate
import util.grid3d.MutableGrid3D
import java.lang.IllegalStateException
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    P22_Solver().solve(INPUT_VARIANT.EXAMPLE)
}

class P22_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "falling bricks"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val bricks = parseInput(inputLines)
        settleBricks(bricks)

        // now let's see which bricks we can safely 'disintegrate'
        val disintegratableBricks = bricks
            .filter { it.fallenBricksWhenDisintegrated(bricks).isEmpty() }
            .map { it.id }

        println("disintegratable bricks: $disintegratableBricks")
        return disintegratableBricks.size
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val bricks = parseInput(inputLines)
        settleBricks(bricks)

        val answer = bricks.sumOf {it.fallenBricksWhenDisintegrated2(bricks).size  }
        return answer
    }

    fun parseInput(inputLines: List<String>) : List<Brick> {
        return inputLines.mapIndexed() { index, line ->
            val tuple = line.split("~").map{rawCoordinate ->
                val elems = rawCoordinate.split(',').map{it.toInt()}
                Coordinate(elems[0], elems[1], elems[2])
            }
            Brick(index, tuple.first(), tuple.last())
        }.toList()
    }

    private fun settleBricks(
        bricks: List<Brick>
    ) {
        val maxX = bricks.maxOf { max(it.firstCoordinate.x, it.secondCoordinate.x) } + 1
        val maxY = bricks.maxOf { max(it.firstCoordinate.y, it.secondCoordinate.y) } + 1
        val maxZ = bricks.maxOf { listOf(it.firstCoordinate.z, it.secondCoordinate.z, bricks.size).max() } + 1
        //        println("grid dimensions: x=$maxX, y=$maxY, z=$maxZ")

        val grid = MutableGrid3D(maxX, maxY, maxZ, ".")
        // fill the floor (z=0)
        (0 until maxX).forEach { x ->
            (0 until maxY).forEach { y ->
                grid.setValue(x, y, 0, "#")
            }
        }

        // settle the bricks
        val sortedBrickEntries = bricks.sortedBy { min(it.firstCoordinate.z, it.secondCoordinate.z) }
        sortedBrickEntries.forEachIndexed { sortIndex, brick ->
    //            val brickLabel = ('A'.code + brick.id).toChar().toString()
            val brickLabel = brick.id.toString()
            val footPrint = brick.footprint()
            var zLevel = sortIndex + 1
            lateinit var touchedFieldValues: MutableSet<String>
            while (zLevel >= 0) {
                touchedFieldValues = footPrint.map { grid.getValue(it.rowNo, it.colNo, zLevel) }.toMutableSet()
                touchedFieldValues.remove(".")
    //                touchedFieldValues.remove("#")
                if (touchedFieldValues.isNotEmpty()) {
                    break
                } else {
                    zLevel--
                }
            }

            if (zLevel < 0) {
                throw IllegalStateException("oops, zLevel=$zLevel for brick $brickLabel, that's too low")
            }
            // zLevel is the first level (from high to low) at which the brick does not fit
            // we need to +1 to get to the level which does fit
            // mark the grid cells which will hold this brick
            zLevel++
    //            println("brick $brickLabel goes to $zLevel")
            brick.zLevel = zLevel
            // mark the cells in the grid which hold this brick
            footPrint.forEach {
                (0..abs(brick.secondCoordinate.z - brick.firstCoordinate.z)).forEach { zDelta ->
                    grid.setValue(it.rowNo, it.colNo, zLevel + zDelta, brick.id.toString())
                }
            }
            // inform the brick by which other bricks it is being supported and vice versa
            touchedFieldValues.remove("#")
            brick.supportedBy = touchedFieldValues.map { it.toInt() }.toSet()
            brick.supportedBy.forEach { supportBrickId ->
                bricks.get(supportBrickId).supports.add(brick.id)
            }
        }
    }
}

class Brick(val id:Int, val firstCoordinate:Coordinate, val secondCoordinate: Coordinate) {
    fun footprint() : List<util.grid2d.Coordinate> {
        return (this.firstCoordinate.x .. this.secondCoordinate.x).flatMap { x ->
            (this.firstCoordinate.y .. this.secondCoordinate.y).map { y ->
                util.grid2d.Coordinate(x, y)
            }.toList()
        }
    }

    var zLevel = 0

    // brick IDs which support this brick
    var supportedBy:Set<Int> = setOf()

    var supports:MutableSet<Int> = mutableSetOf()

    fun fallenBricksWhenDisintegrated(bricks:List<Brick>, disintegratedBricks:MutableSet<Int> = mutableSetOf()) : Set<Int> {
        return if (this.supports.size == 0) {
            emptySet<Int>()
        } else {
            disintegratedBricks.add(this.id)
            this.supports.filter { supportedBrickId ->
//                bricks.get(supportedBrickId).supportedBy.size == 1
                bricks.get(supportedBrickId).supportedBy == disintegratedBricks
            }.toSet()
        }
    }

    fun fallenBricksWhenDisintegrated2(bricks:List<Brick>, disintegratedBricks:MutableSet<Int> = mutableSetOf(), depth:Int = 0) : Set<Int> {
        disintegratedBricks.add(this.id)
        var result =  fallenBricksWhenDisintegrated(bricks, disintegratedBricks).toMutableSet()
        disintegratedBricks.addAll(result)
        result.addAll(
                result.flatMap { fallingBrickId -> bricks.get(fallingBrickId).fallenBricksWhenDisintegrated2(bricks, disintegratedBricks, depth+1)}
            )
        if (depth == 0) {
            var prefix = StringBuilder()
            for (i in 0 until depth) {
                prefix.append(" ")
            }
            println("${prefix.toString()}disintegrating brick $id causes ${result.size} bricks to fall")
        }
        return result

    }
}


