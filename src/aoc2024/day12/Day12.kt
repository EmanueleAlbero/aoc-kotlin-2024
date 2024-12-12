package aoc2024.day12

import common.*

fun main() {
    val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

    class ZoneInfo(val area : Int, val perimeter: Int, val totalFences: Int)

    class Garden(
        input: List<String>,
        val zones: MutableList<ZoneInfo> = mutableListOf(),
    ){
        private var grid: CharGrid2D = CharGrid2D(input)
        private val visited: MutableSet<Pair<Int, Int>> = mutableSetOf()
        private val toVisit: MutableSet<Pair<Int, Int>> = mutableSetOf()

        fun explore(){
            toVisit.add(0 to 0)
            while (toVisit.isNotEmpty()) {
                exploreZone(toVisit.first())
            }
        }

        private fun exploreZone(startingPoint: Pair<Int, Int>) {
            val queue = ArrayDeque<Pair<Int, Int>>()
            val fences: HashSet<Triple<Int, Int, Direction>> = hashSetOf()
            val idZone = grid.getElementAt(startingPoint)
            var area = 1
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
                    val nextCellId = grid.getElementAt(nx to ny)
                    if (visited.contains(nx to ny) && nextCellId == idZone) continue
                    when (grid.getElementAt(nx to ny)) {
                        idZone -> {
                            visited.add(nx to ny)
                            toVisit.remove(nx to ny)
                            area++
                            queue.add(nx to ny)
                        }
                        else -> {
                            toVisit.add(nx to ny)
                            fences.add(Triple(nx, ny, direction))
                        }
                    }
                }
            }
            toVisit.removeAll(visited)
            zones.add(ZoneInfo(area, fences.size, getUniqueFencesCount(fences)))
        }

        private fun removeAdjacentFences(fences: HashSet<Triple<Int, Int, Direction>>, fence: Triple<Int, Int, Direction>, direction: Direction) {
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

        private fun getUniqueFencesCount(fences: HashSet<Triple<Int, Int, Direction>>): Int {
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
    }

    fun part1(input: List<String>): Int {
        val garden = Garden(input)
        garden.explore()

        return garden.zones.sumOf {
            it.area * it.perimeter
        }
    }

    fun part2(input: List<String>): Int {
        val garden = Garden(input)
        garden.explore()

        return garden.zones.sumOf {
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