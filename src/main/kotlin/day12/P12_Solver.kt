package day12

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P12_Solver().solve(INPUT_VARIANT.REAL)
}

class P12_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "hot springs"
    }

    // answer: 7195
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        return inputLines.map{ it ->
            val line = it
            var springs = line.split(' ')[0]
            val brokenSpringsSummary = line.split(' ')[1].split(',').map{it.toInt()}

    //        println("springs: $springs")
    //        println("brokenSpringsSummary: $brokenSpringsSummary")

            val unknownSpringsCount = line.count { spring -> spring == '?' }
                (0 until Math.pow(2.0, unknownSpringsCount.toDouble()).toInt())
                .map{
                    var springsVariant = springs
    //                println("$it -> ${Integer.toBinaryString(it).padStart(unknownSpringsCount, '0')}")
                    Integer.toBinaryString(it).padStart(unknownSpringsCount, '0')
                        .forEach { binary ->
                            springsVariant = springsVariant.replaceFirst('?', if (binary == '0') '.' else '#')}
                    fitsGivenSummary(springsVariant, brokenSpringsSummary)}
                .count{ it }}
            .sum()
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    fun fitsGivenSummary(springs:String, givenDamagedSpringSummary:List<Int>) : Boolean {
//        println("  $springs")
        // add a non-broken spring to the end, this makes things easier in our for loop
        val newSprings:String = "$springs."
        var summary = mutableListOf<Int>()
        var damagedGroupSize = 0
        for (i in newSprings.indices) {
            if (newSprings[i] == '#') {
                damagedGroupSize ++
            } else {
                if (damagedGroupSize > 0) {
                    summary.add(damagedGroupSize)
                    damagedGroupSize = 0
                }
            }
        }
//        println("$springs | $summary | $givenDamagedSpringSummary")
        return summary.equals(givenDamagedSpringSummary)
    }
}


