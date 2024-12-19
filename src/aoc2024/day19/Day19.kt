package aoc2024.day19

import common.benchmarkTime
import common.println
import common.readInput

fun main() {

    fun canConstructString(components: List<String>, text: String): Boolean {
        val memo = mutableMapOf<Int, Boolean>()

        fun dfs(startIndex: Int): Boolean {
            if (startIndex == text.length) return true
            if (startIndex in memo) return memo[startIndex]!!

            for (component in components) {
                if (text.startsWith(component, startIndex)) {
                    if (dfs(startIndex + component.length)) {
                        memo[startIndex] = true
                        return true
                    }
                }
            }

            memo[startIndex] = false
            return false
        }

        return dfs(0)
    }

    fun countConstructWays(components: List<String>, text: String): Long {
        val memo = mutableMapOf<Int, Long>()

        fun dfs(startIndex: Int): Long {
            if (startIndex == text.length) return 1
            if (startIndex in memo) return memo[startIndex]!!

            var ways = 0L
            for (component in components) {
                if (text.startsWith(component, startIndex)) {
                    ways += dfs(startIndex + component.length)
                }
            }

            memo[startIndex] = ways
            return ways
        }

        return dfs(0)
    }

    fun part1(input: List<String>): Int {
        val components = input.first().split(", ").map { it.trim() }
        val strings = input.drop(2).map { it.trim() }
        return strings.count{ canConstructString(components, it) }
    }

    fun part2(input: List<String>): Long {
        val components = input.first().split(", ").map { it.trim() }
        val strings = input.drop(2).map { it.trim() }
        return strings.sumOf{ countConstructWays(components, it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day19_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 6)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 16L)

    val input = readInput("aoc2024/Day19")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}