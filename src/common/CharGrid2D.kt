package common

class CharGrid2D(input: List<String>, mapper: (Char) -> Char = { it }): Grid2D<Char>(
    input, mapper
) {
    fun getStringChunk(x: Int, y: Int, size: Int, direction: Direction): String {
        val result = super.getChunk(x, y, size, direction)
        return result.joinToString("")
    }

    fun getStringChunk(size: Int, direction: Direction) : String {
        val result = super.getChunk(size, direction)
        return result.joinToString("")
    }
}