package base

import util.FileUtil
import java.lang.Exception
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class BaseSolver {

    fun solve(inputVariant:INPUT_VARIANT=INPUT_VARIANT.REAL, part: Part = Part.BOTH) {
        val fileName = if(inputVariant==INPUT_VARIANT.EXAMPLE) "test-input.txt" else "input.txt"
        val fileName2 = if(inputVariant==INPUT_VARIANT.EXAMPLE) "test-input2.txt" else "input2.txt"
        var resourceURL = this::class.java.getResource(fileName)
        if (resourceURL == null) {
            resourceURL = this::class.java.getResource(fileName2)
            if (resourceURL == null) {
                throw Exception("input file named $fileName nor $fileName2 exist in folder ${getDay()}")
            }
        }
        val input = FileUtil.readMultiLineFile(resourceURL)

        if (part == Part.PART1 || part == Part.BOTH) {
            val start = System.currentTimeMillis()
            val solveResult = solvePart1(input, inputVariant)
            val duration = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)
            var answer : Any = solveResult
            var context : Map<String, Any>? = null
            if (solveResult is Pair<*, *>) {
                answer = solveResult.first!!
                context = solveResult.second as Map<String, Any>?
            }
            printAnswerDetails(1, answer, context, inputVariant, duration)
        }

//        input = FileUtil.readMultiLineFile(resourceURL)
        if (part == Part.PART2 || part == Part.BOTH) {
            val start = System.currentTimeMillis()
            val solveResult = solvePart2(input, inputVariant)
            val duration = (System.currentTimeMillis() - start).toDuration(DurationUnit.MILLISECONDS)
            var answer : Any = solveResult
            var context : Map<String, Any>? = null
            if (solveResult is Pair<*, *>) {
                answer = solveResult.first!!
                context = solveResult.second as Map<String, Any>?
            }
            printAnswerDetails(2, answer, context, inputVariant, duration)
        }
    }

    abstract fun solvePart1(inputLines:List<String>, inputVariant:INPUT_VARIANT) : Any
    abstract fun solvePart2(inputLines:List<String>, inputVariant: INPUT_VARIANT) : Any

    open fun getPuzzleName():String {
        return "<unnamed>"
    }

    fun printAnswerDetails(part: Int, answer: Any, context: Map<String, Any>?, inputVariant: INPUT_VARIANT, duration:Duration) {
        val day =  this::class.java.`package`.name
        val appendix = if (inputVariant == INPUT_VARIANT.EXAMPLE) "(!!!TEST!!!)" else ""

        if (context != null) {
            System.out.printf(
                "answer %s (%s), part #%d: %s in %s (%s) %s",
                day,
                getPuzzleName(),
                part,
                answer,
                duration,
                contextToString(context),
                appendix
            )
        } else {
            System.out.printf(
                "answer %s (%s), part #%d: %s in %s %s",
                day,
                getPuzzleName(),
                part,
                answer,
                duration,
                appendix
            )
        }
        println()
    }

    fun printAnswerDetails(part: Int, answer: Any, inputVariant: INPUT_VARIANT, duration: Duration) {
        printAnswerDetails(part, answer, null, inputVariant, duration)
    }

    // basically return the name of this solver's package (e.g. day01)
    fun getDay() : String {
        return this::class.java.`package`.name
    }

    companion object {
        private fun contextToString(context: Map<String, Any>): String? {
            // convert context into comma separated list of key:value pairs
            val stringJoiner = StringJoiner(", ")
            context.entries.stream().forEach { (key, value): Map.Entry<String, Any> ->
                stringJoiner.add(
                    "$key: $value"
                )
            }
            return stringJoiner.toString()
        }
    }

//    fun readMultiLineFile(testOrReal:TestOrReal) : List<String> {
//        val resourceName = if(testOrReal==TestOrReal.TEST) "test-input.txt" else "input.txt"
//        val fileContent = this::class.java.getResource(resourceName).readText()
//        val resourcePath = Paths.get( this::class.java.getResource(resourceName).path)
//        return Files.readAllLines(resourcePath, StandardCharsets.UTF_8)
//        FileUtil.readMultiLineRelativeFile(resourcePath)
//    }

    fun chunkInput(inputLines: List<String>) : MutableList<MutableList<String>> {
        val newInputLines = inputLines.toMutableList()
        newInputLines.add("")

        var result:MutableList<MutableList<String>> = mutableListOf()
        var currentGroup = mutableListOf<String>()
        newInputLines.forEach {
            if (it.trim().isEmpty()) {
                if (!currentGroup.isEmpty()) {
                    result.add(currentGroup)
                    currentGroup = mutableListOf()
                }
            } else {
                currentGroup.add(it)
            }
        }
        return result
    }

}