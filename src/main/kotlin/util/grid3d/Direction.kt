package util.grid3d


enum class Direction (val xDelta:Int, val yDelta:Int, val zDelta:Int){
    X_UP(1, 0, 0),
    X_DOWN(-1, 0, 0),
    Y_UP(0, 1, 0),
    Y_DOWN(0, -1, 0),
    Z_UP (0, 0, 1),
    Z_DOWN (0, 0, -1)
}