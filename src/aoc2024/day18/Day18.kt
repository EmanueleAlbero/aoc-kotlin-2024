package aoc2024.day18

import common.*

fun main() {

    data class Point(val x: Int, val y: Int)
    data class Step(val point: Point, val steps: Set<Point>)

    fun escapeMemory(
        startingPoint: Point,
        endPosition: Point,
        allowedDirections: List<Direction>,
        grid: CharGrid2D,
        fallingPointsMap: Map<Point, Int>,
        maxCorruptedBytes: Int,
    ): Set<Point> {
        val queue = ArrayDeque<Step>()
        val visited: MutableSet<Point> = mutableSetOf()

        queue.add(Step(startingPoint, setOf()))
        visited.add(startingPoint)
        while (queue.isNotEmpty()) {
            val step = queue.removeFirst()
            if (step.point == endPosition) {
                return step.steps
            }
            for (direction in allowedDirections) {
                if (grid.canMove(step.point.x to step.point.y, direction).not()) {
                    continue
                }

                grid.setPosition(step.point.x, step.point.y)
                grid.move(direction, true)
                val newPoint = grid.getCurrentPosition().let { Point(it.first, it.second) }
                if (visited.contains(newPoint)) {
                    continue
                }
                val corrupted = fallingPointsMap[newPoint]
                if (corrupted != null && corrupted < maxCorruptedBytes) {
                    continue
                }

                visited.add(newPoint)
                queue.add(Step(newPoint, (step.steps+newPoint)))
            }
        }
        return setOf()
    }

    fun part1(input: List<String>, gridSize: Int, maxCorruptedBytes: Int): Int {
        val fallingPointsMap = input.mapIndexed { index, line ->
            val coords = line.split(",").map(String::toInt)
            val point = Point(coords[0], coords[1])
            point to index
        }.toMap()
        val map = mutableListOf<String>()
        for(j in 0 until gridSize) {
            map.add(".".repeat(gridSize))
        }
        val grid = CharGrid2D(map)
        val startingPoint = Point(0, 0)
        val endPosition = Point(gridSize - 1, gridSize - 1)
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

        return escapeMemory(startingPoint, endPosition, allowedDirections, grid, fallingPointsMap, maxCorruptedBytes).size
    }

    fun part2(input: List<String>, gridSize: Int): String {
        val fallingPointsMap = input.mapIndexed { index, line ->
            val coords = line.split(",").map(String::toInt)
            val point = Point(coords[0], coords[1])
            point to index
        }.toMap()
        val map = mutableListOf<String>()
        for(j in 0 until gridSize) {
            map.add(".".repeat(gridSize))
        }
        val grid = CharGrid2D(map)
        val startingPoint = Point(0, 0)
        val endPosition = Point(gridSize - 1, gridSize - 1)
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

        var escapeRoute = emptySet<Point>()
        for(point in fallingPointsMap) {
            if (escapeRoute.isNotEmpty() && !escapeRoute.contains(point.key)) {
                continue
            }
            escapeRoute = escapeMemory(
                    startingPoint,
                    endPosition,
                    allowedDirections,
                    grid,
                    fallingPointsMap,
                    point.value+1
                )
            if (escapeRoute.isNotEmpty()) continue
            return "${point.key.x},${point.key.y}"
        }
        return "-1,-1"
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day18_test")
    val part1Result = part1(testInput, 7, 12)
    part1Result.println()
    check(part1Result == 22)

    val part2Result = part2(testInput, 7)
    part2Result.println()
    check(part2Result == "6,1")

    val input = readInput("aoc2024/Day18")
    benchmarkTime("part1") {
        part1(input,71, 1024)
    }

    benchmarkTime("part2") {
        part2(input, 71)
    }
}