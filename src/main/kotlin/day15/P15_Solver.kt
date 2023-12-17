package day15

import base.BaseSolver
import base.INPUT_VARIANT

fun main(args: Array<String>) {
    P15_Solver().solve(INPUT_VARIANT.REAL)
}

class P15_Solver : BaseSolver() {

    override fun getPuzzleName(): String {
        return "hash"
    }

    // answer: 498538
    override fun solvePart1(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any{
        val values = inputLines[0].split(',')
        return values.sumOf{hash(it)}
    }

    // answer: 286278
    override fun solvePart2(inputLines: List<String>, inputVariant: INPUT_VARIANT): Any {
        // initialize boxes
        val boxes:MutableMap<Int, MutableMap<String, Int>> = mutableMapOf()
        (0..255).forEach{boxes[it] = mutableMapOf<String, Int>() }

        // add and remove lenses
        inputLines[0].split(',').forEach {
            var label:String = ""
            var operation:Char = ' '
            var focalLength:Int = 0
            if (it.contains('=')) {
                operation = '='
                label = it.substring(0, it.indexOf('='))
                focalLength = it.substring(it.indexOf('=')+1, it.length).toInt()
            } else {
                operation = '-'
                label = it.substring(0, it.indexOf('-'))
            }

            val boxNo = hash(label)
            var lenses = boxes[boxNo]!!
            if (operation == '=') {
                lenses.set(label, focalLength)
            } else {
                lenses.remove(label)
            }
        }

        // calculate
//        boxes.map{box -> box.value.values.mapIndexed{(index, value:Int) -> value}.sum()}
        return boxes.map{(boxNo, box) -> box.values.mapIndexed{lenseIndex, focalLength -> (boxNo+1) * ((lenseIndex+1)*focalLength) }.sum()}.sum()
    }

    /*
    Determine the ASCII code for the current character of the string.
Increase the current value by the ASCII code you just determined.
Set the current value to itself multiplied by 17.
Set the current value to the remainder of dividing itself by 256.
     */
    fun hash(value:String) : Int {
        var hash = 0
        value.forEach {
            hash = ((hash + it.code) * 17) % 256
        }
        return hash
    }
}


