package util.grid

import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

// grid is a list of rows
// each row is a list of values, each value represents an individual grid value
// to get the cell value in row 3, column 5: grid[3][5]
open class Grid2D<T>(var gridValues:MutableList<MutableList<T>>, val indexBase:Int = 0) {

    init {
        if (indexBase > 1)
            throw IllegalArgumentException("indexBase must be 1 or lower")
    }

    // initialize a grid of <rowCount> rows and <colCount> columns,
    // where each cell has value <initialValue>
    constructor(rowCount: Int, colCount: Int, initialValue: T, indexBase: Int = 0) :
            this(MutableList(rowCount) { MutableList(colCount) { initialValue } }, indexBase) {
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

    open fun setValue(coordinate: Coordinate, value: T) {
        setValue(coordinate.rowNo, coordinate.colNo, value)
    }

    open fun setValue(rowNo: Int, colNo: Int, value: T) {
        if (isValidPosition(rowNo, colNo)) {
            val effectiveRowNo = rowNo - indexBase
            val effectiveColNo = colNo - indexBase
            gridValues[effectiveRowNo][effectiveColNo] = value
        } else {
            throw IllegalArgumentException("coordinate $rowNo, $colNo is outside the bounds of this grid")
        }
    }

    open fun getRowValues(rowNo: Int): MutableList<T> {
        val effectiveRowNo = rowNo - indexBase
        return gridValues[effectiveRowNo]
    }

    open fun getColumnValues(colNo: Int): MutableList<T> {
        val effectiveColNo = colNo - indexBase
        return gridValues.map { it[effectiveColNo] }.toMutableList()
    }

    open fun getValues(orientation: ORIENTATION, id: Int): MutableList<T> {
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

    fun addRow(rowNo: Int = -1, rowValues: MutableList<T>) {
        if (rowNo < 0)
            gridValues.add(rowValues)
        else {
            val effectiveRowNo = rowNo - indexBase
            gridValues.add(effectiveRowNo, rowValues)
        }
    }

    fun addRow(rowNo: Int = -1, value: T) {
        val newRow = MutableList(colCount()) { value }
        if (rowNo < 0)
            gridValues.add(newRow)
        else {
            val effectiveRowNo = rowNo - indexBase
            gridValues.add(effectiveRowNo, newRow)
        }
    }

    fun addColumn(colNo: Int = -1, colValues: MutableList<T>) {
        val effectiveColNo = colNo - indexBase
        gridValues.forEachIndexed { index, row ->
            row.add(effectiveColNo, colValues[index])
        }
    }

    fun addColumn(colNo: Int = -1, value: T) {
        val effectiveColNo = colNo - indexBase
        gridValues.forEach { row ->
            if (effectiveColNo < 0)
                row.add(value)
            else {
                row.add(effectiveColNo, value)
            }
        }
    }

    fun addValues(orientation: ORIENTATION, id: Int, values: MutableList<T>) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> addRow(id, values)
            ORIENTATION.VERTICAL -> addColumn(id, values)
        }
    }

    fun addValues(orientation: ORIENTATION, id: Int, value: T) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> addRow(id, value)
            ORIENTATION.VERTICAL -> addColumn(id, value)
        }
    }

    fun replaceRow(rowNo: Int, row: MutableList<T>) {
        val effectiveRowNo = rowNo - indexBase
        gridValues.removeAt(effectiveRowNo)
        gridValues.add(effectiveRowNo, row)
    }

    fun replaceColumn(colNo: Int, column: MutableList<T>) {
        val effectiveColNo = colNo - indexBase
        gridValues.forEachIndexed { rowNo, row ->
            row[effectiveColNo] = column[rowNo]
        }
    }

    fun replaceValues(orientation: ORIENTATION, id: Int, values: MutableList<T>) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> replaceRow(id, values)
            ORIENTATION.VERTICAL -> replaceColumn(id, values)
        }
    }

    fun isVisited(coordinate: Coordinate, direction: Direction?): Boolean {
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

    // TODO:
    // when the cursorpath consists of elements with distance > 1
    fun borderFill(cursor:GridCursor<T>, clockWisePath:Boolean, borderValue:T, fillValue:T) {
        val path = cursor.path.reversed()
        path.forEachIndexed { index, pathElem ->
            if (pathElem.travelledDirection == Direction.NORTH || (index < path.size-1 && path[index + 1].travelledDirection == Direction.NORTH)) {
                var nextCoordinate = pathElem.coordinate
                while (true) {
                    // continue filling up the tiles to the EAST of the current coordinate,
                    // until you reach a border tile
                    val fillDirection = if (clockWisePath) Direction.EAST else Direction.WEST
                    nextCoordinate = nextCoordinate.move(fillDirection)
                    if (this.isValidPosition(nextCoordinate) && this.getValue(nextCoordinate) != borderValue) {
                        this.setValue(nextCoordinate, fillValue)
                    } else {
                        break
                    }
                }
            }
        }
    }


    // untested
//    fun floodFill(start:Coordinate, border:T, fill:T) {
//        var tilesToFill = listOf(start)
//        while (tilesToFill.isNotEmpty()) {
//            tilesToFill.forEach { setValue(it, fill) }
//            tilesToFill = tilesToFill.flatMap {
//                it.findNeighbours().values
//                    .filter { isValidPosition(it) }
//                    .filter { getValue(it) != border && getValue(it) != fill }
//            }
//        }
//    }

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
    /*
        00 01 02 03 04 05
        -----------------
    00|  x  x  x  x  x  x
    01|  x  x  x  x  x  x
    02|  x  x [x] x  x  x
    03|  x  x  x  x  x  x
    04|  x  x  x  x  x  x
    04|  x  x  x  x  x  x

     */

}

enum class ORIENTATION {
    HORIZONTAL,
    VERTICAL
}