package day02

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P02_Solver().solve(INPUT_VARIANT.REAL)
}

class P02_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "colored cubes game"
    }

    companion object {
        val COLORS_IN_BAG = mapOf("red" to 12, "green" to 13, "blue" to 14)
    }

    // answer: 2406
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        // parse input
        val games = inputLines.map { parseLine(it) }.toList()

        // check valid inputs
        val result = games
            .filter { game ->
                val okPhases = game.second
                    .map { gamePhase ->
                        // return true when the phase is not OK
                        gamePhase.keys
                            .filter { color -> gamePhase[color]!! > COLORS_IN_BAG[color]!! }.toList()}
                    .filter { list -> list.isEmpty()}
//                println("${game.first} $okPhases")
                okPhases.size == game.second.size }
            .map {game -> game.first}
            .sum()

        return result
    }

    fun parseLine(line:String) : Pair<Int, List<Map<String, Int>>> {
        val id = line.substring("Game ".length, line.indexOf(':')).toInt()
        val draws = line.substring(line.indexOf(':')+1).split("; ")
        val phaseResults:MutableList<Map<String, Int>> = mutableListOf()
        draws.forEach {
            val colorMap = mutableMapOf<String, Int>()
            it.split(", ").forEach { colorResult ->
                COLORS_IN_BAG.keys.forEach { color ->
                    if (colorResult.endsWith(color)) {
                        colorMap[color] = colorResult.substring(0, colorResult.length - color.length).trim().toInt()
                    }
                }
            }
            phaseResults.add(colorMap)
        }
//        println("gameID $id: $phaseResults")
        return Pair(id, phaseResults)
    }

    // answer: 78375
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        // parse input
        val games = inputLines.map { parseLine(it) }.toList()

        // check valid inputs
        val result = games
            .map{ game ->
                COLORS_IN_BAG.keys.map { color ->
                    color to game.second
                        .map { draw -> draw[color] }
                        .filterNotNull()
                        .toMutableList()
                        .sorted()
                        .last()}
                    .toMap()
                .values.reduce { accumulator, element ->
                    accumulator * element }
            }
            .sum()

        return result
    }
}


