package util.grid2d

import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

// grid is a list of rows
// each row is a list of values, each value represents an individual grid value
// to get the cell value in row 3, column 5: grid[3][5]
open class Grid2D<T>(var gridValues:List<List<T>>, val indexBase:Int = 0) {

    init {
        if (indexBase > 1)
            throw IllegalArgumentException("indexBase must be 1 or lower")
    }

    // initialize a grid of <rowCount> rows and <colCount> columns,
    // where each cell has value <initialValue>
    constructor(rowCount: Int, colCount: Int, initialValue: T, indexBase: Int = 0) :
            this(List(rowCount) { List(colCount) { initialValue } }, indexBase) {
    }

    var visitedCoordinates: MutableMap<Coordinate, MutableMap<Any, Any>> = mutableMapOf()

    open fun rowCount(): Int {
        return gridValues.size
    }

    open fun colCount(): Int {
        return if (gridValues.isEmpty()) 0 else gridValues[0].size
    }

    open fun size(orientation: ORIENTATION): Int {
        return when (orientation) {
            ORIENTATION.HORIZONTAL -> rowCount()
            ORIENTATION.VERTICAL -> colCount()
        }
    }

    open fun dimensions(): Map<ORIENTATION, Int> {
        return ORIENTATION.values().associateWith { size(it) }
    }

    open fun getValue(coordinate: Coordinate): T {
        return getValue(coordinate.rowNo, coordinate.colNo)
    }

    open fun getValue(rowNo: Int, colNo: Int): T {
        if (isValidPosition(rowNo, colNo)) {
            val effectiveRowNo = rowNo - indexBase
            val effectiveColNo = colNo - indexBase
            return gridValues[effectiveRowNo][effectiveColNo]
        } else {
            throw IllegalArgumentException("coordinate $rowNo, $colNo is outside the bounds of this grid")
        }
    }

    open fun getRowValues(rowNo: Int): List<T> {
        val effectiveRowNo = rowNo - indexBase
        return gridValues[effectiveRowNo]
    }

    open fun getColumnValues(colNo: Int): MutableList<T> {
        val effectiveColNo = colNo - indexBase
        return gridValues.map { it[effectiveColNo] }.toMutableList()
    }

    open fun getValues(orientation: ORIENTATION, id: Int): List<T> {
        return when (orientation) {
            ORIENTATION.HORIZONTAL -> getRowValues(id)
            ORIENTATION.VERTICAL -> getColumnValues(id)
        }
    }

    fun isValidPosition(coordinate: Coordinate): Boolean {
        return isValidPosition(coordinate.rowNo, coordinate.colNo)
    }

    open fun isValidPosition(rowNo: Int, colNo: Int): Boolean {
//        println("Grid2D.isValidPosition ${rowNo} + ${colNo}")
        val effectiveRowNo = rowNo - indexBase
        val effectiveColNo = colNo - indexBase
        if (effectiveRowNo !in 0..gridValues.size - 1) {
            return false
        }
        if (effectiveColNo !in 0..gridValues[effectiveRowNo].size - 1) {
            return false
        }
        return true
    }

    fun isVisited(coordinate: Coordinate, direction: Direction? = null): Boolean {
//        val print = coordinate.equals(Coordinate(72, 20))
        val print = false
        if (print) {
            println("isVisited($coordinate, $direction)")
        }
        return if (direction == null) {
            if (print)
                println("visitedCoordinates.containsKey($coordinate): ${visitedCoordinates.containsKey(coordinate)}")
            visitedCoordinates.containsKey(coordinate)
        } else {
            if (print) {
                println("visitedCoordinates.containsKey($coordinate): ${visitedCoordinates.containsKey(coordinate)}")
                println("details: ${visitedCoordinates[coordinate]}")
                if (visitedCoordinates.containsKey(coordinate) )
                    println("visitedCoordinates[$coordinate]!![$direction]: ${visitedCoordinates[coordinate]!![direction]}")
            }
            var result = visitedCoordinates.containsKey(coordinate) && (visitedCoordinates[coordinate]!![direction] == true)
            if (print) {
                println("result: $result")
                println("---------------")
            }
            result
        }
    }

    fun reset() {
        visitedCoordinates = mutableMapOf()
    }

    open fun find(value: T): List<Coordinate> {
        return (0 until rowCount())
            .map { rowNo ->
                getRowValues(rowNo)
                    .mapIndexed { colNo, gridValue -> if (gridValue == value) Coordinate(rowNo, colNo) else null }
                    .filterNotNull()
                    .toList()
            }
            .flatten()
    }

    open fun count(value: T): Int {
        return (0 until rowCount())
            .map { rowNo ->
                getRowValues(rowNo)
                    .map { gridValue -> if (gridValue == value) 1 else 0 }.sum()
            }
            .sum()
    }

    fun print(cursor:GridCursor<T>? = null) {
        // column index line
        print("    ")
        (0+indexBase..gridValues[0].size-1+indexBase).forEach {  print(it.absoluteValue.toString().padStart(2, ' ') + " ") }
        println()
        // dash line
        print("   ")
        (0+indexBase..gridValues[0].size-1+indexBase).forEach{print("---")}
        println()

        // row lines
        gridValues.forEachIndexed { rowIndex, colValues ->
            val rowNo = rowIndex+indexBase
            print(rowNo.absoluteValue.toString().padStart(2, ' ') + "| ")
            colValues.forEachIndexed { colIndex, value ->
                val colNo = colIndex + indexBase
                if (cursor?.isAt(rowNo, colNo) == true) {
                    print(">$value<")
                } else if (cursor != null && cursor.hasVisited(rowNo, colNo)) {
                    print("[$value]")
                } else if (cursor == null && visitedCoordinates.containsKey(Coordinate(rowNo, colNo))) {
                    print("($value)")
                } else {
                    print(" $value ")
                }
            }
            println()
        }
    }
}

enum class ORIENTATION {
    HORIZONTAL,
    VERTICAL
}