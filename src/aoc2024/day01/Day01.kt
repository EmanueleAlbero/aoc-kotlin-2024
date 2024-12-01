package aoc2024.day01

import common.benchmarkTime
import common.println
import common.readInput
import kotlin.math.abs

fun main() {

    fun part1(input: DataResult): Int = input.leftList.zip(input.rightList).sumOf { abs(it.first - it.second) }

    fun part2(input: DataResult) = input.leftList.sumOf { it * (input.occurrences[it] ?: 0) }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day01_test")
    val testInputData = extractData(testInput)
    val part1Result = part1(testInputData)
    part1Result.println()
    check(part1Result == 11)

    val part2Result = part2(testInputData)
    part2Result.println()
    check(part2Result == 31)

    val input = readInput("aoc2024/Day01")
    val inputData = benchmarkTime("dataExtraction", printResult = false){
        extractData(input)
    }

    benchmarkTime("part1"){
        part1(inputData)
    }

    benchmarkTime("part2") {
        part2(inputData)
    }
}

fun extractData(input: List<String>): DataResult {
    val occurrences = mutableMapOf<Int, Int>()

    val (leftList, rightList) = input.map{
        val first = it.substringBefore(" ").toInt()
        val second = it.substringAfterLast(" ").toInt()
        occurrences[second] = (occurrences[second] ?: 0) + 1
        first to second
    }.unzip()

    return DataResult(leftList.sorted(), rightList.sorted(), occurrences)
}

data class DataResult(
    val leftList: List<Int>,
    val rightList: List<Int>,
    val occurrences: Map<Int, Int>
)