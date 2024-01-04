package day11

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid2d.ORIENTATION
import util.grid2d.Coordinate
import util.grid2d.Grid2DFactory
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    P11_Solver().solve(INPUT_VARIANT.REAL)
}

class P11_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "cosmic expansion"
    }

    // answer: 9724940
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initMutableCharGrid(inputLines)

        // expand the grid, do the same for the horizontal (rows) and vertical (columns) orientation
        ORIENTATION.values().forEach { orientation ->
            (0 until grid.size(orientation))
                // A find the indices of the 'empty' rows/columns
                .filter{grid.getValues(orientation, it).count{cell -> cell != '.' } == 0}
                // B expand the grid for every empty row/column found
                .forEachIndexed {index, emptyOrientationIndex ->
                    grid.addValues(orientation, emptyOrientationIndex + index, '.')
            }
        }

//        grid.print()

        // C. find all galaxy coordinates
        val galaxies = (0 until grid.gridValues.size)
            .map { rowNo -> grid.getRowValues(rowNo)
                .mapIndexed{colNo, value -> if (value == '#') Coordinate(rowNo, colNo) else null}
                .filterNotNull()
                .toList() }
            .flatten()

        // D. calculate distances
        return galaxies.map { galaxy1 -> galaxies.map { galaxy2 -> galaxy1.shortestDistance(galaxy2) }.sum()}.sum()/2
    }

    // answer: 569052586852
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initMutableCharGrid(inputLines)

        // A. find the empty rows and columns
        val emptyOrientationsIndices:Map<ORIENTATION, List<Long>> = ORIENTATION.values()
            .map { orientation ->
                orientation to (0 until grid.size(orientation))
                    // A find the 'empty' rows/columns
                    .filter { grid.getValues(orientation, it).count { cell -> !cell.equals('.') } == 0 }
                    .map{it.toLong()}
            }
            .toMap()


        // B. find all galaxy coordinates
        val galaxies = (0 until grid.rowCount())
            .map { rowNo -> grid.getRowValues(rowNo)
                .mapIndexed{colNo, value -> if (value == '#') Coordinate(rowNo, colNo) else null}
                .filterNotNull()
                .toList() }
            .flatten()

        val factor:Long = 1000000

        // C. calculate distances
        return galaxies.map { galaxy1 -> galaxies.map { galaxy2 ->
            val rowMin = min(galaxy1.rowNo, galaxy2.rowNo).toLong()
            val rowMax = max(galaxy1.rowNo, galaxy2.rowNo).toLong()
            val colMin = min(galaxy1.colNo, galaxy2.colNo).toLong()
            val colMax = max(galaxy1.colNo, galaxy2.colNo).toLong()
            // process rowIDs
            val rowDistance = (rowMin until rowMax)
                .map{rowNo -> if (emptyOrientationsIndices[ORIENTATION.HORIZONTAL]!!.contains(rowNo)) factor else 1}
                .sum()
            // process colIDs
            val colDistance = (colMin until colMax)
                .map{colNo -> if (emptyOrientationsIndices[ORIENTATION.VERTICAL]!!.contains(colNo)) factor else 1}
                .sum()
            rowDistance + colDistance
        }.sum()}.sum()/2
    }
}


