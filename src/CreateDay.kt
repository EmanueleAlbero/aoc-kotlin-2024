import java.io.File

fun main() {
    val yearString = "2024"
    val dayString = "01"

    val baseDir = File(".")
    val resourcesDir = File(baseDir, "resources/aoc$yearString")
    val srcDir = File(baseDir, "src/aoc$yearString")

    val dayDir = File(srcDir, "day$dayString")
    if (!dayDir.exists()) {
        dayDir.mkdirs()
    }

    val kotlinFile = File(dayDir, "Day$dayString.kt")
    if (!kotlinFile.exists()) {
        kotlinFile.writeText(
            """
            package aoc$yearString.day$dayString
            
            import common.benchmarkTime
            import common.println
            import common.readInput
            
            fun main() {
            
                fun part1(input: List<String>): Int {
                    return 0
                }
            
                fun part2(input: List<String>): Int {
                    return 0
                }
            
                // test if implementation meets criteria from the description, like:
                val testInput = readInput("aoc$yearString/Day${dayString}_test")
                val part1Result = part1(testInput)
                part1Result.println()
                check(part1Result == 0)
            
                val part2Result = part2(testInput)
                part2Result.println()
                check(part2Result == 0)
            
                val input = readInput("aoc$yearString/Day${dayString}")
                benchmarkTime("part1") {
                    part1(input)
                }
            
                benchmarkTime("part2") {
                    part2(input)
                }
            }
            """.trimIndent()
        )
    }

    if (!resourcesDir.exists()) {
        resourcesDir.mkdirs()
    }

    val inputFile = File(resourcesDir, "Day$dayString.txt")
    if (!inputFile.exists()) {
        inputFile.createNewFile()
    }

    val testInputFile = File(resourcesDir, "Day${dayString}_test.txt")
    if (!testInputFile.exists()) {
        testInputFile.createNewFile()
    }

    println("Setup completed $dayString!")
}
