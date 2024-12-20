package aoc2024.day20

import common.*
import common.Direction.*

const val WALL = '#'

fun main() {
    val allowedDirections = listOf(UP, DOWN, LEFT, RIGHT)

    fun race(input: List<String>, maxDistance: Int): Int {
        val grid = CharGrid2D(input){ it -> it}
        val startingPoint = grid.find('S')!!
        val endPoint = grid.find('E')!!
        grid.setValue(startingPoint.first, startingPoint.second, '.')
        grid.setValue(endPoint.first, endPoint.second, '.')

        val trackRecord = ArrayDeque<Pair<Int, Int>>()
        val cheatList = mutableMapOf<Pair<Pair<Int, Int>, Pair<Int, Int>>, Int>()

        var currentPoint = endPoint
        var previousDirection: Direction? = null
        var step = 0
        while (currentPoint != startingPoint){
            trackRecord.addFirst(currentPoint)
            grid
                .getNeighbors(currentPoint, allowedDirections).firstOrNull {
                    it.x in 0 until grid.width
                            && it.y in 0 until grid.height
                            && (it.direction) != Direction.Inverted(previousDirection)
                            && grid.getElementAt(it.x, it.y) != WALL
                }?.let { neighbor ->
                    previousDirection = neighbor.direction
                    currentPoint = neighbor.x to neighbor.y
                    step++
                } ?: break
        }
        trackRecord.addFirst(startingPoint)

        trackRecord.forEachIndexed { index, current ->
            for (j in index + (maxDistance-1) until trackRecord.size) {
                val other = trackRecord[j]
                val dx = current.first - other.first
                val dy = current.second - other.second
                val manhattanDistance = kotlin.math.abs(dx) + kotlin.math.abs(dy)
                if (manhattanDistance <= maxDistance) {
                    cheatList[other to current] = (j-index)-manhattanDistance
                }
            }
        }

        return cheatList.count(){it.value >= 100}
    }

    fun part1(input: List<String>): Int {
        return race(input, 2)
    }

    fun part2(input: List<String>): Int {
        return race(input,20)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day20_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 0)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 0)

    val input = readInput("aoc2024/Day20")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}