package day15

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    Pxx_Solver().solve(INPUT_VARIANT.REAL)
}

class Pxx_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "hash"
    }
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val values = inputLines[0].split(',')
        return values.sumOf{hash(it)}
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    /*
    Determine the ASCII code for the current character of the string.
Increase the current value by the ASCII code you just determined.
Set the current value to itself multiplied by 17.
Set the current value to the remainder of dividing itself by 256.
     */
    fun hash(value:String) : Int {
        var hash = 0
        value.forEach {
            hash = ((hash + it.code) * 17) % 256
        }
        return hash
    }
}


