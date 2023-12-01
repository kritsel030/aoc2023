package day01

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P01_Solver().solve(INPUT_VARIANT.EXAMPLE)
}

class P01_Solver : BaseSolver() {

    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return inputLines.map { determineNumber(it) }.sum()
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return inputLines.map { determineNumber2(it) }.sum()
    }

    fun determineNumber(line:String) : Int {
        val firstDigit = line.filter { it.isDigit() }.first()
        val secondDigit = line.reversed().filter { it.isDigit() }.first()
        return ("$firstDigit$secondDigit").toInt()
    }

    fun determineNumber2(line:String) : Int {
        val firstDigit = findFirstSpelledDigit(line)
        val secondDigit = findFirstSpelledDigit(line.reversed())
        return ("$firstDigit$secondDigit").toInt()
    }

    fun findFirstSpelledDigit(line:String) : Int {
        val spelledDigits = listOf(
            Pair("one", 1),
            Pair("two", 2),
            Pair("three", 3),
            Pair("four", 4),
            Pair("five", 5),
            Pair("six", 6),
            Pair("seven", 7),
            Pair("eight", 8),
            Pair("nine", 9),
            Pair("one".reversed(), 1),
            Pair("two".reversed(), 2),
            Pair("three".reversed(), 3),
            Pair("four".reversed(), 4),
            Pair("five".reversed(), 5),
            Pair("six".reversed(), 6),
            Pair("seven".reversed(), 7),
            Pair("eight".reversed(), 8),
            Pair("nine".reversed(), 9),
            Pair("1", 1),
            Pair("2", 2),
            Pair("3", 3),
            Pair("4", 4),
            Pair("5", 5),
            Pair("6", 6),
            Pair("7", 7),
            Pair("8", 8),
            Pair("9", 9))
        val firstDigitIndices = spelledDigits.map { line.indexOf ( it.first ) to it}.toMap().filter { it.key >= 0 }
        val result = firstDigitIndices[firstDigitIndices.keys.min()]!!.second
        //println("findFirstSpelledDigit $line: $result")
        return result
    }
}