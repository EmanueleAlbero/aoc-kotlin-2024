package aoc2024.day13

import common.benchmarkTime
import common.println
import common.readInputAsString
import kotlin.math.ceil

typealias Coordinates = Pair<Double, Double>

fun main() {

    data class Button(
        val movement: Coordinates
    )

    data class Prize(
        val position: Coordinates
    )

    data class ClawMachine(
        val buttonA: Button,
        val buttonB: Button,
        val prize: Prize,
    )

    fun extractMovements2(line: String): Coordinates {
        val regex = "X\\+(\\d+), Y\\+(\\d+)".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Invalid movement format: $line")
        val (x, y) = match.destructured
        return x.toDouble() to y.toDouble()
    }

    fun extractCoordinates2(line: String): Coordinates {
        val regex = "X=(\\d+), Y=(\\d+)".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Invalid coordinates format: $line")
        val (x, y) = match.destructured
        return x.toDouble() to y.toDouble()
    }

    fun parseClawMachineData2(input: String): List<ClawMachine> {
        return input
            .split("\r\n\r\n")
            .map {
                val (buttonA, buttonB, prize) = it.split("\r\n")
                ClawMachine(
                    Button(extractMovements2(buttonA)),
                    Button(extractMovements2(buttonB)),
                    Prize(extractCoordinates2(prize)))
            }
    }

    fun add2(a: Coordinates, offset: Long) = Coordinates(a.first + offset, a.second + offset)

    fun solve2(a: Coordinates, b: Coordinates, c: Coordinates): Long {
        val aMoves = (b.second*c.first - b.first*c.second) / (b.second*a.first - b.first*a.second)
        if (ceil(aMoves) != aMoves) return 0L
        val bMoves: Double = (c.first - a.first*aMoves) / b.first
        if (ceil(bMoves) != bMoves) return 0L
        return (aMoves*3+bMoves).toLong()
    }

    fun part1(input: String): Long {
        return parseClawMachineData2(input)
            .sumOf {
                solve2(
                    it.buttonA.movement,
                    it.buttonB.movement,
                    it.prize.position
                )
            }
    }

    fun part2(input: String): Long {
        return parseClawMachineData2(input)
            .sumOf {
                solve2(
                    it.buttonA.movement,
                    it.buttonB.movement,
                    add2(it.prize.position, 10000000000000L)
                )
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day13_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 480L)

    val part2Result = part2(testInput)
    part2Result.println()
    //check(part2Result == 0L)

    val input = readInputAsString("aoc2024/Day13")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}