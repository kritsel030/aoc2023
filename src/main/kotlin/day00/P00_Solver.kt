package day00

import base.BaseSolver
import base.TestOrReal

fun main(args: Array<String>) {
    P00_Solver().solve(TestOrReal.TEST)
}

class P00_Solver : BaseSolver() {
    override fun solvePart1(inputLines: List<String>, testOrReal: TestOrReal): Any{
        return inputLines.size
    }

    override fun solvePart2(inputLines: List<String>, testOrReal: TestOrReal): Any {
        var context = mutableMapOf<String, Any>(Pair("1", "100"))
        context.put("someKey", "someValue")

        return Pair(inputLines.size * 10, context)
    }
}


