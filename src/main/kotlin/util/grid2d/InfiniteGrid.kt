package util.grid2d

class InfiniteGrid<T>(gridValues:List<List<T>>, indexBase:Int = 0) : Grid2D<T>(gridValues, indexBase) {

    fun patternRowCount() : Int {
        return super.rowCount()
    }

    fun patternColCount() : Int {
        return super.colCount()
    }

    fun patternSize(orientation: ORIENTATION): Int {
        return super.size(orientation)
    }

    fun normalizeCoordinate(coordinate:Coordinate) : Coordinate {
        return normalizeCoordinate(coordinate.rowNo, coordinate.colNo)
    }

    fun normalizeCoordinate(rowNo:Int, colNo:Int) : Coordinate {
        return Coordinate(Math.floorMod(rowNo, patternRowCount()), Math.floorMod(colNo, patternColCount()))
    }

    override fun getValue(coordinate: Coordinate): T {
        return super.getValue(normalizeCoordinate(coordinate.rowNo, coordinate.colNo))
    }

    override fun getValue(rowNo: Int, colNo: Int): T {
        val normalized = normalizeCoordinate(rowNo, colNo)
        return super.getValue(normalized.rowNo, normalized.colNo)
    }

    override fun isValidPosition(rowNo: Int, colNo: Int): Boolean {
        return true
    }

    fun findInPattern(value:T) : List<Coordinate> {
        return (0 until patternRowCount())
            .map { rowNo ->
                super.getRowValues(rowNo)
                    .mapIndexed { colNo, gridValue -> if (gridValue == value) Coordinate(rowNo, colNo) else null }
                    .filterNotNull()
                    .toList()
            }
            .flatten()
    }

    fun countInPattern(value:T) : Int {
        return (0 until patternRowCount())
            .map { rowNo ->
                super.getRowValues(rowNo)
                    .map { gridValue -> if (gridValue == value) 1 else 0 }.sum()
            }
            .sum()
    }

    override fun rowCount() : Int {
        throw NotImplementedError("${this.javaClass.simpleName}.rowCount()")
    }

    override fun colCount() : Int {
        throw NotImplementedError("${this.javaClass.simpleName}.colCount()")
    }

    override fun size(orientation: ORIENTATION): Int {
        throw NotImplementedError("${this.javaClass.simpleName}.size(ORIENTATION)")
    }

    override fun  getRowValues(rowNo: Int): MutableList<T> {
        throw NotImplementedError("${this.javaClass.simpleName}.getRowValues(Int)")
    }

    override fun getColumnValues(colNo: Int): MutableList<T> {
        throw NotImplementedError("${this.javaClass.simpleName}.getColumnValues(Int)")
    }

    override fun getValues(orientation: ORIENTATION, id: Int): MutableList<T> {
        throw NotImplementedError("${this.javaClass.simpleName}.getValues(ORIENTATION, Int)")
    }

    override fun find(value: T): List<Coordinate> {
        throw NotImplementedError("${this.javaClass.simpleName}.find(T)")
    }

    override fun count(value: T): Int {
        throw NotImplementedError("${this.javaClass.simpleName}.count(T)")
    }
}