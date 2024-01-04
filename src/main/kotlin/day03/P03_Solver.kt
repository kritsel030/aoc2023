package day03

import base.BaseSolver
import base.INPUT_VARIANT
import util.grid2d.*

fun main(args: Array<String>) {
    P03_Solver().solve(INPUT_VARIANT.REAL)
}

class P03_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "gear ratios"
    }

    // answer: 559667
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val grid = Grid2DFactory.initCharGrid(inputLines)
        val numbers = findNumbers(grid)
        val answer = numbers.filter { isNumberValidPartNumber(it, grid) }.map { it.third }.sum()
//        println(numbers)
        return answer
    }

    // answer: 86841457
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        // initialize the grid, and find the valid part numbers in the grid
        val grid = Grid2DFactory.initMutableCharGrid(inputLines)
        val numbers = findNumbers(grid)
        val validPartNumbers = numbers.filter { isNumberValidPartNumber(it, grid) }.toList()

        // key: coordinate with a '*' value
        // value: list of numbers (each number is Triple with a start coordinate, a lenght and a number value)
        var potentialGears:MutableMap<Coordinate, List<Triple<Coordinate, Int, Int>>> = mutableMapOf()

        // for every grid coordinate which contains a '*', loop through all neighbours of all variants
        // for each number with a neighbour-coordinate matching the asterisk-coordinate,
        // update the potentialGears map
        grid.gridValues.mapIndexed { rowNo, row ->
            row.mapIndexed { colNo, value ->
                if (value.equals('*')) {
                    val coordinate = Coordinate(rowNo, colNo)
                    potentialGears[coordinate] = validPartNumbers.filter{findNumberNeighbours(it, grid).contains(coordinate)}
                }
            }
        }

        val gears = potentialGears.filter{it.value.size == 2}
        val answer = gears.map{it.value[0].third * it.value[1].third}.sum()

        return answer
    }

    // number.first: Coordinate -> start coordinate
    // number.second: Int -> length of the number
    // number.third: Int -> value of the number
    private fun findNumbers(grid:Grid2D<Char>) : List<Triple<Coordinate, Int, Int>>{
        val numbers = mutableListOf<Triple<Coordinate, Int, Int>>()
        grid.gridValues.mapIndexed { rowNo, row ->
            var numberStartCoordinate:Coordinate? = null
            row.mapIndexed { colNo, value ->
                if (grid.getValue(rowNo, colNo).isDigit()) {
                    if (numberStartCoordinate == null) {
                        // we've found the start coordinate of a new number
//                        println("start found at $rowNo, $colNo")
                        numberStartCoordinate = Coordinate(rowNo, colNo)
                    }
                } else {
                    if (numberStartCoordinate != null) {
                        // we've found the end of the most recent number
//                        println("end found at $rowNo, ${colNo-1}")
                        val numberValue = (numberStartCoordinate!!.colNo until colNo)
                            .map{grid.getValue(rowNo, it)}
                            .toList()
                            .joinToString("")
                            .toInt()
                        numbers.add(Triple(numberStartCoordinate!!, colNo-numberStartCoordinate!!.colNo, numberValue))
                        numberStartCoordinate = null
                    }
                }
                if (colNo == row.size-1) {
                    // we're at the end of a row
                    if (grid.getValue(rowNo, colNo).isDigit()) {
                        if (numberStartCoordinate == null) {
                            // we've found the start coordinate of a new number
//                            println("start found at $rowNo, $colNo")
                            numberStartCoordinate = Coordinate(rowNo, colNo)
                        }

                        // we've found the end of the most recent number
//                        println("end found at $rowNo, $colNo")
                        val numberValue = (numberStartCoordinate!!.colNo until colNo+1)
                            .map { grid.getValue(rowNo, it) }
                            .toList()
                            .joinToString("")
                            .toInt()
                        numbers.add(Triple(numberStartCoordinate!!, colNo - numberStartCoordinate!!.colNo, numberValue))
                        numberStartCoordinate = null
                    }
                }
            }
        }
        return numbers
    }

    // number.first: Coordinate -> start coordinate
    // number.second: Int -> length of the number
    // number.third: Int -> value of the number
    private fun isNumberValidPartNumber(number:Triple<Coordinate, Int, Int>, grid:Grid2D<Char>) : Boolean {
        val neighbours = findNumberNeighbours(number, grid)
        val valid = neighbours.count {! (grid.getValue(it).isDigit() || grid.getValue(it).equals('.')) } > 0
//        println("$number is valid: $valid")
        return valid
    }

    // number.first: Coordinate -> start coordinate
    // number.second: Int -> length of the number
    // number.third: Int -> value of the number
    private fun findNumberNeighbours(number:Triple<Coordinate, Int, Int>, grid: Grid2D<Char>) : List<Coordinate> {
        var neighbours = mutableListOf<Coordinate>()
        // A. start coordinate
        // determine coordinates of its valid Western neighbours
        val startCoordinate = GridCursor<Char> (grid, number.first)
        neighbours.addAll(startCoordinate.getNeighbours(listOf(Direction.NORTHWEST, Direction.WEST, Direction.SOUTHWEST)).values)


        // B. end coordinate
        // // determine coordinates of its valid Eastern neighbours
        val endCoordinate =
            GridCursor<Char>(grid, Coordinate(number.first.rowNo, number.first.colNo + number.second - 1))
        neighbours.addAll(endCoordinate.getNeighbours(listOf(Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST)).values)

        // C. each coordinate
        // determine coordinates of its valid North and South neighbours
        val directions = listOf(Direction.NORTH, Direction.SOUTH)
        (0 until number.second).forEach {
            val cursor = GridCursor<Char> (grid, Coordinate(number.first.rowNo, number.first.colNo + it))
            neighbours.addAll(cursor.getNeighbours(directions).values)
        }
        return neighbours
    }


}


