package aoc2024.day07

import common.benchmarkTime
import common.println
import common.readInput

fun main() {

    fun findOperations(numbers: List<Long>, target: Long, operationsAllowed: List<String>): String? {
        data class State(val index: Int, val currentValue: Long, val operations: String)

        val queue = ArrayDeque<State>()
        queue.add(State(0, numbers[0], numbers[0].toString()))

        while (queue.isNotEmpty()) {
            val (index, currentValue, operations) = queue.removeFirst()
            if (index == numbers.size - 1) {
                if (currentValue == target) {
                    return operations
                }
                continue
            }
            if (currentValue > target) continue
            val nextIndex = index + 1

            if (operationsAllowed.contains("+")){
                queue.add(
                    State(
                        nextIndex,
                        currentValue + numbers[nextIndex],
                        "$operations + ${numbers[nextIndex]}"
                    )
                )
            }

            if (operationsAllowed.contains("*")) {
                queue.add(
                    State(
                        nextIndex,
                        currentValue * numbers[nextIndex],
                        "$operations * ${numbers[nextIndex]}"
                    )
                )
            }

            if (operationsAllowed.contains("||")) {
                queue.add(
                    State(
                        nextIndex,
                        (currentValue.toString() + numbers[nextIndex].toString()).toLong(),
                        "$operations || ${numbers[nextIndex]}"
                    )
                )
            }
        }
        return null
    }


    fun part1(input: List<String>): Long {
        val numbers: List<Pair<Long, List<Long>>> = input.map { line ->
            val (result, numbers) = line.split(": ")
            result.toLong() to numbers.split(" ").map { it.toLong() }
        }

        return numbers.sumOf { (result, numbers) ->
            val operations = findOperations(numbers, result, listOf("+", "*"))
            when (operations) {
                 null -> 0
                 else -> result
            }
        }
    }

    fun part2(input: List<String>): Long {
        val numbers: List<Pair<Long, List<Long>>> = input.map { line ->
            val (result, numbers) = line.split(": ")
            result.toLong() to numbers.split(" ").map { it.toLong() }
        }

        return numbers.sumOf { (result, numbers) ->
            val operations = findOperations(numbers, result, listOf("+", "*", "||"))
            when (operations) {
                null -> 0
                else -> result
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day07_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 3749L)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 11387L)

    val input = readInput("aoc2024/Day07")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}