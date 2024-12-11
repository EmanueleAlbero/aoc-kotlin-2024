package aoc2024.day10

import common.*

fun main() {

    fun part1(input: List<String>): Int {
        val grid = NumberGrid2D(input)
        val startingPositions = grid.findAll { it == 0 }.toList()
        val result = startingPositions.sumOf {
            grid.findAllPathTo(
                it,
                goalCriteria = { item -> item == 9 },
                moveValidator = { c1, c2 -> c2 == c1 + 1
                }
            ).map { path ->
                path.last()
            }.toSet().size
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val grid = NumberGrid2D(input)
        val startingPositions = grid.findAll { it == 0 }.toList()
        val result = startingPositions.sumOf {
            grid.findAllPathTo(
                it,
                goalCriteria = { item -> item == 9 },
                moveValidator = { c1, c2 -> c2 == c1 + 1
                }
            ).size
        }
        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day10_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 36)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 81)

    val input = readInput("aoc2024/Day10")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}