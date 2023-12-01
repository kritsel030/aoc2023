package day02

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P02_Solver().solve(INPUT_VARIANT.EXAMPLE)
}

class P02_Solver : BaseSolver() {
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        return "dummy answer"
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        var context = mutableMapOf<String, Any>(Pair("someKey", "someValue"))

        return Pair("dummy answer", context)
    }
}


