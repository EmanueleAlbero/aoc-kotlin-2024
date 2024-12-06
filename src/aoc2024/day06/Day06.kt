package aoc2024.day06

import common.*
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

    fun part2(input: List<String>): Int {
        val grid = CharGrid2D(input){ it -> it}
        val startPosition = grid.find('^') ?: return 0
        var loops: AtomicInteger = AtomicInteger(0)

        visitedPosition
            .filterNot { it == startPosition }
            .parallelStream()
            .forEach { (x, y) ->
                val localGrid = CharGrid2D(input){ it -> it}
                val moves: MutableSet<Item<Char>> = mutableSetOf()
                localGrid.setValue(x, y, '#')

                var direction = Direction.UP
                var isLoop: Boolean = false
                localGrid.setPosition(startPosition.first, startPosition.second)
                while (localGrid.canMove(direction)) {
                    var nextPosition = localGrid.getNeighbors(listOf(direction))[0]
                    while (nextPosition.value == '#') {
                        direction = Direction.rotateRight(direction)
                        nextPosition = localGrid.getNeighbors(listOf(direction))[0]
                    }

                    if ( moves.contains(nextPosition) ) {
                        isLoop = true
                        break
                    }

                    moves.add(nextPosition)
                    localGrid.move(direction)
                }
                if (isLoop) loops.incrementAndGet()
                localGrid.setValue(x, y, '.')
            }
            return loops.get()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day06_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 41)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 6)

    val input = readInput("aoc2024/Day06")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}