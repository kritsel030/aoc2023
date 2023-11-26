package base

import util.FileUtil
import java.lang.Exception
import java.util.*

abstract class BaseSolver {

    fun solve(testOrReal:TestOrReal=TestOrReal.REAL, part: Part = Part.BOTH) {
        val fileName = if(testOrReal==TestOrReal.TEST) "test-input.txt" else "input.txt"
        val resourceURL = this::class.java.getResource(fileName)
        if (resourceURL == null) {
            throw Exception("resource $fileName does not exist in folder ${getDay()}")
        }
        val input = FileUtil.readMultiLineFile(resourceURL)

        if (part == Part.PART1 || part == Part.BOTH) {
            var solveResult = solvePart1(input, testOrReal)
            var answer : Any = solveResult
            var context : Map<String, Any>? = null
            if (solveResult is Pair<*, *>) {
                answer = solveResult.first!!
                context = solveResult.second as Map<String, Any>?
            }
            printAnswerDetails(1, answer, context, testOrReal)
        }
        if (part == Part.PART2 || part == Part.BOTH) {
            var solveResult = solvePart2(input, testOrReal)
            var answer : Any = solveResult
            var context : Map<String, Any>? = null
            if (solveResult is Pair<*, *>) {
                answer = solveResult.first!!
                context = solveResult.second as Map<String, Any>?
            }
            printAnswerDetails(2, answer, context, testOrReal)
        }
    }

    abstract fun solvePart1(inputLines:List<String>, testOrReal:TestOrReal) : Any
    abstract fun solvePart2(inputLines:List<String>, testOrReal: TestOrReal) : Any

    fun printAnswerDetails(part: Int, answer: Any, context: Map<String, Any>?, testOrReal: TestOrReal) {
        val day =  this::class.java.`package`.name
        val appendix = if (testOrReal == TestOrReal.TEST) "(!!!TEST!!!)" else ""

        if (context != null) {
            System.out.printf(
                "answer %s, part #%d: %s (%s) %s",
                day,
                part,
                answer,
                contextToString(context),
                appendix
            )
        } else {
            System.out.printf(
                "answer %s, part #%d: %s %s",
                day,
                part,
                answer,
                appendix
            )
        }
        println()
    }

    fun printAnswerDetails(part: Int, answer: Any, testOrReal: TestOrReal) {
        printAnswerDetails(part, answer, null, testOrReal)
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

}