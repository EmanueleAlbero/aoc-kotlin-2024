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
        val loopsMoves: MutableSet<Item<Char>> = mutableSetOf()
        var loops: AtomicInteger = AtomicInteger(0)
        val bounceIdentified = '@'

        visitedPosition
            .filterNot { it == startPosition }
            .parallelStream()
            .forEach { (x, y) ->
                val localGrid = CharGrid2D(input){ it -> it}
                localGrid.setValue(x, y, '#')
                localGrid.setPosition(x,y)
                val obstacleInteractionMoves =
                    localGrid
                        .getNeighbors(listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT))
                        .filter { it.value != '#'}
                        .flatMap { value ->
                            listOf(
                                Item<Char>(value.x, value.y, Direction.Inverted(value.direction), value.value),
                                Item<Char>(value.x, value.y, Direction.Inverted(value.direction), bounceIdentified),
                            ) }

                val moves: MutableList<Item<Char>> = mutableListOf()

                localGrid.setPosition(startPosition.first, startPosition.second)
                var direction = Direction.UP
                var loopFound = false
                while (localGrid.canMove(direction)) {
                    var nextPosition = localGrid.getNeighbors(listOf(direction))[0]
                    while (nextPosition.value == '#') {
                        direction = Direction.rotateRight(direction)
                        moves.add(Item(
                            localGrid.getCurrentPosition().first,
                            localGrid.getCurrentPosition().second,
                            direction,
                            bounceIdentified,
                        ))
                        nextPosition = localGrid.getNeighbors(listOf(direction))[0]
                    }

                    if (nextPosition in loopsMoves) {
                        loopFound = true
                        break
                    }

                    val previousOccurrence = moves.indexOf(nextPosition)
                    if ( previousOccurrence >= 0) {
                        /*if (obstacleInteractionMoves.none{ moves.indexOf(it) >= previousOccurrence }) {
                            loopsMoves.addAll(moves.subList(previousOccurrence, moves.size-1))
                        }*/
                        loopFound = true
                        break
                    }
                    moves.add(nextPosition)
                    localGrid.move(direction)
                }
                if (loopFound) {
                    loops.getAndIncrement()
                }
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