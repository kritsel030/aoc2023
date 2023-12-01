package day00

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P00_Solver().solve(INPUT_VARIANT.EXAMPLE)
}

class P00_Solver : BaseSolver() {
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        return inputLines.size
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        var context = mutableMapOf<String, Any>(Pair("1", "100"))
        context.put("someKey", "someValue")

        return Pair(inputLines.size * 10, context)
    }
}


