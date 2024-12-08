package aoc2024.day08

import common.*

fun main() {
    fun getAntinode(firstValue: Pair<Int, Int>, secondValue: Pair<Int, Int>) =
        2 * firstValue.first - secondValue.first to 2 * firstValue.second - secondValue.second

    fun parseAntennas(input: List<String>): Map<Char, List<Pair<Int, Int>>> {
        return CharGrid2D(input) { it }
            .findAllItems { it != '.' }
            .groupBy { it.value }
            .filter { it.value.size > 1 }
            .mapValues { entry -> entry.value.map { it.x to it.y } }
    }

    fun calculateAntinodes(
        groupedAntennas: Map<Char, List<Pair<Int, Int>>>,
        boundaries: Pair<Int, Int>,
        recursive: Boolean
    ): Int {
        val (maxX, maxY) = boundaries
        val antinodes: MutableSet<Pair<Int, Int>> = mutableSetOf()

        groupedAntennas.forEach{
            it.value.forEach { firstValue ->
                it.value.filter { it != firstValue }.forEach { secondValue ->
                    if (recursive) {
                        var checkPoint = firstValue
                        var prevPoint = secondValue
                        while (checkPoint.first in 0 until maxX && checkPoint.second in 0 until maxY) {
                            antinodes.add(checkPoint)
                            val nextPoint = getAntinode(checkPoint, prevPoint)
                            prevPoint = checkPoint
                            checkPoint = nextPoint
                        }
                    } else {
                        val resonancePoint = getAntinode(firstValue, secondValue)
                        if (resonancePoint.first in 0 until maxX && resonancePoint.second in 0 until maxY) {
                            antinodes.add(resonancePoint)
                        }
                    }
                }
            }
        }

        return antinodes.size
    }

    fun part1(groupedAntennas: Map<Char, List<Pair<Int, Int>>>, boundaries: Pair<Int, Int>): Int {
        return calculateAntinodes(groupedAntennas, boundaries, recursive = false)
    }

    fun part2(groupedAntennas: Map<Char, List<Pair<Int, Int>>>, boundaries: Pair<Int, Int>): Int {
        return calculateAntinodes(groupedAntennas, boundaries, recursive = true)
    }

    fun executeTests() {
        val testInput = readInput("aoc2024/Day08_test")
        val testBoundaries = testInput[0].length to testInput.size
        val testAntennas = parseAntennas(testInput)

        val part1Result = part1(testAntennas, testBoundaries)
        part1Result.println()
        check(part1Result == 14)

        val part2Result = part2(testAntennas, testBoundaries)
        part2Result.println()
        check(part2Result == 34)
    }

    fun execute() {
        val input = readInput("aoc2024/Day08")
        val inputBoundaries = input[0].length to input.size
        val antennas = parseAntennas(input)

        benchmarkTime("part1") {
            part1(antennas, inputBoundaries)
        }

        benchmarkTime("part2") {
            part2(antennas, inputBoundaries)
        }
    }

    executeTests()
    execute()

}
