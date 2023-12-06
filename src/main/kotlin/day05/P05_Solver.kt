package day05

import base.BaseSolver
import base.INPUT_VARIANT
import java.util.*

fun main(args: Array<String>) {
    P05_Solver().solve(INPUT_VARIANT.REAL)
}

class P05_Solver : BaseSolver() {

    // answer: 662197086
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val almanac = parseInput(inputLines)
        return almanac.seeds
            .map {seedToLocation(it, almanac)}
            .sorted()
            .first()
    }

    // answer: 52510809
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        val almanac = parseInput(inputLines)

        var locationNumber:Long = -1
        var matchingSeedNumberFound: Boolean = false
        val zero:Long = 0
        while (! matchingSeedNumberFound) {
            locationNumber++
            if (locationNumber%1000000 == zero) {
                println("${Date()}: $locationNumber")
            }

            val seedNumber = locationToSeed(locationNumber, almanac)
            if (seedNumberInSeedNumberRanges(seedNumber, almanac)) {
                matchingSeedNumberFound = true
                println("lowest location number $locationNumber <- seed number $seedNumber")
            }
        }
        return locationNumber
    }

    fun parseInput(input:List<String>) : Almanac {
        val almanac = Almanac()
        var currentMap:SourceToTargetMap? = null
        input.forEach { line ->
            if (line.startsWith("seeds: ")) {
                almanac.seeds = line.split(": ")[1].split(" ").map{it.trim().toLong()}.toList()
            } else if (line.contains("map:")){
                val mapNameElements = line.split(" map:")[0].split("-to-")
                currentMap = SourceToTargetMap(mapNameElements[0], mapNameElements[1])
                almanac.sourceToTargetMaps[currentMap!!.source] = currentMap!!
            } else if (!line.isEmpty()) {
                val numbers = line.split(" ").map{it.toLong()}
                currentMap!!.sourceToTargetRangesMap[numbers[0]] = SourceToTargetRange(numbers[0], numbers[1], numbers[2])
            }
        }
        almanac.init()
        return almanac
    }

    private fun seedToLocation(seedNumber: Long, almanac: Almanac): Long {
        if (almanac.seedToSoilCache.containsKey(seedNumber))
            return almanac.seedToSoilCache[seedNumber]!!
        var sourceNumber = seedNumber
        var sourceType = "seed"
        while (almanac.sourceToTargetMaps.containsKey(sourceType)) {
            val sourceToTargetMap = almanac.sourceToTargetMaps[sourceType]
            sourceNumber = sourceToTargetMap!!.sourceNumberToTargetNumber(sourceNumber)
            sourceType = sourceToTargetMap.target
        }
        almanac.seedToSoilCache[seedNumber] = sourceNumber
        return sourceNumber
    }

    private fun locationToSeed(locationNumber: Long, almanac: Almanac): Long {
        var targetNumber = locationNumber
        var targetType = "location"
        while (almanac.targetToSourceMaps.containsKey(targetType)) {
            val targetToSourceMap = almanac.targetToSourceMaps[targetType]
            targetNumber = targetToSourceMap!!.targetNumberToSourceNumber(targetNumber)
            targetType = targetToSourceMap.source
        }
        return targetNumber
    }

    fun seedNumberInSeedNumberRanges(candidateSeedNumber: Long, almanac: Almanac) : Boolean{
        var found = false
        for (x in 0 until almanac.seeds.size/2) {
            if (almanac.seeds[x*2] <= candidateSeedNumber && candidateSeedNumber < almanac.seeds[x*2] + almanac.seeds[x*2+1]){
                found = true
                break
            }
        }
        return found
    }
}

class Almanac(var seeds:List<Long> = emptyList<Long>(), var sourceToTargetMaps:MutableMap<String, SourceToTargetMap> = mutableMapOf(), var seedToSoilCache:MutableMap<Long, Long> = mutableMapOf()) {
    var targetToSourceMaps:Map<String, SourceToTargetMap> = emptyMap()

    fun init() {
        targetToSourceMaps = sourceToTargetMaps.map { (_, map) -> map.target to map }.toMap()
    }
}


class SourceToTargetMap (val source:String, val target:String, var sourceToTargetRangesMap:MutableMap<Long, SourceToTargetRange> = mutableMapOf()) {

    fun sourceNumberToTargetNumber(sourceNumber: Long): Long {
        val targetNumber = sourceToTargetRangesMap.values.map { it.sourceNumberToTargetNumber(sourceNumber) }.filter { it != sourceNumber }
            .firstOrNull() ?: sourceNumber
        return targetNumber
    }

    fun targetNumberToSourceNumber(targetNumber: Long): Long {
        val sourceNumber = sourceToTargetRangesMap.values.map { it.targetToSourceNumber(targetNumber) }.filter { it != targetNumber }
            .firstOrNull() ?: targetNumber
        return sourceNumber
    }
}

class SourceToTargetRange(val targetRangeStart:Long, val sourceRangeStart:Long, val rangeLength:Long) {

    fun sourceNumberToTargetNumber(sourceNumber:Long) : Long{
        return if (sourceRangeStart <= sourceNumber && sourceNumber < sourceRangeStart + rangeLength )
            targetRangeStart + (sourceNumber-sourceRangeStart)
        else
            sourceNumber
    }

    fun targetToSourceNumber(targetNumber:Long) : Long{
        return if (targetRangeStart <= targetNumber && targetNumber < targetRangeStart + rangeLength )
            sourceRangeStart + (targetNumber-targetRangeStart)
        else
            targetNumber
    }

}


