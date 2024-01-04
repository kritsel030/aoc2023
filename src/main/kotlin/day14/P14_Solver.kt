package day14

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid2d.Grid2DFactory
import util.grid2d.ORIENTATION

fun main(args: Array<String>) {
    P14_Solver().solve(INPUT_VARIANT.REAL)
}

class P14_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "tilt platform"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initMutableCharGrid(inputLines)

//        grid.print()
//        println()

        (0 until grid.size(ORIENTATION.VERTICAL)).forEach { listIndex ->
            val list = grid.getValues(ORIENTATION.VERTICAL, listIndex).toMutableList()

            // roll down the rolling rocks
            val cubeIndices = list.mapIndexed { shapeIndex, shape -> if (shape == '#') shapeIndex else -1}.filter{it>= 0}
            if (!cubeIndices.isEmpty()) {
                // start to first cube rock
//                if (listIndex == 2)
//                    println("column $listIndex | start | roll down from 0 to ${cubeIndices.first()}")
                rollDownRoundRocks(list, 0, cubeIndices.first())
                // in between cube rocks
                (0 until cubeIndices.size-1).forEach{i ->
//                    if (listIndex == 2)
//                        println("column $listIndex | between | roll down from ${cubeIndices[i]+1} to ${cubeIndices[i+1]}")
                    rollDownRoundRocks(list, cubeIndices[i]+1, cubeIndices[i+1])
                }
                // last cube rock to end
//                if (listIndex == 2)
//                    println("column $listIndex | last | roll down from ${cubeIndices.last()+1} to ${list.size}")
                rollDownRoundRocks(list, cubeIndices.last()+1, list.size)
            } else {
//                if (listIndex == 2)
//                    println("column $listIndex | only | roll down from 0 to ${list.size}")
                rollDownRoundRocks(list, 0, list.size)
            }
            grid.replaceValues(ORIENTATION.VERTICAL, listIndex, list)
        }
//        grid.print()
        // calculate load
        return (0 until grid.size(ORIENTATION.HORIZONTAL)).sumOf { listIndex ->
            grid.getValues(ORIENTATION.HORIZONTAL, listIndex).count{it == 'O'} * (grid.size(ORIENTATION.HORIZONTAL)-listIndex)
        }
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun rollDownRoundRocks(shapes:MutableList<Char>, start:Int, end:Int) {
        val orderedSublist = shapes.subList(start, end).sorted().reversed()
        orderedSublist.forEachIndexed{index, item ->
            shapes[index+start] = orderedSublist[index]
        }
    }
}


