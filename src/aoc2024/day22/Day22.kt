package aoc2024.day22

import common.benchmarkTime
import common.println
import common.readInput

fun main() {

    fun getNextSecret(secretNumber: Long): Long {
        var newSecret = secretNumber
        newSecret = newSecret.shl(6).xor(newSecret).and(16777215)
        newSecret = newSecret.shr(5).xor(newSecret).and(16777215)
        newSecret = newSecret.shl(11).xor(newSecret).and(16777215)
        return newSecret
    }

    fun transform(initialSecret: Long, count: Int = 2000): List<Long> {
        val secrets = mutableListOf<Long>()
        var current = initialSecret
        repeat(count) {
            current = getNextSecret(current)
            secrets.add(current)
        }
        return secrets
    }

    fun part1(input: List<String>): Long {
        return input.sumOf{ transform(it.toLong(), 2000).last() }
    }

    fun part2(input: List<String>): Int {
        val pricesMap = mutableMapOf<List<Int>, Int>().withDefault { 0 }
        val buyers = input.map { it.toLong() }
        buyers.forEach { buyer ->
            val secretNumbers = transform(buyer, 2000)
            val bananaPrices = secretNumbers.map { (it % 10).toInt() }
            val priceChanges = bananaPrices.zipWithNext { a, b -> b - a }

            val visited = mutableSetOf<List<Int>>()

            priceChanges.windowed(size = 4).forEachIndexed { i, windowOf4 ->
                if (visited.add(windowOf4)) {
                    val priceToAdd = bananaPrices[i + 4]
                    pricesMap[windowOf4] = pricesMap.getValue(windowOf4) + priceToAdd
                }
            }
        }
        return pricesMap.values.maxOrNull() ?: 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day22_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 37327623L)

    val testInput2 = readInput("aoc2024/Day22_test2")
    val part2Result = part2(testInput2)
    part2Result.println()
    check(part2Result == 23)

    val input = readInput("aoc2024/Day22")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}