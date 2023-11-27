package dayxx_template

import base.BaseSolver
import base.TestOrReal

fun main(args: Array<String>) {
    Pxx_Solver().solve(TestOrReal.TEST)
}

class Pxx_Solver : BaseSolver() {
    override fun solvePart1(inputLines: List<String>, testOrReal: TestOrReal): Any{
        return "dummy answer"
    }

    override fun solvePart2(inputLines: List<String>, testOrReal: TestOrReal): Any {
        var context = mutableMapOf<String, Any>(Pair("someKey", "someValue"))

        return Pair("dummy answer", context)
    }
}


