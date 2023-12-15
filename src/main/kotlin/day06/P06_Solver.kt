package day06

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P06_Solver().solve(INPUT_VARIANT.REAL)
}

class P06_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "boat race"
    }

    // answer: 2269432
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val races:List<Pair<Long, Long>> =
            if (inputVariant == INPUT_VARIANT.EXAMPLE)
                listOf(Pair(7,9), Pair(15,40), Pair(30,200))
            else
                listOf(Pair(49,298), Pair(78,1185), Pair(79,1066), Pair(80,1181))

        val answer = races
            .map { race -> determineWinPossibilitiesCount(race) }
            .reduce { accumulator, element -> accumulator * element }

        return answer
    }

    // answer: 35865985
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val race: Pair<Long, Long> =
            if (inputVariant == INPUT_VARIANT.EXAMPLE)
                Pair(71530,940200)
            else
                Pair(49787980, 298118510661181)
        val answer = determineWinPossibilitiesCount(race)
        return answer
    }

    fun determineWinPossibilitiesCount(race:Pair<Long, Long>) : Long {
        var firstWin:Long = 0
        for (p in (1 until race.first)) {
            if (race.second < (race.first - p) * p) {
                firstWin = p
                break
            }
        }
        firstWin

        var lastWin:Long = 0
        for (p in (race.first-1 downTo 1)) {
            if (race.second < (race.first - p) * p) {
                lastWin = p
                break
            }
        }
        return (lastWin - firstWin) + 1
    }
}


