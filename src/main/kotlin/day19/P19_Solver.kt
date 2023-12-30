package day19

import base.BaseSolver
import base.INPUT_VARIANT
import day19.P19_Solver.Companion.applyBoundaryRestrictions
import day19.P19_Solver.Companion.applyInverseBoundaryRestrictions
import day19.P19_Solver.Companion.cloneRatingBoundaries
import kotlin.math.max
import kotlin.math.min

fun main(args: Array<String>) {
    P19_Solver().solve(INPUT_VARIANT.REAL)
}

class P19_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "parts workflow"
    }

    // answer: 389114
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val chunkedInput: List<List<String>> = chunkInput(inputLines)

        // parse workflows
        val workflows = parseWorkflows(chunkedInput[0])

        // parse parts
        // example: {x=787,m=2655,a=1222,s=2876}
        val parts:List<Part> = chunkedInput[1].map { line ->
            val map = line
                .substring(1, line.length - 1)
                .split(',')
                .map { elem ->
                    val catAndValue = elem.split('=')
                    catAndValue[0][0] to catAndValue[1].toLong()
                }.toMap().toMutableMap()
            Part(map)
        }

        parts.forEach { workflows["in"]!!.execute(it, workflows) }
        return parts.filter { it.status == Status.ACCEPTED }.sumOf{it.map.values.sum()}
    }

    // answer: 125051049836302
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val chunkedInput: List<List<String>> = chunkInput(inputLines)

        // parse workflows
        val workflows = parseWorkflows(chunkedInput[0])

        val ratingCategories = listOf('x', 'm', 'a', 's')
        val initialRatingBoundaries = ratingCategories.associateWith { Pair(1, 4000) }.toMutableMap()

        val ratingBoundariesList = workflows["in"]!!.determineRatingBoundaries(initialRatingBoundaries, workflows)

        return ratingBoundariesList.sumOf { boundaryMap ->
            boundaryMap.values
            .map {(it.second - it.first + 1L)}
            .reduce { accumulator, element -> accumulator * element }
        }
    }

    // example: px{a<2006:qkq,m>2090:A,rfg}
    fun parseWorkflows(input:List<String>) : Map<String, Workflow> {
        return input.map{line ->
            val workflowName = line.substring(0, line.indexOf('{'))
            val rulesLine = line.substring(line.indexOf('{')+1, line.length-1)
            val rulesRaw = rulesLine.split(',')
            val rules = rulesRaw.map { rawRule ->
                if (rawRule.contains(':')) {
                    val conditionAndResult = rawRule.split(':')
                    val rawResult = conditionAndResult[1]
                    val category = conditionAndResult[0][0]
                    val operator = Operator[conditionAndResult[0][1]]!!
                    val value = conditionAndResult[0].substring(2).toInt()
                    if (rawResult.length == 1) {
                        val nextStatus = Status[rawResult[0]]!!
                        ConditionalStatusRule(category, operator, value, nextStatus)
                    } else {
                        ConditionalWorkflowRule(category, operator, value, rawResult)
                    }
                } else if (rawRule.length == 1) {
                    val nextStatus = Status[rawRule[0]]!!
                    StatusRule(nextStatus)
                } else {
                    WorkflowRule(rawRule)
                }
            }
            workflowName to Workflow(workflowName, rules)
        }.toMap()

    }

    companion object {
        fun applyBoundaryRestrictions(ratingBoundaries:MutableMap<Char, Pair<Int, Int>>, category: Char, operator: Operator, value:Int) : MutableMap<Char, Pair<Int, Int>> {
            when (operator) {
                Operator.GT -> ratingBoundaries[category] = Pair(max(value+1, ratingBoundaries[category]!!.first), ratingBoundaries[category]!!.second)
                Operator.LT -> ratingBoundaries[category] = Pair(ratingBoundaries[category]!!.first, min(value-1, ratingBoundaries[category]!!.second))
            }
            return ratingBoundaries
        }

        fun applyInverseBoundaryRestrictions(ratingBoundaries:MutableMap<Char, Pair<Int, Int>>, category: Char, operator: Operator, value:Int) : MutableMap<Char, Pair<Int, Int>> {
            when (operator) {
                Operator.GT -> ratingBoundaries[category] = Pair(ratingBoundaries[category]!!.first, min(value, ratingBoundaries[category]!!.second))
                Operator.LT -> ratingBoundaries[category] = Pair(max(value, ratingBoundaries[category]!!.first), ratingBoundaries[category]!!.second)
            }
            return ratingBoundaries
        }

        fun cloneRatingBoundaries(orig:Map<Char, Pair<Int, Int>>) : MutableMap<Char, Pair<Int, Int>>{
            return orig.map {(key, value) -> key to Pair(value.first, value.second)}.toMap().toMutableMap()
        }
    }
}

data class Part (val map:MutableMap<Char, Long>, var status:Status=Status.IN_WORKFLOW )

class Workflow(val name:String, var rules:List<Rule>) {

    init {
        rules.forEachIndexed { index, rule ->
            rule.index = index
            rule.workflow = this }
    }


    fun execute(part:Part, workflows:Map<String, Workflow>) {
        for (rule in rules) {
            val (doContinue, nextWorkflow) = rule.processPart(part)
            if (nextWorkflow != null) {
                workflows[nextWorkflow]!!.execute(part, workflows)
                break
            } else if (!doContinue) {
                break
            }
        }
    }

    fun determineRatingBoundaries(ratingBoundaries:MutableMap<Char, Pair<Int, Int>>, workflows: Map<String, Workflow>) : List<Map<Char, Pair<Int, Int>>> {
        var nextRuleBoundaries = ratingBoundaries
        return rules
            .map { rule ->
                val result = rule.determineRatingBoundaries(cloneRatingBoundaries(nextRuleBoundaries), workflows).toMutableList()
                if (rule is ConditionalWorkflowRule) {
                    nextRuleBoundaries = applyInverseBoundaryRestrictions(cloneRatingBoundaries(nextRuleBoundaries), rule.category, rule.operator, rule.value)
                }
                if (rule is ConditionalStatusRule) {
                    nextRuleBoundaries = applyInverseBoundaryRestrictions(cloneRatingBoundaries(nextRuleBoundaries), rule.category, rule.operator, rule.value)
                }
                result
            }
            .flatten()
    }
}

abstract class Rule {

    var workflow: Workflow? = null
    var index: Int = -1
    abstract fun processPart(part: Part): Pair<Boolean, String?>

    abstract fun determineRatingBoundaries(
        ratingBoundaries: MutableMap<Char, Pair<Int, Int>>,
        workflows: Map<String, Workflow>
    ): List<Map<Char, Pair<Int, Int>>>
}
//abstract class ConditionalRule(val category:Char, val operator:Operator, val value:Long): Rule() {}

open class WorkflowRule(val nextWorkflow:String) : Rule() {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        return Pair(false, nextWorkflow)
    }

    override fun determineRatingBoundaries(
        ratingBoundaries: MutableMap<Char, Pair<Int, Int>>,
        workflows: Map<String, Workflow>
    ): List<Map<Char, Pair<Int, Int>>> {
        return workflows[nextWorkflow]!!.determineRatingBoundaries(ratingBoundaries, workflows)
    }
}

open class StatusRule(val nextStatus:Status) : Rule() {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        part.status = nextStatus
        return Pair(false, null)
    }

    override fun determineRatingBoundaries(
        ratingBoundaries: MutableMap<Char, Pair<Int, Int>>,
        workflows: Map<String, Workflow>
    ): List<Map<Char, Pair<Int, Int>>> {
        return if (nextStatus == Status.ACCEPTED)
            listOf(ratingBoundaries)
        else
            listOf()
    }
}

class ConditionalWorkflowRule(val category:Char, val operator: Operator, val value:Int, nextWorkflow:String) : WorkflowRule(nextWorkflow) {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        val conditionMet = when (operator){
            Operator.GT -> part.map[category]!! > value
            Operator.LT -> part.map[category]!! < value
        }
        return if (conditionMet)
            Pair(false, nextWorkflow)
        else
            Pair(true, null)
    }

    override fun determineRatingBoundaries(
        ratingBoundaries: MutableMap<Char, Pair<Int, Int>>,
        workflows: Map<String, Workflow>
    ): List<Map<Char, Pair<Int, Int>>> {
       return workflows[nextWorkflow]!!
           .determineRatingBoundaries(applyBoundaryRestrictions(ratingBoundaries, category, operator, value), workflows)
    }
}

class ConditionalStatusRule(val category:Char, val operator:Operator, val value:Int, nextStatus:Status): StatusRule(nextStatus) {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        val conditionMet = when (operator){
            Operator.GT -> part.map[category]!! > value
            Operator.LT -> part.map[category]!! < value
        }
        if (conditionMet) {
            part.status = nextStatus
            return Pair(false, null)
        }
        return Pair(true, null)
    }

    override fun determineRatingBoundaries(
        ratingBoundaries: MutableMap<Char, Pair<Int, Int>>,
        workflows: Map<String, Workflow>
    ): List<Map<Char, Pair<Int, Int>>> {
        return if (nextStatus == Status.ACCEPTED) {
            listOf(applyBoundaryRestrictions(ratingBoundaries, category, operator, value))
        } else {
            emptyList()
        }
    }
}


enum class Status (val label:Char){
    IN_WORKFLOW('X'),
    ACCEPTED('A'),
    REJECTED('R');

    companion object {
        private val map:Map<Char, Status> = Status.values().associateBy { it.label }
        operator fun get(label: Char) = map[label]
    }
}

enum class Operator(val char:Char) {
    GT('>'),
    LT('<');

    companion object {
        private val map:Map<Char, Operator> = Operator.values().associateBy { it.char }
        operator fun get(char: Char) = map[char]
    }
}
