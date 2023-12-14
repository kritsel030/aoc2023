package day11

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid.Coordinate
import util.grid.Grid2DFactory
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    P11_Solver().solve(INPUT_VARIANT.REAL)
}

class P11_Solver : BaseSolver() {

    // answer: 9724940
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initCharGrid(inputLines)

        // A. find the empty rows and columns
        val emptyRows = (0 until grid.gridValues.size)
            .filter{grid.getRowValues(it).count{cell -> !cell.equals('.')} == 0}
        val emptyColumns = (0 until grid.gridValues[0].size)
            .filter{grid.getColumnValues(it).count{cell -> !cell.equals('.')} == 0}

        // B. expand the grid
        emptyRows.forEachIndexed {index, rowNo ->
            grid.gridValues.add(rowNo+index, MutableList(grid.getRowValues(0).size){'.'})
        }
        emptyColumns.forEachIndexed {index, colNo ->
            grid.gridValues.forEach { row -> row.add(index+colNo, '.') }
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
        val grid = Grid2DFactory.initCharGrid(inputLines)

        // A. find the empty rows and columns
        val emptyRows = (0 until grid.gridValues.size)
            .filter{grid.getRowValues(it).count{cell -> !cell.equals('.')} == 0}
            .map{it.toLong()}
        val emptyColumns = (0 until grid.gridValues[0].size)
            .filter{grid.getColumnValues(it).count{cell -> !cell.equals('.')} == 0}
            .map{it.toLong()}

        // B. find all galaxy coordinates
        val galaxies = (0 until grid.gridValues.size)
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
                .map{rowNo -> if (emptyRows.contains(rowNo)) factor else 1}
                .sum()
            val colDistance = (colMin until colMax)
                .map{colNo -> if (emptyColumns.contains(colNo)) factor else 1}
                .sum()
            rowDistance + colDistance
        }.sum()}.sum()/2
    }
}


