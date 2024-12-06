package common

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    companion object {
        fun from(value: String) = when (value) {
            "U", "T" -> UP
            "D", "B" -> DOWN
            "L" -> LEFT
            "R" -> RIGHT
            "U_L" -> UP_LEFT
            "U_R" -> UP_RIGHT
            "B_L" -> DOWN_LEFT
            "B_R" -> DOWN_RIGHT
            else -> throw IllegalArgumentException()
        }

        fun rotateRight(currentDirection: Direction): Direction {
            return when (currentDirection) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP_LEFT -> UP_RIGHT
                UP_RIGHT -> DOWN_RIGHT
                DOWN_LEFT -> DOWN_LEFT
                DOWN_RIGHT -> UP_LEFT
            }
        }

        fun Inverted(direction: Direction?): Direction? {
            return when (direction) {
                UP -> DOWN
                DOWN -> UP
                LEFT -> RIGHT
                RIGHT -> LEFT
                UP_LEFT -> DOWN_RIGHT
                UP_RIGHT -> DOWN_LEFT
                DOWN_LEFT -> UP_RIGHT
                DOWN_RIGHT -> UP_LEFT
                else -> null
            }
        }
    }
}