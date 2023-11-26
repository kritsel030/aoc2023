package util.grid

class Grid2DFactory {

    companion object {
        fun initCharGrid(inputLines: List<String>, indexBase:Int = 0) : Grid2D<Char> {
//            val parsedLines: MutableList<MutableList<Char>> = mutableListOf()
//            for (line in inputLines) {
//                val parsedLine: MutableList<Char> = ArrayList()
//                parsedLines.add(parsedLine)
//                for (value in line) {
//                    parsedLine.add(value)
//                }
//            }
            return Grid2D(inputLines.map { line -> line.map { it }.toMutableList() }.toMutableList() , indexBase)
        }

        fun initIntGrid(inputLines: List<String>, indexBase:Int = 0) : Grid2D<Int> {
//            val parsedLines: MutableList<MutableList<Int>> = mutableListOf()
//            for (line in inputLines) {
//                val parsedLine: MutableList<Int> = ArrayList()
//                parsedLines.add(parsedLine)
//                for (value in line) {
//                    parsedLine.add(value.digitToInt())
//                }
//            }
//            return Grid2D(parsedLines, indexBase)
            return Grid2D(inputLines.map { line -> line.map { it.digitToInt() }.toMutableList() }.toMutableList() , indexBase)
        }

    }
}