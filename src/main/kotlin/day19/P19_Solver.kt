package day19

import base.BaseSolver
import base.INPUT_VARIANT
import com.sun.corba.se.spi.orbutil.threadpool.Work
import day07.CardType
import java.lang.IllegalStateException

fun main(args: Array<String>) {
    P19_Solver().solve(INPUT_VARIANT.EXAMPLE)
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

    /*
px{a<2006:qkq,m>2090:A,rfg}           // a >= 2006, m <= 2090
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}              // s >= 537, x > 2440
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}                         // x <= 2262
in{s<1351:px,qqz}                      // cancel each other out
qqz{s>2770:qs,m<1801:hdj,R}           // m >= 1181, s<= 2770
gd{a>3333:R,R}
hdj{m>838:A,pv}

// a : 1095 possibilities
// x : 3820 possibilities
// m : 930 possibilities
// s : 2243 possibilities
     */
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val chunkedInput: List<List<String>> = chunkInput(inputLines)

        // parse workflows
        val workflows = parseWorkflows(chunkedInput[0])

        val workflowInRules:MutableMap<String, MutableList<Rule>> = mutableMapOf<String, MutableList<Rule>>()
        workflows.values.forEach { it.rules.forEach { rule ->
            val workflowInRule = when (rule) {
                is ConditionalWorkflowRule -> rule.nextWorkflow
                is WorkflowRule -> rule.nextWorkflow
                else -> null
            }
            if (workflowInRule != null) {
                if (workflowInRules[workflowInRule] == null) {
                    workflowInRules[workflowInRule] = mutableListOf(rule)
                } else {
                    workflowInRules[workflowInRule]!!.add(rule)
                }
            }
        }}

        val statusInRules = mutableMapOf<Status, MutableList<Rule>>()
        workflows.values.forEach { it.rules.forEach { rule ->
            val statusInRule = when (rule) {
                is ConditionalStatusRule -> rule.nextStatus
                is StatusRule -> rule.nextStatus
                else -> null
            }
            if (statusInRule != null) {
                if (statusInRules[statusInRule] == null) {
                    statusInRules[statusInRule] = mutableListOf(rule)
                } else {
                    statusInRules[statusInRule]!!.add(rule)
                }
            }
        }}

        var ratingValues = mutableMapOf(
            'x' to (1 .. 4000).toList(),
            'm' to (1 .. 4000).toList(),
            'a' to (1 .. 4000).toList(),
            's' to (1 .. 4000).toList(),
            )

        val rejectedRules = statusInRules[Status.REJECTED]
        rejectedRules?.forEach { rule ->
            when (rule) {
                is StatusRule ->
                    (rule.index-1 downTo 0).forEach { ruleIndex ->
                        val previousRule = rule.workflow!!.rules[ruleIndex]
                        if (previousRule is ConditionalRule) {
                            when (previousRule.operator) {
                                Operator.LT -> ratingValues[previousRule.category] = ratingValues[previousRule.category]!!.filter { it >= previousRule.value }
                                Operator.GT -> ratingValues[previousRule.category] = ratingValues[previousRule.category]!!.filter { it <= previousRule.value }
                            }
                        }}
                //is ConditionalStatusRule -> {//TODO }
                else -> {}
            }
            if (! rule.workflow!!.name.equals("in")) {
                // lookup the rules in which this rule's workflow occurs
                val referredInRules: MutableList<Rule>? = workflowInRules[rule.workflow!!.name]
                if (referredInRules != null) {
                    println("referredInRules for ${rule.workflow!!.name}: $referredInRules")
                } else {
                    throw IllegalStateException("we shouldn't get here, workflow name ${rule.workflow!!.name} not found in any rules")
                }
            }

        }

        // expected: 167.409.079.868.000
        // 54.068.685.924.000
        return ratingValues['x']!!.size.toLong() * ratingValues['m']!!.size.toLong() * ratingValues['a']!!.size.toLong() * ratingValues['s']!!.size.toLong()
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
                    val value = conditionAndResult[0].substring(2).toLong()
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
}

data class Part (val map:MutableMap<Char, Long>, var status:Status=Status.IN_WORKFLOW )

class Workflow(val name:String, val rules:List<Rule>) {

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
}

abstract class Rule {

    var workflow:Workflow? = null
    var index:Int = -1
    abstract fun processPart(part:Part) : Pair<Boolean, String?>
}

abstract class ConditionalRule(val category:Char, val operator:Operator, val value:Long): Rule() {}

class ConditionalWorkflowRule(category:Char, operator: Operator, value:Long, val nextWorkflow:String) : ConditionalRule(category, operator, value) {
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
}

class ConditionalStatusRule(category:Char, operator:Operator, value:Long, val nextStatus:Status): ConditionalRule(category, operator, value) {
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
