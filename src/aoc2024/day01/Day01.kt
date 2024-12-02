package aoc2024.day01

import common.benchmarkTime
import common.println
import common.readInput
import kotlin.math.abs

fun main() {

    fun part1(input: DataResult): Int = input.leftList.zip(input.rightList).sumOf { abs(it.first - it.second) }

    fun part2(input: DataResult) = input.leftList.sumOf { it * (input.frequencies[it] ?: 0) }

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
    val (leftList, rightList) = input.map{
        val first = it.substringBefore(" ").toInt()
        val second = it.substringAfterLast(" ").toInt()
        first to second
    }.unzip()
    val frequencies = rightList.groupingBy { it }.eachCount()

    return DataResult(leftList.sorted(), rightList.sorted(), frequencies)
}

data class DataResult(
    val leftList: List<Int>,
    val rightList: List<Int>,
    val frequencies: Map<Int, Int>
)