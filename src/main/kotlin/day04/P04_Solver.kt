package day04

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P04_Solver().solve(INPUT_VARIANT.REAL)
}

class P04_Solver : BaseSolver() {

    // answer: 22193
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val cardMap = parseInput(inputLines)
        val two = 2
        val answer = cardMap.values
            .map{it.numbersOnCard.filter{myNumber -> it.winningNumbers.contains(myNumber)}.count()}
            .filter{it > 0}
            .map{Math.pow(two.toDouble(), it.toDouble()-1)}
            .sum()

        return answer
    }

    // answer: 5625994
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val cardMap = parseInput(inputLines)
        cardMap
            .forEach { (id, card) ->
                val matchingNumbers = card.numbersOnCard.filter{myNumber -> card.winningNumbers.contains(myNumber)}.count()
                (1 .. card.copies).forEach {
                    (id + 1..id + matchingNumbers)
                        .filter { cardMap.containsKey(it) }
                        .forEach {
                            cardMap.get(it)?.copies = cardMap.get(it)?.copies!! + 1
                        }
                }
            }
        val answer = cardMap.values.map{it.copies}.sum()
        return answer
    }

    // example: Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
    fun parseInput(inputLines: List<String>) : Map<Int, Card> {
        return inputLines.map{line ->
            val idAndRest = line.split(": ")
//            val id = idAndRest[0].split(" ")[1].trim().toInt()
            val id = idAndRest[0].split(" ").last().toInt()
//            val id = idAndRest[0]
            val winningNumbers = idAndRest[1].split(" | ")[0].split(" ").filter{!it.trim().isEmpty()}.map{it.trim().toInt()}.toList()
            val cardNumbers = idAndRest[1].split(" | ")[1].split(" ").filter{!it.trim().isEmpty()}.map{it.trim().toInt()}.toList()
            id to Card(id, winningNumbers, cardNumbers)
        }.toMap()
    }
}

data class Card(val id:Int, val winningNumbers:List<Int>, val numbersOnCard:List<Int>, var copies:Int = 1)


