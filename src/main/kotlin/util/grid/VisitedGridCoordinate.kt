package util.grid

class VisitedGridCoordinate<T> (val coordinate: Coordinate, val travelledDirection: Direction? = null, val travelledDistance:Int? = null, val value:T){
    override fun toString() : String {
        return "{coordinate=$coordinate, value=$value, travelledDirection=$travelledDirection, travelledDistance=$travelledDistance}"
    }

    fun print() {
        println(this.toString())
    }
}