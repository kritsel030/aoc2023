package util.grid2d

import java.lang.IllegalArgumentException
import kotlin.math.absoluteValue

// grid is a list of rows
// each row is a list of values, each value represents an individual grid value
// to get the cell value in row 3, column 5: grid[3][5]
open class MutableGrid2D<T>(gridValues:MutableList<MutableList<T>>, indexBase:Int = 0) : Grid2D<T>(gridValues, indexBase) {

    // initialize a grid of <rowCount> rows and <colCount> columns,
    // where each cell has value <initialValue>
    constructor(rowCount: Int, colCount: Int, initialValue: T, indexBase: Int = 0) :
            this(MutableList(rowCount) { MutableList(colCount) { initialValue } }, indexBase) {
    }
    
    val mutableGridValues = gridValues

    open fun setValue(coordinate: Coordinate, value: T) {
        setValue(coordinate.rowNo, coordinate.colNo, value)
    }

    open fun setValue(rowNo: Int, colNo: Int, value: T) {
        if (isValidPosition(rowNo, colNo)) {
            val effectiveRowNo = rowNo - indexBase
            val effectiveColNo = colNo - indexBase
            mutableGridValues[effectiveRowNo][effectiveColNo] = value
        } else {
            throw IllegalArgumentException("coordinate $rowNo, $colNo is outside the bounds of this grid")
        }
    }

    fun addRow(rowNo: Int = -1, rowValues: MutableList<T>) {
        if (rowNo < 0)
            mutableGridValues.add(rowValues)
        else {
            val effectiveRowNo = rowNo - indexBase
            mutableGridValues.add(effectiveRowNo, rowValues)
        }
    }

    fun addRow(rowNo: Int = -1, value: T) {
        val newRow = MutableList(colCount()) { value }
        if (rowNo < 0)
            mutableGridValues.add(newRow)
        else {
            val effectiveRowNo = rowNo - indexBase
            mutableGridValues.add(effectiveRowNo, newRow)
        }
    }

    fun addColumn(colNo: Int = -1, colValues: MutableList<T>) {
        val effectiveColNo = colNo - indexBase
        mutableGridValues.forEachIndexed { index, row ->
            row.add(effectiveColNo, colValues[index])
        }
    }

    fun addColumn(colNo: Int = -1, value: T) {
        val effectiveColNo = colNo - indexBase
        mutableGridValues.forEach { row ->
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
        mutableGridValues.removeAt(effectiveRowNo)
        mutableGridValues.add(effectiveRowNo, row)
    }

    fun replaceColumn(colNo: Int, column: MutableList<T>) {
        val effectiveColNo = colNo - indexBase
        mutableGridValues.forEachIndexed { rowNo, row ->
            row[effectiveColNo] = column[rowNo]
        }
    }

    fun replaceValues(orientation: ORIENTATION, id: Int, values: MutableList<T>) {
        when (orientation) {
            ORIENTATION.HORIZONTAL -> replaceRow(id, values)
            ORIENTATION.VERTICAL -> replaceColumn(id, values)
        }
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

}
