package util.grid3d

import java.lang.IllegalArgumentException

open class MutableGrid3D<T>(gridValues:MutableList<MutableList<MutableList<T>>>, indexBase:Int = 0) : Grid3D<T>(gridValues, indexBase) {

    // initialize a grid of the given dimensions where each cell has value <initialValue>
    constructor(x: Int, y: Int, z: Int, initialValue: T, indexBase: Int = 0) :
            this(MutableList(x) { MutableList(y) { MutableList(z) {initialValue } } }, indexBase) {
    }
    
    val mutableGridValues = gridValues

    open fun setValue(coordinate: Coordinate, value: T) {
        setValue(coordinate.x, coordinate.y, coordinate.z, value)
    }

    open fun setValue(x: Int, y: Int, z: Int, value: T) {
        if (isValidPosition(x, y, z)) {
            val effectiveX = x - indexBase
            val effectiveY = y - indexBase
            val effectiveZ = z - indexBase
            mutableGridValues[effectiveX][effectiveY][effectiveZ] = value
        } else {
            throw IllegalArgumentException("coordinate $x, $y, $z is outside the bounds of this grid")
        }
    }

}
