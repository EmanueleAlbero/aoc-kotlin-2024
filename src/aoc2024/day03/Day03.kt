package aoc2024.day03

import common.benchmarkTime
import common.println
import common.readInput
import common.readInputAsString

fun main() {

    val MUL_REGEX = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
    val INSTRUCTION_REGEX_PART1 = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
    val INSTRUCTION_REGEX_PART2 = """don't\(\)|do\(\)|mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

    fun executeMul(input: String) =
        MUL_REGEX
            .find(input)
            ?.destructured
            ?.let{ (x, y) ->
                x.toInt() * y.toInt()
            }
            ?: 0

    fun getValidInstructions(input: String, regex: Regex = INSTRUCTION_REGEX_PART1) = 
        regex.findAll(input).map { it.value }.toList()
    
    fun part1(input: String) =
        getValidInstructions(input, INSTRUCTION_REGEX_PART1)
            .let {
                it.sumOf { instruction -> executeMul(instruction) }
            }

    fun part2(input: String) =
        getValidInstructions(input, INSTRUCTION_REGEX_PART2)
            .fold(Pair(true, 0)){ (enabled, sum), instruction ->
                when {
                    instruction.contains("don't()") -> Pair(false, sum)
                    instruction.contains("do()") -> Pair(true, sum)
                    enabled -> Pair(true, sum + executeMul(instruction))
                    else -> Pair(false, sum)
                }
            }.second

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day03_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 161)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 48)

    val input = readInputAsString("aoc2024/Day03")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}
