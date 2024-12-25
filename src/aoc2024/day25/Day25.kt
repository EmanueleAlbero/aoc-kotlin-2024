package aoc2024.day25

import common.benchmarkTime
import common.println
import common.readInputAsString

fun main() {

    fun part1(input: String): Int {
        val keys = mutableListOf<Long>()
        val doors = mutableListOf<Long>()

        val parts = input.split("\r\n\r\n")
        for (part in parts) {
            val lines = part.split("\r\n")
            var value = 0L

            for (line in lines) {
                for (c in line) {
                    value = (value shl 1) or when (c) {
                        '.' -> 0L
                        '#' -> 1L
                        else -> throw IllegalArgumentException()
                    }
                }
            }

            if ((value and 1L) == 1L) {
                keys.add(value)
            } else {
                doors.add(value)
            }
        }

        val doorsArray = doors.toLongArray()
        val keysArray = keys.toLongArray()

        return keysArray.sumOf { key ->
            doorsArray.count { door -> door and key == 0L }
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