package aoc2024.day02

import common.benchmarkTime
import common.println
import common.readInput
import kotlin.math.abs

fun main() {

    fun isSafeAscending(it: List<Int>) = it.zipWithNext().all { (a, b) -> (a < b && abs(a - b) in 1..3) }

    fun isSafeDescending(it: List<Int>) = it.zipWithNext().all { (a, b) -> (a > b && abs(a - b) in 1..3) }

    fun isSafe(it: List<Int>) = isSafeAscending(it) || isSafeDescending(it)

    fun part1(scores: List<List<Int>>) = scores.count { isSafe(it) }

    fun part2(scores: List<List<Int>>)  =
        scores.count { scoreList ->
            scoreList.indices.any { index ->
                    isSafe(
                        scoreList.withIndex()
                        .filter { it.index != index }
                        .map { it.value }
                    )
                }
            }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day02_test")
    val testScores = testInput.map { it ->
        it.split(" ")
            .map(String::toInt)
    }

    val part1Result = part1(testScores)
    part1Result.println()
    check(part1Result == 2)

    val part2Result = part2(testScores)
    part2Result.println()
    check(part2Result == 4)

    val input = readInput("aoc2024/Day02")
    val scores = input.map { it ->
        it.split(" ")
            .map(String::toInt)
    }

    benchmarkTime("part1") {
        part1(scores)
    }

    benchmarkTime("part2") {
        part2(scores)
    }
}