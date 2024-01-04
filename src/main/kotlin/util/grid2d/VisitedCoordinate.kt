package util.grid2d

class VisitedCoordinate (val coordinate: Coordinate, val travelledDirection: Direction? = null, val travelledDistance:Int = 1){
    override fun toString() : String {
        return "{coordinate=$coordinate, travelledDirection=$travelledDirection, travelledDistance=$travelledDistance}"
    }

    fun print() {
        println(this.toString())
    }
}