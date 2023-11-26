package util.grid

import kotlin.test.Test

class Grid2DFactoryTest {

    @Test
    fun initCharGridTest() {
        val inputLines = listOf(
            "abcde",
            "ghijk",
            "lmnop",
            "qrstu"
        )
        val grid = Grid2DFactory.initCharGrid(inputLines)
    }

    @Test
    fun initIntTest() {
        val inputLines = listOf(
            "0123456",
            "2345678",
            "1357913",
            "2468024"
        )
        val grid = Grid2DFactory.initIntGrid(inputLines)
    }
}