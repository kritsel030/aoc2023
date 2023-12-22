package day21

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid.Coordinate
import util.grid.Grid2DFactory
import util.grid.InfiniteGrid

fun main(args: Array<String>) {
    P21_Solver().solve(INPUT_VARIANT.REAL)
}

class P21_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "garden plots"
    }

    // answer: 3764
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initCharGrid(inputLines)
        println("rows: ${grid.rowCount()}, columns: ${grid.colCount()}")
        val start = grid.find('S').first()
        grid.setValue(start, 'O')

        (0 until 64).forEach { step ->
            val previouslyReachedGardenPlots = grid.find('O')
            // reset to a normal (non-visited) plot
            previouslyReachedGardenPlots.forEach { grid.setValue(it, '.')}
            // and now lets see what we can reach from here
            previouslyReachedGardenPlots
                .forEach {
                    it.findNeighbours().values.forEach { neighbour ->
                        if (grid.isValidPosition(neighbour) && grid.getValue(neighbour) != '#')
                            grid.setValue(neighbour, 'O')
                    }
                }
        }
        return grid.count('O')
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initInfiniteCharGrid(inputLines)
        println("pattern rows: ${grid.patternRowCount()}, pattern columns: ${grid.patternColCount()}")

//        val gizmo = initGizmo(grid)
//        gizmo.forEach { (key, value) ->
//            println("$key -> ${value.size} reached plots")
//        }

//        grid.print()
//        println("*************************************")

//        var reachedGardenPlots: Set<Coordinate> = runCycles(grid, grid.findInPattern('S').first(), 131)
//        return reachedGardenPlots.size
        return "WORKING ON IT"
    }

    fun initGizmo(grid:InfiniteGrid<Char>) : Map<Coordinate, Set<Coordinate>>{
        var result:MutableMap<Coordinate, Set<Coordinate>> = mutableMapOf()
        (0 until grid.patternRowCount()).forEach { rowCount ->
            (0 until grid.patternColCount()).forEach { colCount ->
                val coordinate = Coordinate(rowCount, colCount)
                if (grid.getValue(coordinate) != '#') {
                    result[coordinate] = runCycles(grid, coordinate, grid.patternRowCount())
                }
            }
        }
        return result
    }

    private fun runCycles(grid: InfiniteGrid<Char>, startCoordinate:Coordinate, cycles:Int): Set<Coordinate> {
        print("runCycles ($startCoordinate, $cycles): ")
        var previouslyReachedGardenPlots: Set<Coordinate> = setOf(startCoordinate)
        var reachedGardenPlots: Set<Coordinate> = emptySet()

        (0 until cycles).forEach { step ->
            // now lets see what we can reach from here
            reachedGardenPlots = previouslyReachedGardenPlots
                .flatMap {
                    it.findNeighbours().values.filter { neighbour ->
                        grid.getValue(neighbour) != '#'
                    }
                }.toSet()
            //            if (step % grid.patternRowCount() == 0) {
//            println("after step ${step + 1}: ${reachedGardenPlots.size} tiles reached")
            //            }
            previouslyReachedGardenPlots = reachedGardenPlots
            //            println("plots reached after step ${step+1}: $reachedGardenPlots")
            //            grid.print()
            //            println("*************************************")
        }
        println(" ${reachedGardenPlots.size}")
        return reachedGardenPlots
    }
}


