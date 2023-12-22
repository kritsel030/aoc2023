package util.grid

class Grid2DFactory {

    companion object {
        fun initCharGrid(inputLines: List<String>, indexBase:Int = 0) : Grid2D<Char> {
            return Grid2D(inputLines.map { line -> line.map { it }.toMutableList() }.toMutableList() , indexBase)
        }

        fun initInfiniteCharGrid(inputLines: List<String>, indexBase:Int = 0) : InfiniteGrid<Char> {
            return InfiniteGrid(inputLines.map { line -> line.map { it }.toMutableList() }.toMutableList() , indexBase)
        }

        fun initIntGrid(inputLines: List<String>, indexBase:Int = 0) : Grid2D<Int> {
            return InfiniteGrid(inputLines.map { line -> line.map { it.digitToInt() }.toMutableList() }.toMutableList() , indexBase)
        }

        fun initInfiniteIntGrid(inputLines: List<String>, indexBase:Int = 0) : InfiniteGrid<Int> {
            return InfiniteGrid(inputLines.map { line -> line.map { it.digitToInt() }.toMutableList() }.toMutableList() , indexBase)
        }

    }
}