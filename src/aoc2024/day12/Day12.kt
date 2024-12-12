package aoc2024.day12

import common.*

fun main() {
    val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

    class ZoneInfo(val area : Int, val perimeter: Int, val totalFences: Int)

    fun removeAdjacentFences(fences: HashSet<Triple<Int, Int, Direction>>, fence: Triple<Int, Int, Direction>, direction: Direction) {
        val (dx, dy) = when (direction) {
            Direction.UP -> Pair(0, -1)
            Direction.DOWN -> Pair(0, 1)
            Direction.LEFT -> Pair(-1, 0)
            Direction.RIGHT -> Pair(1, 0)
            else -> throw IllegalArgumentException()
        }

        var newX = fence.first + dx
        var newY = fence.second + dy

        while (true) {
            val tripleToRemove = Triple(newX, newY, fence.third)
            if (!fences.contains(tripleToRemove)) {
                break
            }
            fences.remove(tripleToRemove)
            newX += dx
            newY += dy
        }
    }

    fun getUniqueFencesCount(fences: HashSet<Triple<Int, Int, Direction>>): Int {
        var sideCoveredByFences = 0
        while (fences.isNotEmpty()) {
            val fence = fences.first()
            fences.remove(fence)
            if (fence.third == Direction.UP || fence.third == Direction.DOWN) {
                removeAdjacentFences(fences, fence, Direction.LEFT)
                removeAdjacentFences(fences, fence, Direction.RIGHT)
            } else {
                removeAdjacentFences(fences, fence, Direction.UP)
                removeAdjacentFences(fences, fence, Direction.DOWN)
            }
            sideCoveredByFences++
        }
        return sideCoveredByFences
    }

    fun calculateZoneInfo(grid: CharGrid2D, startingPoint: Pair<Int, Int>, coordinates: MutableList<Pair<Int, Int>>): ZoneInfo {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        val fences: HashSet<Triple<Int, Int, Direction>> = hashSetOf()

        val idZone = grid.getElementAt(startingPoint)
        queue.add(startingPoint)
        visited.add(startingPoint)

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()
            for (direction in allowedDirections) {
                grid.setPosition(x,y)
                grid.move(direction, true)
                val (nx, ny) = grid.getCurrentPosition()
                if (grid.canMove(x to y,direction).not()) {
                    fences.add(Triple(nx, ny, direction))
                    continue
                }
                if (visited.contains(nx to ny)) continue
                when (grid.getElementAt(nx to ny)) {
                    idZone -> {
                        coordinates.remove(nx to ny)
                        queue.add(nx to ny)
                        visited.add(nx to ny)
                    }
                    else -> fences.add(Triple(nx, ny, direction))
                }
            }
        }
        return ZoneInfo(visited.size, fences.size, getUniqueFencesCount(fences))
    }

    fun part1(input: List<String>): Int {
        val grid = CharGrid2D(input)
        val cellsPerIdentifier: MutableMap<Char, MutableList<Pair<Int,Int>>> = mutableMapOf()
        val zones: MutableList<ZoneInfo> = mutableListOf()

        grid.traverse(0,0, grid.width-1, grid.height-1) { x, y, c ->
            cellsPerIdentifier.getOrPut(c){ mutableListOf() }.add(x to y)
        }

        cellsPerIdentifier.forEach { (_, coordinates) ->
            while (coordinates.isNotEmpty()) {
                val (x, y) = coordinates.removeAt(0)
                zones.add(calculateZoneInfo(grid, x to y, coordinates))
            }
        }
        return zones.sumOf {
            it.area * it.perimeter
        }
    }

    fun part2(input: List<String>): Int {
        val grid = CharGrid2D(input)
        val cellsPerIdentifier: MutableMap<Char, MutableList<Pair<Int,Int>>> = mutableMapOf()
        val zones: MutableList<ZoneInfo> = mutableListOf()

        grid.traverse(0,0, grid.width-1, grid.height-1) { x, y, c ->
            cellsPerIdentifier.getOrPut(c){ mutableListOf() }.add(x to y)
        }

        cellsPerIdentifier.forEach { (_, coordinates) ->
            while (coordinates.isNotEmpty()) {
                val (x, y) = coordinates.removeAt(0)
                zones.add(calculateZoneInfo(grid, x to y, coordinates))
            }
        }

        return zones.sumOf {
            it.area * it.totalFences
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day12_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 1930)

    val testInput2 = readInput("aoc2024/Day12_test")
    val part2Result = part2(testInput2)
    part2Result.println()
    check(part2Result == 1206)

    val input = readInput("aoc2024/Day12")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}