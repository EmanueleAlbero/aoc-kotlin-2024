package aoc2024.day11

import common.*
import java.util.UUID

fun main() {

    val memoResults: MutableMap<Pair<Int, String>, Long> = mutableMapOf()

    fun getStoneCount(currentIteration: Int, gaolIteration: Int, stone: String): Long {
        memoResults[currentIteration to stone]?.let {
            return it
        }
        if (currentIteration == gaolIteration) {
            return 1L
        }

        memoResults[currentIteration to stone] = when {
            stone == "0" -> {
                val value = getStoneCount(currentIteration + 1, gaolIteration, "1")
                value
            }
            stone.length % 2 == 0 -> {
                val left = stone.substring(0, stone.length / 2).toLong().toString()
                val right = stone.substring(stone.length / 2).toLong().toString()
                val value = getStoneCount(currentIteration + 1, gaolIteration, left) +
                        getStoneCount(currentIteration + 1, gaolIteration, right)
                value
            }
            else -> {
                val value = getStoneCount(currentIteration + 1, gaolIteration, (stone.toLong() * 2024).toString())
                value
            }
        }
        return memoResults[currentIteration to stone]!!
    }

    fun part1(input: List<String>):Long {
        memoResults.clear()
        return input.sumOf { getStoneCount(0, 25, it) }
    }

    fun part2(input: List<String>):Long {
        memoResults.clear()
        return input.sumOf { getStoneCount(0, 75, it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day11_test").split(" ")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 55312L)

    //val part2Result = part2(testInput)
    //part2Result.println()
    //check(part2Result == 0)

    val input = readInputAsString("aoc2024/Day11").split(" ")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}