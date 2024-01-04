package day20

import base.BaseSolver
import base.INPUT_VARIANT
import day20.P20_Solver.Companion.signalName
import java.util.LinkedList
import java.util.Queue

fun main(args: Array<String>) {
    P20_Solver().solve(INPUT_VARIANT.REAL)
}

class P20_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "pulses"
    }
    // answer: 925955316
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        var msgQueue:Queue<Message> = LinkedList()
        var moduleMap:MutableMap<String, Module> = mutableMapOf()
        // parse input
        parseInput(inputLines, moduleMap, msgQueue)

        initializeModules(moduleMap, msgQueue)

        val cycles:Int = 1000
        var pulseCounters:MutableMap<Boolean, Long> = mutableMapOf(false to 0L, true to 0L)
        (0 until cycles).forEach { cycle ->
//            println("cycle ${cycle+1}")
            moduleMap["button"]!!.receivePulse("nowhere", false)
            while(msgQueue.isNotEmpty()) {
                val message = msgQueue.poll()
//                println("  ${message.from} sends ${signalName(message.pulse!!)} to ${message.to}")
                pulseCounters[message.pulse] = pulseCounters[message.pulse]!!+1
                moduleMap[message.to]!!.receivePulse(message.from, message.pulse)
            }
            val backInInitialPos = allModulesBackInInitialPosition(moduleMap)
            if (backInInitialPos) {
                println("back in initial position after $cycle cycles")
            }
        }

//        println("------------------------------------")
//        println("pulse counters: $pulseCounters")
        return pulseCounters[false]!! * pulseCounters[true]!!
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        var msgQueue:Queue<Message> = LinkedList()
        var moduleMap:MutableMap<String, Module> = mutableMapOf()
        // parse input
        parseInput(inputLines, moduleMap, msgQueue)

        initializeModules(moduleMap, msgQueue)

        var cycle:Int = 0
        var endStateReached = false
        while (!endStateReached && cycle < 50) {
            cycle++
            println("cycle ${cycle}")
            moduleMap["button"]!!.receivePulse("nowhere", false)
            while(msgQueue.isNotEmpty()) {
                val message = msgQueue.poll()
//                println("  ${message.from} sends ${signalName(message.pulse!!)} to ${message.to}")
                if (message.to == "rx" && message.pulse == false) {
                    println("${signalName(message.pulse)} sent to ${message.to}")
                    endStateReached = true
                    break
                }
                if (message.to == "qn" || message.from == "qn") {
                    println("  ${message.from} sends ${signalName(message.pulse!!)} to ${message.to}")
                }
                moduleMap[message.to]!!.receivePulse(message.from, message.pulse)
            }
            val backInInitialPos = allModulesBackInInitialPosition(moduleMap)
            if (backInInitialPos) {
                println("back in initial position after $cycle cycles")
            }
        }

//        println("------------------------------------")
//        println("pulse counters: $pulseCounters")
        var context = mapOf("success" to endStateReached)
        return Pair(cycle, context)
    }

    private fun parseInput(
        inputLines: List<String>,
        moduleMap: MutableMap<String, Module>,
        msgQueue: Queue<Message>
    ) {
        inputLines.forEach {
            val (rawInput, rawOutputs) = it.split(" -> ")
            val outputs = rawOutputs.split(", ")
            val module: Module =
                if (rawInput.startsWith('%'))
                    FlipFlopModule(rawInput.substring(1).trim(), outputs, moduleMap, msgQueue)
                else if (rawInput.startsWith('&'))
                    ConjunctionModule(rawInput.substring(1).trim(), outputs, moduleMap, msgQueue)
                else {
                    Module("broadcaster", outputs, moduleMap, msgQueue)
                }
            moduleMap[module.name] = module
        }
    }

    private fun initializeModules(
        moduleMap: MutableMap<String, Module>,
        msgQueue: Queue<Message>
    ) {
        // Add button module
        moduleMap["button"] = Module("button", listOf("broadcaster"), moduleMap, msgQueue)

        // create plain modules for each unknown output
        var newModules: MutableMap<String, Module> = mutableMapOf()
        moduleMap.values.forEach { module ->
            module.outputs.forEach { outputName ->
                if (!moduleMap.containsKey(outputName)) {
                    newModules[outputName] = Module(outputName, emptyList(), moduleMap, msgQueue)
                }
            }
        }
        moduleMap.putAll(newModules)

        // initialize all modules
        // (goal: setting LOW input pulses for conjunction modules)
        //        println("initialization ------------------------")
        moduleMap.values.forEach {
            it.sendPulsesToOutputs(false)
        }
        while (msgQueue.isNotEmpty()) {
            val message = msgQueue.poll()
            moduleMap[message.to]!!.receivePulse(message.from, message.pulse)
        }
        moduleMap.values.forEach { it.initialize() }

        //        println("ready for business")
    }

    companion object {
        fun signalName(signal:Boolean) : String {
            return if (signal) "HIGH" else "LOW"
        }

        fun allModulesBackInInitialPosition(moduleMap:Map<String, Module>) : Boolean{
            // count the modules which did not return to their initial position
            return moduleMap.values.count {
                when (it) {
                    is FlipFlopModule -> it.on
                    is ConjunctionModule -> it.receivedPulses.values.count { p -> p } > 0
                    else -> false
                }
            } == 0
        }
    }
}

data class Message(val from:String, val to:String, val pulse:Boolean)

open class Module(val name:String, val outputs:List<String> = emptyList(), val moduleMap: Map<String, Module>, val msgQueue: Queue<Message>) {
//    var outputPulse:Boolean? = null
    var receivedPulse: Boolean? = null

    open fun initialize() {
        receivedPulse = null
    }

    open fun receivePulse(from:String, pulse:Boolean) {
        receivedPulse = pulse
        setOutputPulseAndInternalState()
    }

    // default implementation: output pulse = input pulse
    open fun setOutputPulseAndInternalState() {
        val outputPulse = receivedPulse
        if (outputPulse != null) {
            sendPulsesToOutputs(outputPulse)
        }
    }

    fun sendPulsesToOutputs(pulse:Boolean) {
        outputs.forEach {
//            println("     $name schedules ${signalName(pulse!!)} to $it")
            msgQueue.add(Message(this.name, moduleMap[it]!!.name, pulse!!))
        }
    }
}

class FlipFlopModule(name: String, outputs: List<String>, moduleMap: Map<String, Module>, msgQueue: Queue<Message>) : Module(name, outputs, moduleMap, msgQueue) {
    var on = false

    override fun initialize() {
        super.initialize()
        on = false
    }

    override fun setOutputPulseAndInternalState()  {
        var outputPulse:Boolean? = null
        if(receivedPulse != null) {
            // If a flip-flop module receives a high pulse (pulse=true), it is ignored and nothing happens.
            // This means we're only interested in pulse=false (a low pulse)
            if (!receivedPulse!!) {
                if (!on) {
                    // If it was off, it turns on and sends a high pulse (true).
                    on = true
                    outputPulse = true
                } else {
                    // If it was on, it turns off and sends a low pulse (false).
                    on = false
                    outputPulse = false
                }
            }
        }
        if (outputPulse != null) {
            sendPulsesToOutputs(outputPulse!!)
        }
    }
}

class ConjunctionModule(name:String, outputs: List<String>, moduleMap: Map<String, Module>, msgQueue: Queue<Message>) : Module(name, outputs, moduleMap, msgQueue) {

    var receivedPulses = mutableMapOf<String, Boolean>()

    override fun receivePulse(from:String, pulse:Boolean) {
        receivedPulses[from] = pulse
        setOutputPulseAndInternalState()
    }

    override fun setOutputPulseAndInternalState() {
        // only return LOW when all received pulses are HIGH
        // if one or more received pulses is LOW, return HIGH
        // (pulse = false : LOW, pulse = true : HIGH)
        val outputPulse = receivedPulses.values.filter{!it}.isNotEmpty()
        sendPulsesToOutputs(outputPulse)
    }

}


