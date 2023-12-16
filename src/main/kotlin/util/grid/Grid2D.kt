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
    constructor(rowCount:Int, colCount:Int, initialValue:T, indexBase:Int = 0) :
            this(MutableList(rowCount) {MutableList(colCount) {initialValue}}, indexBase) {}

    var visitedCoordinates : MutableMap<Coordinate, Cursor<T>> = mutableMapOf()

    fun rowCount() : Int {
        return gridValues.size
    }

    fun colCount() : Int {
        return if (gridValues.isEmpty()) 0 else gridValues[0].size
    }

    fun size(orientation: ORIENTATION) : Int {
        return when (orientation) {
            ORIENTATION.HORIZONTAL -> rowCount()
            ORIENTATION.VERTICAL -> colCount()
        }
    }

    fun getValue(coordinate:Coordinate) : T {
        return getValue(coordinate.rowNo, coordinate.colNo)
    }

    fun getValue(rowNo: Int, colNo: Int) : T {
        if (isValidPosition(rowNo, colNo)) {
            val effectiveRowNo = rowNo - indexBase
            val effectiveColNo = colNo - indexBase
            return gridValues[effectiveRowNo][effectiveColNo]
        } else {
            throw IllegalArgumentException("coordinate $rowNo, $colNo is outside the bounds of this grid")
        }
    }

    fun setValue(rowNo: Int, colNo: Int, value: T) {
        if (isValidPosition(rowNo, colNo)) {
            val effectiveRowNo = rowNo - indexBase
            val effectiveColNo = colNo - indexBase
            gridValues[effectiveRowNo][effectiveColNo] = value
        } else {
            throw IllegalArgumentException("coordinate $rowNo, $colNo is outside the bounds of this grid")
        }
    }

    fun getRowValues(rowNo:Int) : MutableList<T>{
        val effectiveRowNo = rowNo - indexBase
        return gridValues[effectiveRowNo]
    }

    fun getColumnValues(colNo:Int) : MutableList<T> {
        val effectiveColNo = colNo - indexBase
        return gridValues.map { it[effectiveColNo] }.toMutableList()
    }

    fun getValues(orientation: ORIENTATION, id: Int) : MutableList<T> {
        return when (orientation) {
            ORIENTATION.HORIZONTAL -> getRowValues(id)
            ORIENTATION.VERTICAL -> getColumnValues(id)
        }
    }

    fun isValidPosition(coordinate:Coordinate) : Boolean {
        return isValidPosition(coordinate.rowNo, coordinate.colNo)
    }

    fun isValidPosition(rowNo:Int, colNo:Int) : Boolean{
//        println("Grid2D.isValidPosition ${rowNo} + ${colNo}")
        val effectiveRowNo = rowNo - indexBase
        val effectiveColNo = colNo - indexBase
        if (effectiveRowNo !in 0..gridValues.size-1) {
            return false
        }
        if (effectiveColNo !in 0..gridValues[effectiveRowNo].size-1) {
            return false
        }
        return true
    }

    fun addRow(rowNo:Int = -1, rowValues:MutableList<T>) {
        if (rowNo < 0)
            gridValues.add(rowValues)
        else {
            val effectiveRowNo = rowNo - indexBase
            gridValues.add(effectiveRowNo, rowValues)
        }
    }

    fun addRow(rowNo:Int = -1, value:T) {
        val newRow = MutableList(colCount()){value}
        if (rowNo < 0)
            gridValues.add(newRow)
        else {
            val effectiveRowNo = rowNo - indexBase
            gridValues.add(effectiveRowNo, newRow)
        }
    }

    fun addColumn(colNo: Int = -1, colValues:MutableList<T>) {
        val effectiveColNo = colNo - indexBase
        gridValues.forEachIndexed {index, row ->
                row.add(effectiveColNo, colValues[index]) }
    }

    fun addColumn(colNo: Int = -1, value:T) {
        val effectiveColNo = colNo - indexBase
        gridValues.forEach { row ->
            if (effectiveColNo < 0)
                row.add(value)
            else {
                row.add(effectiveColNo, value)
            }
        }
    }

    fun addValues(orientation: ORIENTATION, id: Int, values:MutableList<T>) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> addRow(id, values)
            ORIENTATION.VERTICAL -> addColumn(id, values)
        }
    }

    fun addValues(orientation: ORIENTATION, id:Int, value:T) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> addRow(id, value)
            ORIENTATION.VERTICAL -> addColumn(id, value)
        }
    }

    fun replaceRow(rowNo:Int, row:MutableList<T>) {
        val effectiveRowNo = rowNo-indexBase
        gridValues.removeAt(effectiveRowNo)
        gridValues.add(effectiveRowNo, row)
    }

    fun replaceColumn(colNo:Int, column:MutableList<T>) {
        val effectiveColNo = colNo - indexBase
        gridValues.forEachIndexed { rowNo, row ->
            row[effectiveColNo] = column[rowNo]
        }
    }

    fun replaceValues(orientation: ORIENTATION, id:Int, values:MutableList<T>) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> replaceRow(id, values)
            ORIENTATION.VERTICAL -> replaceColumn(id, values)
        }
    }

    fun print(cursor:Cursor<T>? = null) {
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
                    print("[$value]")
                } else if (cursor?.hasVisited(rowNo, colNo) == true) {
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