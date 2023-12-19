package day13

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid.ORIENTATION
import util.grid.Grid2D
import util.grid.Grid2DFactory
import java.lang.IllegalArgumentException
import java.lang.Integer.min

fun main(args: Array<String>) {
    P13_Solver().solve(INPUT_VARIANT.EXAMPLE)
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
                findMirrorsForOrientation1(
                    grid,
                    orientation
                ).sumOf { gridIndex -> gridIndex * (if (orientation == ORIENTATION.VERTICAL) 1 else 100) }
            }
        }
   }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val pattern2 = chunkInput(inputLines)[1]
        val grid2= Grid2DFactory.initCharGrid(pattern2, 1)
        println("col 2: ${grid2.getValues(ORIENTATION.VERTICAL, 2)}")
        println("col 5: ${grid2.getValues(ORIENTATION.VERTICAL, 5)}")
        println("differences: ${differences(grid2.getValues(ORIENTATION.VERTICAL, 2), grid2.getValues(ORIENTATION.VERTICAL, 5))}")

        return chunkInput(inputLines).sumOf {pattern ->
            // we use a 1-based index for this puzzle's grid
            // meaning that the index number for the first column/row is 1 and not 0
            val grid = Grid2DFactory.initCharGrid(pattern, 1)

            // find mirrors for both the horizontal and vertical orientation
            // and do the math based on the mirror indexes
            ORIENTATION.values().sumOf { orientation ->
                findMirrorsForOrientation2(
                    grid,
                    orientation
                ).sumOf { gridIndex -> gridIndex * (if (orientation == ORIENTATION.VERTICAL) 1 else 100) }
            }
        }
    }

    private fun findMirrorsForOrientation1(grid: Grid2D<Char>, orientation: ORIENTATION): List<Int> {
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
                    val mirrorFound = isMirrorPresent(grid, it, orientation, mirrorSpan)
                    //                    println("mirror found: $mirrorFound")
                    mirrorFound
                } else {
                    false
                }
            }
        return mirrorTestIndices
    }

    private fun findMirrorsForOrientation2(grid: Grid2D<Char>, orientation: ORIENTATION): List<Int> {
        val size = grid.size(orientation)

        // this puzzle's grid uses a 1-based index, so we start at 1
        val potentialMirrorOuterIndices = (1 .. size)
            .map { index1 ->
                index1 to (1..size)
                    .filter { index2 ->
                        index1 != index2
                                && index1 < index2
                                && (index2-index1-1)%2 == 0
                                && differences(grid.getValues(orientation, index1), grid.getValues(orientation, index2)).size==1
                    }
            }
            .toMap().filter { (key, value) -> value.isNotEmpty() }

        println("[$orientation] potentialMirrorOuterIndices: $potentialMirrorOuterIndices")

        val indices = potentialMirrorOuterIndices.map {(index1, others) ->
            others.map { otherIndex ->
                val mirrorSpan = (otherIndex - index1 - 1) / 2
                val centerIndex = index1 + mirrorSpan
                if (isMirrorPresent(grid, centerIndex, orientation, mirrorSpan-1)) centerIndex else -1
            }.filter { it >= 0 }
        }.flatten()

        println("findMirrorsForOrientation2(grid, $orientation) = $indices")
        return indices
    }

    // return the indices on which the values in both lists differ
    fun differences(list1:List<Char>, list2:List<Char>) : List<Int> {
        if (list1.size != list2.size)
            throw IllegalArgumentException("lists must be the same size to be compared")

        return list1.indices.filter {list1[it] != list2[it] }
    }


    private fun isMirrorPresent(
        grid: Grid2D<Char>,
        startIndex: Int,
        orientation: ORIENTATION,
        mirrorSpan: Int
    ): Boolean {
        val mirrorFound = (0 until mirrorSpan).count { indexDelta ->
    //                        print("compare $orientation indices ${it - indexDelta} and ${it + 1 + indexDelta}: ")
            val result =
                grid.getValues(orientation, startIndex - indexDelta).equals(grid.getValues(orientation, startIndex + 1 + indexDelta))
    //                        println(result)
            !result
        } == 0
        return mirrorFound
    }

}


