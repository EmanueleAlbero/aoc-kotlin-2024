package aoc2024.day06

import common.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun main() {

    val visitedPosition = mutableSetOf<Pair<Int, Int>>()

    fun part1(input: List<String>): Int {
        visitedPosition.clear()
        val grid = CharGrid2D(input){ it -> it}
        val startPosition = grid.find('^') ?: return 0
        var direction = Direction.UP

        grid.setPosition(startPosition.first, startPosition.second)
        visitedPosition.add(startPosition)
        while (grid.canMove(direction)){
            var nextPosition = grid.getNeighbors(listOf(direction))[0]
            while (nextPosition.value == '#'){
                direction = Direction.rotateRight(direction)
                nextPosition = grid.getNeighbors(listOf(direction))[0]
            }
            visitedPosition.add(nextPosition.x to nextPosition.y)
            grid.move(direction)
        }
        return visitedPosition.size
    }

    fun part2(input: List<String>, visitedPosition: Set<Pair<Int, Int>>): Int {
        val grid = input.map { it.toCharArray() }.toTypedArray()
        val width = grid[0].size
        val height = grid.size
        var startPosition: Pair<Int, Int> = Pair(0, 0)

        val rows = mutableMapOf<Int, TreeSet<Int>>()
        val cols = mutableMapOf<Int, TreeSet<Int>>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (grid[y][x] == '#') {
                    rows.computeIfAbsent(y) { TreeSet() }.add(x)
                    cols.computeIfAbsent(x) { TreeSet() }.add(y)
                }
                if (grid[y][x] == '^') {
                    startPosition = Pair(x, y)
                }
            }
        }

        fun nextMovement(x: Int, y: Int, direction: Direction): Triple<Int, Int, Direction>? {
            return when (direction) {
                Direction.UP -> {
                    val columnObs = cols[x] ?: return null
                    val nextStep = columnObs.lower(y) ?: return null
                    Triple(x, nextStep + 1, Direction.RIGHT)
                }

                Direction.DOWN -> {
                    val columnObs = cols[x] ?: return null
                    val nextStep = columnObs.higher(y) ?: return null
                    Triple(x, nextStep - 1, Direction.LEFT)
                }

                Direction.LEFT -> {
                    val rowObs = rows[y] ?: return null
                    val nextStep = rowObs.lower(x) ?: return null
                    Triple(nextStep + 1, y, Direction.UP)
                }

                Direction.RIGHT -> {
                    val rowObs = rows[y] ?: return null
                    val nextStep = rowObs.higher(x) ?: return null
                    Triple(nextStep - 1, y, Direction.DOWN)
                }

                else -> return null
            }
        }

        var loops = 0
        visitedPosition
            .filterNot { it == startPosition }
            .forEach { (ox, oy) ->
                rows.computeIfAbsent(oy) { TreeSet() }
                cols.computeIfAbsent(ox) { TreeSet() }

                rows[oy]!!.add(ox)
                cols[ox]!!.add(oy)

                val storedMovements = mutableSetOf<Triple<Int, Int, Direction>>()
                var nextMovementState: Triple<Int, Int, Direction>? = Triple(startPosition.first, startPosition.second, Direction.UP)

                var loopFound = false
                while (true) {
                    if (nextMovementState == null) break
                    if (nextMovementState in storedMovements) {
                        loopFound = true
                        break
                    }
                    storedMovements.add(nextMovementState)
                    nextMovementState = nextMovement(nextMovementState.first, nextMovementState.second, nextMovementState.third)
                }

                if (loopFound) {
                    loops++
                }

                rows[oy]!!.remove(ox)
                cols[ox]!!.remove(oy)
            }
        return loops
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day06_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 41)

    val part2Result = part2(testInput, visitedPosition)
    part2Result.println()
    check(part2Result == 6)

    val input = readInput("aoc2024/Day06")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input, visitedPosition)
    }
}