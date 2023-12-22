package day20

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P20_Solver().solve(INPUT_VARIANT.REAL)
}

class P20_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "pulses"
    }
    // 689162712 too low
    // 787569643 too low
    // 818029817 is not right
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val moduleMap = inputLines.map {
            val (rawInput, rawOutputs) = it.split(" -> ")
            val outputs = rawOutputs.split(", ")
            val module:Module =
                if (rawInput.startsWith('%'))
                    FlipFlopModule(rawInput.substring(1).trim(), outputs)
                else if (rawInput.startsWith('&'))
                    ConjunctionModule(rawInput.substring(1).trim(), outputs)
                else {
                    Module("broadcaster", outputs)
                }
            module.name to module
        }.toMap().toMutableMap()
        moduleMap["button"] = Module("button", listOf("broadcaster"))

        // create plain modules for each unknown output
        // set inputs for all modules (actually only necessary for conjunction modules)
        // and prepare all modules
        var newModules:MutableMap<String, Module> = mutableMapOf()
        moduleMap
            .forEach { (moduleName, module) ->
                module.outputs.forEach { outputName ->
                    if (moduleMap.containsKey(outputName))
                        moduleMap[outputName]!!.inputs.add(moduleName)
                    else newModules[outputName] = Module(outputName)
                module.prepare()} }
        moduleMap.putAll(newModules)

        val cycles:Int = 1000
        var pulseCounters:MutableMap<Boolean, Long> = mutableMapOf(false to 0, true to 0)
        (0 until cycles).forEach { cycle ->
            moduleMap["button"]!!.receivePulse("nowhere", false)
            var phases = 0
            while (moduleMap.values.count { it.touched } > 0) {
                phases++
//                println("------------------------------------")
                val touchedModules = moduleMap.values.filter { it.touched }
                val touchedModulesAndOutputPulses = touchedModules.associateWith { it.determineOutputPulse() }
                touchedModules.forEach { it.reset() }
                //   println("modules which received a pulse: ${outputPulses.keys.map{it.name}.joinToString(", ")} ")
                touchedModulesAndOutputPulses.forEach { (touchedModule, pulse)
                    -> addPulseCounters(pulseCounters, touchedModule.sendPulsesToOutputs(pulse, moduleMap))
                }
//                println("pulse counters: $pulsesSent")
            }
            println ("cycle $cycle: $phases phases")
            val backInInitialPos = allModulesBackInInitialPosition(moduleMap)
            if (backInInitialPos) {
                println("back in initial position after $cycle cycles")
            }
        }

//        println("------------------------------------")
        println("pulse counters: $pulseCounters")
        return pulseCounters[false]!! * pulseCounters[true]!!
    }

    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        return "TODO"
    }

    companion object {
        fun signalName(signal:Boolean) : String {
            return if (signal) "HIGH" else "LOW"
        }

        fun addPulseCounters(pulseCounts:MutableMap<Boolean, Long>, countsToAdd:Map<Boolean, Long>?) {
            if (countsToAdd != null) {
                if (countsToAdd.containsKey(false))
                    pulseCounts[false] = pulseCounts[false]!! + countsToAdd[false]!!
                if (countsToAdd.containsKey(true))
                    pulseCounts[true] = pulseCounts[true]!! + countsToAdd[true]!!
            }
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

open class Module(val name:String, val outputs:List<String> = emptyList()) {
    var inputs = mutableListOf<String>()
    var touched = false

    open fun prepare() { }

    var receivedPulses = mutableMapOf<String, Boolean>()

    fun receivePulse(from:String, pulse:Boolean) {
//        println("  $name received ${signalName(pulse)} from $from")
        receivedPulses[from] = pulse
        this.touched = true
    }

    /*
    Returns:
    - null: no output pulse
    - false: LOW output pulse
    - true: HIGH output pulse
     */
    open fun determineOutputPulse(): Boolean? {
        return if (receivedPulses.isNotEmpty()) receivedPulses.values.first() else null
    }

    // return the number of pulses which have been sent
    fun sendPulsesToOutputs(outputPulse:Boolean?, map:Map<String, Module>) : Map<Boolean, Long>? {
        var result:Map<Boolean, Long>? = null
        if (outputPulse != null) {
            outputs.forEach {
//                    println("  $name sends ${signalName(outputPulse)} to $it")
                map[it]!!.receivePulse(this.name, outputPulse) }
            result = mapOf(outputPulse to outputs.count().toLong())
        }
        return result
    }

    open fun reset() {
        receivedPulses = mutableMapOf()
        touched = false
    }
}

class FlipFlopModule(name: String, outputs: List<String>) : Module(name, outputs) {
    var on = false

    /*
    Returns:
    - null: no output pulse
    - false: LOW output pulse
    - true: HIGH output pulse
    */
    override fun determineOutputPulse() : Boolean? {
        var outputPulse:Boolean? = null
        if(receivedPulses.isNotEmpty()) {
            val receivedPulse = receivedPulses.values.first()
            if (!receivedPulse) {
                // If a flip-flop module receives a high pulse (pulse=true), it is ignored and nothing happens.
                // This means we're only interested in pulse=false (a low pulse)
                if (!on) {
                    // If it was off, it turns on and sends a high pulse (true).
                    on = true
                    outputPulse = true
                } else {
                    // If it was on, it turns off and sends a low pulse (false).
                    on = false
                    outputPulse = false
                }
//                    println("   $name outputs ${signalName(outputPulse)} and on=$on")
            }
        }
        return outputPulse
    }

}

class ConjunctionModule(name:String, outputs: List<String>) : Module(name, outputs) {

    override fun prepare() {
        inputs.forEach {
            receivedPulses[it] = false
        }
    }

    /*
    Returns:
    - null: no output pulse
    - false: LOW output pulse
    - true: HIGH output pulse
     */
    override fun determineOutputPulse(): Boolean? {
        // only return LOW when all received pulses are HIGH
        // if one or more received pulses is LOW, return HIGH
        // (pulse = false : LOW, pulse = true : HIGH)
        return receivedPulses.values.filter{!it}.isNotEmpty()
    }

    override fun reset() {
        // ro reset of received pulses!
        touched = false
    }

}


