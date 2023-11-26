package util.grid

class VisitedCoordinate (val coordinate: Coordinate, val travelledDirection: Direction? = null, val travelledDistance:Int? = null){
    override fun toString() : String {
        return "{coordinate=$coordinate, travelledDirection=$travelledDirection, travelledDistance=$travelledDistance}"
    }

    fun print() {
        println(this.toString())
    }
}