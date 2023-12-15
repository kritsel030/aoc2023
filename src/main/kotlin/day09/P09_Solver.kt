package day09

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P09_Solver().solve(INPUT_VARIANT.REAL)
}

class P09_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "sensor prediction"
    }

    // answer: 1969958987
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val parsedInput = inputLines
            .map{it.split(" ").map { value -> value.toLong() }.toMutableList()}
            .toMutableList()

        return parsedInput
            .sumOf { extrapolateNextValue(it) }

    }

    // answer: 1068
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val parsedInput = inputLines
            .map{it.split(" ").map { value -> value.toLong() }.toMutableList()}
            .toMutableList()

        return parsedInput
            .sumOf { extrapolatePreviousValue(it) }

    }

    // Recursive function to add a new sequence line to the given [lineSequences],
    // based on the last line in the given [lineSequences].
    // Stops when the added sequence line contains only zero's
    fun addSequenceLine(lineSequences: MutableList<MutableList<Long>>) {
        val nextSequence =
            (0 until lineSequences.last().size-1)
                .map {lineSequences.last()[it+1] - lineSequences.last()[it]}
                .toMutableList()
        lineSequences.add(nextSequence)
        if (nextSequence.count{it.equals(0)} != nextSequence.size) {
            addSequenceLine(lineSequences)
        }
    }

    fun extrapolateNextValue(historyLine:MutableList<Long>) : Long {
        // generate the sequence lines until we've hit the all zeros line
        val lineSequences = mutableListOf(historyLine)
        addSequenceLine(lineSequences)

        // calculate the next value for our original historyLine, based on the generated line sequences
        // (start at the last sequence line, and work our way up)
        for (i in lineSequences.size-1 downTo 0) {
            if (i == lineSequences.size - 1) {
                // simply add a zero to the last sequence line
                lineSequences[i].add(0)
            } else {
                lineSequences[i].add(lineSequences[i].last() + lineSequences[i+1].last())
            }
        }
        return lineSequences.first().last()
    }

    fun extrapolatePreviousValue(historyLine:MutableList<Long>) : Long {
        // generate the sequence lines until we've hit the all zeros line
        val lineSequences = mutableListOf(historyLine)
        addSequenceLine(lineSequences)

        // calculate the previous value for our original historyLine, based on the generated line sequences
        // (start at the last sequence line, and work our way up)
        for (i in lineSequences.size-1 downTo 0) {
            if (i == lineSequences.size - 1) {
                // simply add a zero to the beginning of the last sequence line
                lineSequences[i].add(0, 0)
            } else {
                lineSequences[i].add(0, lineSequences[i].first() - lineSequences[i+1].first())
            }
        }
        return lineSequences.first().first()
    }
}


