package aoc2024.day13

import common.benchmarkTime
import common.println
import common.readInputAsString
import kotlin.math.ceil

typealias Coordinates = Pair<Long, Long>

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
        val prize: Prize
    )

    fun extractMovements(line: String): Coordinates {
        val regex = "X\\+(\\d+), Y\\+(\\d+)".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Invalid movement format: $line")
        val (x, y) = match.destructured
        return x.toLong() to y.toLong()
    }

    fun extractCoordinates(line: String): Coordinates {
        val regex = "X=(\\d+), Y=(\\d+)".toRegex()
        val match = regex.find(line) ?: throw IllegalArgumentException("Invalid coordinates format: $line")
        val (x, y) = match.destructured
        return x.toLong() to y.toLong()
    }

    fun parseClawMachineData(input: String): List<ClawMachine> {
        return input
            .split("\r\n\r\n")
            .map {
                val (buttonA, buttonB, prize) = it.split("\r\n")
                ClawMachine(
                    Button(extractMovements(buttonA)),
                    Button(extractMovements(buttonB)),
                    Prize(extractCoordinates(prize)))
            }
    }

    fun matrixDeterminant(a: Coordinates, b: Coordinates): Long {
        return a.first * b.second - a.second * b.first
    }

    fun add(a: Coordinates, offset: Long) = Coordinates(a.first + offset, a.second + offset)

    fun solve(a: Coordinates, b: Coordinates, c: Coordinates): Long {
        //use cramer method to found solution to
        // a*xA + b*xB = c
        // a*yA + b*yB = d

        val det = matrixDeterminant(a, b)
        if (det == 0L) return 0L

        val ta = matrixDeterminant(c, b).toDouble() / det
        val tb = matrixDeterminant(a, c).toDouble() / det

        if (ceil(ta) == ta && ceil(tb) == tb) {
            return ta.toLong()*3+tb.toLong()
        }
        return 0L
    }

    fun part1(input: String): Long {
        return parseClawMachineData(input)
            .sumOf {
                solve(
                    it.buttonA.movement,
                    it.buttonB.movement,
                    it.prize.position
                )
            }
    }

    fun part2(input: String): Long {
        return parseClawMachineData(input)
            .sumOf {
                solve(
                    it.buttonA.movement,
                    it.buttonB.movement,
                    add(it.prize.position, 10000000000000L)
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