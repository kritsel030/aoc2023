package day12

import base.BaseSolver
import base.INPUT_VARIANT
import java.lang.Math.pow

fun main(args: Array<String>) {
    P12_Solver().solve(INPUT_VARIANT.REAL)
}

class P12_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "hot springs"
    }

    // answer: 7195
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        return inputLines.sumOf { it ->
            val line = it
            var springs = line.split(' ')[0]
            val brokenSpringsSummary = line.split(' ')[1].split(',').map { it.toInt() }

            determineNumberOfMatchingArrangement(springs, brokenSpringsSummary)
        }
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return inputLines
            .mapIndexed{ index, it ->
                println ("line ${index+1} of ${inputLines.size}")
                val line = it
                var springs = line.split(' ')[0]
                var brokenSpringsSummary = line.split(' ')[1].split(',').map{it.toInt()}.toMutableList()

                val count1 = determineNumberOfMatchingArrangement(springs, brokenSpringsSummary)
//                println("[$index] $springs $brokenSpringsSummary: $count1")
                springs = springs + '?' + springs
                brokenSpringsSummary.addAll(brokenSpringsSummary)
                val count2 = determineNumberOfMatchingArrangement(springs, brokenSpringsSummary)
//                println("[$index] $springs $brokenSpringsSummary: $count2")
                val prediction = count1 * (count2/count1) * (count2/count1) * (count2/count1) * (count2/count1)
                println("  prediction = $prediction")
                println("-----------------------------------")
                prediction
            }.sum()
    }

    private fun determineNumberOfMatchingArrangement(
        springs: String,
        brokenSpringsSummary: List<Int>
    ): Int {
//        val unknownSpringsCount = springs.count { spring -> spring == '?' }
//        return (0 until pow(2.0, unknownSpringsCount.toDouble()).toInt())
//            .map {
//                var springsVariant = springs
//                //                println("$it -> ${Integer.toBinaryString(it).padStart(unknownSpringsCount, '0')}")
//                Integer.toBinaryString(it).padStart(unknownSpringsCount, '0')
//                    .forEach { bit ->
//                        springsVariant = springsVariant.replaceFirst('?', if (bit == '0') '.' else '#')
//                    }
//                fitsGivenSummary(springsVariant, brokenSpringsSummary)
//            }
//            .count { it }
        val unknownSpringIndices = springs
            .mapIndexed { index, spring ->  if (spring == '?') index else -1 }
            .filter { it >= 0 }
        val springsVariant = springs.toCharArray()
//        println("spring variant before: ${springsVariant.map{it}.joinToString("")}")
        var matchCounter = 0
        val noOfVariants = pow(2.0, unknownSpringIndices.size.toDouble()).toInt()
        println("  noOfVariants: 2^${unknownSpringIndices.size} = $noOfVariants")
        (0 until noOfVariants )
            .forEach {
                val unknownBits = Integer.toBinaryString(it).padStart(unknownSpringIndices.size, '0')
                unknownSpringIndices.forEachIndexed { unknownIndex, springIndex ->
                    springsVariant[springIndex] = if (unknownBits[unknownIndex] == '0') '.' else '#'
                }
//                println("spring variant ${springsVariant.map{it}.joinToString("")}")
                if (fitsGivenSummary(String(springsVariant), brokenSpringsSummary)) {
                    matchCounter++
                }
            }
        return matchCounter
//        println("-------------")
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


