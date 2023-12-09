package day08

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P08_Solver().solve(INPUT_VARIANT.REAL)
}

class P08_Solver : BaseSolver() {
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
//        val (instructions, nodeInstructions) = parseInput(inputLines)
//
//        val start = "AAA"
//        val target = "ZZZ"
//        var current = start
//        var steps = 0
//        while (! current.equals(target)) {
//            for (command in instructions) {
//                steps++
//                current = nodeInstructions[current]!![command]!!
//                if (current.equals(target))
//                    break
//            }
//        }
//        return steps
        return "dummy"
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val (instructions, nodeInstructions) = parseInput(inputLines)

        val startNodes = nodeInstructions.keys.filter{it.endsWith('A')}
        println("startNodes: $startNodes")

        var currentNodes = startNodes.map{it to it}.toMap()
        var steps:Long = 0
        //var visitedNodes:MutableMap<String, MutableMap<String, MutableList<Int>>> = mutableMapOf()
        var visitedNodes = startNodes.map{it to mutableMapOf<String, MutableList<Long>>() }.toMap()

        var cycleFoundNodes:MutableMap<String, Pair<Long, Long>> = mutableMapOf()

        while ( currentNodes.values.count{it.endsWith('Z')} != currentNodes.size) {
            for (command in instructions) {
                steps++
                currentNodes = currentNodes
                    .filter{!cycleFoundNodes.containsKey(it.key)}
                    .map{it.key to nodeInstructions[it.value]!![command]!!}
                    .toMap()
//                if (currentNodes.values.count{it.endsWith('Z')} == currentNodes.size)
//                    break

//                // print when we've reached a 'Z' field for one of the start nodes
//                currentNodes.forEach{
//                    if (it.value.endsWith('Z'))
//                        println ("${it.key} after $steps steps at $it.value")
//                }

                // register what we have visited when
                currentNodes.forEach {
                    if (it.value.endsWith('Z')) {
                        if (!visitedNodes[it.key]!!.containsKey(it.value)) {
                            visitedNodes[it.key]!![it.value] = mutableListOf()
                        }
                        visitedNodes[it.key]!![it.value]!!.add(steps)

                        // print when we've detected a cycle
                        if (visitedNodes[it.key]!![it.value]!!.size > 1) {
                            val latestTimestamp = visitedNodes[it.key]!![it.value]!!.last()
                            visitedNodes[it.key]!![it.value]!!.forEach { timestamp ->
                                val zero:Long = 0
                                if (timestamp != latestTimestamp && (latestTimestamp - timestamp) % instructions.length == zero) {
                                    println("cycle detected for start node ${it.key}: visited ${it.value} at $timestamp and $latestTimestamp")
                                    cycleFoundNodes[it.key] = Pair(timestamp, timestamp / instructions.length)
                                }
                            }
                        }
                    }
                }
            }
        }
        println("cycleFoundNodes: $cycleFoundNodes")
        println("currentNodes: $currentNodes")
        return cycleFoundNodes.map{it.value.second}.reduce { acc, i -> acc * i } * instructions.length
    }

    private fun parseInput(inputLines: List<String>): Pair<String, Map<String, Map<Char, String>>> {
        val instructions = inputLines[0]

        val nodeInstructions = (2 until inputLines.size)
            .map {
                val elements = inputLines[it].split("=")
                val nodeId = elements[0].trim()
                val leftAndRight = elements[1].trim().substring(1, elements[1].trim().length - 1).split(",")
                val navigateInstructions = mapOf('L' to leftAndRight[0].trim(), 'R' to leftAndRight[1].trim())
                nodeId to navigateInstructions
            }
            .toMap()
        return Pair(instructions, nodeInstructions)
    }

}


