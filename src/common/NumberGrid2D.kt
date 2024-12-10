package common

class NumberGrid2D(input: List<String>, mapper: (Char) -> Int = { it -> if (it.isDigit()) it.digitToInt() else -99 }): Grid2D<Int>(
    input, mapper
) {

}