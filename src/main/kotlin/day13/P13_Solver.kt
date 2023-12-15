package day13

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid.ORIENTATION
import util.grid.Grid2D
import util.grid.Grid2DFactory
import java.lang.Integer.min

fun main(args: Array<String>) {
    P13_Solver().solve(INPUT_VARIANT.REAL)
}

class P13_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "mirrors"
    }

    // answer: 32723
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        return chunkInput(inputLines).sumOf {pattern ->
            // we use a 1-based index for this puzzle's grid
            // meaning that the index number for the first column/row is 1 and not 0
            val grid = Grid2DFactory.initCharGrid(pattern, 1)

            // find mirrors for both the horizontal and vertical orientation
            // and do the math based on the mirror indexes
            ORIENTATION.values().sumOf { orientation ->
                findMirrorsForOrientation(
                    grid,
                    orientation
                ).sumOf { gridIndex -> gridIndex * (if (orientation == ORIENTATION.VERTICAL) 1 else 100) }
            }
        }
   }

    private fun findMirrorsForOrientation(grid: Grid2D<Char>, orientation: ORIENTATION): List<Int> {
        val size = grid.size(orientation)

        // this puzzle's grid uses a 1-based index, so we start at 1
        val mirrorTestIndices = (1 .. size - 1)
            .filter {
                if (grid.getValues(orientation, it).equals(grid.getValues(orientation, it + 1))) {
                    // we've found two rows/columns next to each other who are identical
                    // now let's see if this is a real mirror
//                    println("potential $orientation mirror between indices $it and ${it + 1} ($orientation grid size = $size)")

                    // mirrorspan = the number of rows/columns the mirror should span
                    // so it reaches all the way till the border of the grid
                    val mirrorSpan = min(it, size-it)
//                    println("mirrorspan: $mirrorSpan")

                    // now check if each pair of rows/colums within the mirror span is identical
                    // (we count the pairs which AREN'T equal and check if that count is 0)
                    val mirrorFound = (0 until mirrorSpan).count { indexDelta ->
//                        print("compare $orientation indices ${it - indexDelta} and ${it + 1 + indexDelta}: ")
                        val result =
                            grid.getValues(orientation, it - indexDelta).equals(grid.getValues(orientation, it + 1 + indexDelta))
//                        println(result)
                        !result
                    } == 0
//                    println("mirror found: $mirrorFound")
                    mirrorFound
                } else {
                    false
                }
            }
        return mirrorTestIndices
    }


    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun chunkInput(inputLines: List<String>) : MutableList<MutableList<String>> {
        val newInputLines = inputLines.toMutableList()
        newInputLines.add("")

        var result:MutableList<MutableList<String>> = mutableListOf()
        var currentGroup = mutableListOf<String>()
        newInputLines.forEach {
            if (it.trim().isEmpty()) {
                if (!currentGroup.isEmpty()) {
                    result.add(currentGroup)
                    currentGroup = mutableListOf()
                }
            } else {
                currentGroup.add(it)
            }
        }
        return result
    }
}


