package aoc2024.day25

import common.benchmarkTime
import common.println
import common.readInputAsString

fun main() {

    fun part1(input: String): Int {
        val keys = mutableSetOf<Long>()
        val doors = mutableSetOf<Long>()

        input.split("\r\n\r\n").map {
            val value = it.split("\r\n").map {
                it.replace('.', '0').replace('#', '1')
            }.joinToString("").toLong(2)
            if ((value and 0b1) == 1L) {
                keys.add(value)
            } else {
                doors.add(value)
            }
        }
        return keys.sumOf { key ->
            doors.count { door -> door and key == 0L }
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day25_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 3)


    val input = readInputAsString("aoc2024/Day25")
    benchmarkTime("part1") {
        part1(input)
    }
}