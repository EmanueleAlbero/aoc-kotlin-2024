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
    }
}