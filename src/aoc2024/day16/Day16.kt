package aoc2024.day16

import common.*
import java.util.*

fun main() {
    data class Point(val x: Int, val y: Int)
    data class PathsOutcome(val numberOfCellsInBestPaths: Int, val minimumCost: Int)

    val allowedDirections = listOf(
        Direction.UP,
        Direction.DOWN,
        Direction.LEFT,
        Direction.RIGHT
    )

    fun findAllBestPaths(
        grid: CharGrid2D,
        startPosition: Pair<Int, Int>,
        endPosition: Pair<Int, Int>
    ): PathsOutcome {
        val priorityQueue = PriorityQueue<Triple<List<Point>, Direction, Int>>(compareBy { it.third })
        val targetPoint = Point(endPosition.first, endPosition.second)
        val visitedNodes = HashMap<Pair<Point, Direction>, Int>()
        val bestPathsNodes = HashSet<Point>()

        priorityQueue.add(
            Triple(
                listOf(Point(startPosition.first, startPosition.second)),
                Direction.RIGHT,
                0
            )
        )

        var minimumCost = Int.MAX_VALUE

        while (priorityQueue.isNotEmpty()) {
            val (currentPath, currentDirection, currentCost) = priorityQueue.poll()
            val currentPoint = currentPath.last()

            if (currentPoint == targetPoint) {
                if (currentCost <= minimumCost) {
                    minimumCost = currentCost
                } else {
                    break
                }
                bestPathsNodes.addAll(currentPath)
            }

            if (visitedNodes[currentPoint to currentDirection] != null &&
                visitedNodes[currentPoint to currentDirection]!! < currentCost
            ) continue
            visitedNodes[currentPoint to currentDirection] = currentCost

            val neighbors = grid.getNeighbors(
                currentPoint.x to currentPoint.y,
                allowedDirections
            )

            for (neighbor in neighbors) {
                if (neighbor.direction == null) continue
                if (neighbor.direction == Direction.Inverted(currentDirection)) continue
                if (neighbor.value == '#') continue

                priorityQueue.add(
                    Triple(
                        currentPath + Point(neighbor.x, neighbor.y),
                        neighbor.direction,
                        currentCost + when (neighbor.direction) {
                            currentDirection -> 1
                            else -> 1001
                        }
                    )
                )
            }
        }
        return PathsOutcome(bestPathsNodes.size, minimumCost)
    }

    fun part1(input: List<String>): Int {
        val grid = CharGrid2D(input){ it -> it}
        val startElement = grid.find('S')
        val goalElement = grid.find('E')
        return findAllBestPaths(
            grid,
            startElement!!,
            goalElement!!).minimumCost

    }

    fun part2(input: List<String>): Int {
        val grid = CharGrid2D(input){ it -> it}
        val startElement = grid.find('S')
        val goalElement = grid.find('E')

        return findAllBestPaths(
            grid,
            startElement!!,
            goalElement!!).numberOfCellsInBestPaths
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day16_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 7036)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 45)

    val input = readInput("aoc2024/Day16")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}