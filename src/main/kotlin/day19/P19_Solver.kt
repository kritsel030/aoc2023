package day19

import base.BaseSolver
import base.INPUT_VARIANT
import java.lang.IllegalStateException

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
        // example: px{a<2006:qkq,m>2090:A,rfg}
        val workflows = chunkedInput[0].map{line ->
            val workflowName = line.substring(0, line.indexOf('{'))
            val rulesLine = line.substring(line.indexOf('{')+1, line.length-1)
            val rulesRaw = rulesLine.split(',')
            val rules = rulesRaw.map { rawRule ->
                if (rawRule.contains(':')) {
                    val conditionAndResult = rawRule.split(':')
                    val rawResult = conditionAndResult[1]
                    val category = conditionAndResult[0][0]
                    val operator = conditionAndResult[0][1]
                    val value = conditionAndResult[0].substring(2).toLong()
                    if (rawResult.length == 1) {
                        val nextStatus = when (rawResult) {
                            "A" -> Status.ACCEPTED
                            "R" -> Status.REJECTED
                            else -> throw IllegalStateException("unknown status ${rawResult}")
                        }
                        ConditionalStatusRule(category, operator, value, nextStatus)
                    } else {
                        ConditionalWorkflowRule(category, operator, value, rawResult)
                    }
                } else if (rawRule.length == 1) {
                        val nextStatus = when (rawRule) {
                            "A" -> Status.ACCEPTED
                            "R" -> Status.REJECTED
                            else -> throw IllegalStateException("unknown status ${rawRule}")
                        }
                    StatusRule(nextStatus)
                } else {
                    WorkflowRule(rawRule)
                }
            }
            workflowName to Workflow(workflowName, rules)
        }.toMap()

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

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        // 167.409.079.868.000
        return "TODO"
    }
}

data class Part (val map:MutableMap<Char, Long>, var status:Status=Status.IN_WORKFLOW )

class Workflow(val name:String, val rules:List<Rule>) {
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
}

abstract class Rule {
    abstract fun processPart(part:Part) : Pair<Boolean, String?>
}

class ConditionalWorkflowRule(val category:Char, val operator:Char, val value:Long, val nextWorkflow:String) : Rule() {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        val conditionMet = when (operator){
            '>' -> part.map[category]!! > value
            '<' -> part.map[category]!! < value
            else -> throw IllegalStateException("operator $operator not supported")
        }
        return if (conditionMet)
            Pair(false, nextWorkflow)
        else
            Pair(true, null)
    }
}

class ConditionalStatusRule(val category:Char, val operator:Char, val value:Long, val nextStatus:Status): Rule() {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        val conditionMet = when (operator){
            '>' -> part.map[category]!! > value
            '<' -> part.map[category]!! < value
            else -> throw IllegalStateException("operator $operator not supported")
        }
        if (conditionMet) {
            part.status = nextStatus
            return Pair(false, null)
        }
        return Pair(true, null)
    }
}

class WorkflowRule(val nextWorkflow:String) : Rule() {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        return Pair(false, nextWorkflow)
    }
}

class StatusRule(val nextStatus:Status) : Rule() {
    override fun processPart(part: Part) : Pair<Boolean, String?>{
        part.status = nextStatus
        return Pair(false, null)
    }
}

enum class Status {
    IN_WORKFLOW,
    ACCEPTED,
    REJECTED
}
