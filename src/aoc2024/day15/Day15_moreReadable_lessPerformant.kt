package aoc2024.day15_readable

import common.*
import common.Direction.*

const val EMPTY = '.'
const val BOX = 'O'
const val BOX_LEFT = '['
const val BOX_RIGHT = ']'
const val WALL = '#'
const val ROBOT = '@'

data class Cords(var x: Int, var y: Int) {
    operator fun plus(move: Cords): Cords {
        return Cords(x + move.x, y + move.y)
    }
}

fun Direction.toCords(): Cords = when (this) {
    LEFT -> Cords(-1,0)
    RIGHT -> Cords(1,0)
    UP -> Cords(0,-1)
    DOWN -> Cords(0,1)
    else -> Cords(0,0)
}

class Robot(val position: Cords) {
    private val boxesValues = arrayOf(BOX,BOX_LEFT,BOX_RIGHT)

    fun push(grid: CharGrid2D, direction: Direction) {
        val move = direction.toCords()
        val nextElement = grid.getElementAt(position.x + move.x, position.y + move.y)

        when (nextElement) {
            WALL -> return
            EMPTY -> {
                position.x += move.x
                position.y += move.y
                return
            }
            else -> {
                when (direction) {
                    LEFT, RIGHT -> pushHorizontally(grid, direction, move)
                    else -> pushVertically(grid, direction, move)
                }
            }
        }
    }

    private fun MutableSet<Item<Char>>.addAll(boxes: Set<Cords>, grid: CharGrid2D) {
        boxes.forEach {
            this.add(Item(it.x, it.y, null, grid.getElementAt(it.x, it.y)!!))
        }
    }

    private fun pushVertically(grid: CharGrid2D, direction: Direction, movement: Cords) {
        val newPosition = position + movement
        val boxesThatWillBePushed = mutableSetOf<Item<Char>>()
        val lastRowOfPushedBoxes = getConnectedBoxes(grid, newPosition)
        boxesThatWillBePushed.addAll(lastRowOfPushedBoxes, grid)

        var canBePushed = false
        while (lastRowOfPushedBoxes.none { grid.getElementAt(it.x, it.y + movement.y) == WALL }) {
            if (lastRowOfPushedBoxes.all { grid.getElementAt(it.x, it.y + movement.y) == EMPTY }) {
                canBePushed = true
                break
            }

            lastRowOfPushedBoxes
                .filter { grid.getElementAt(it.x, it.y + movement.y) in boxesValues }
                .forEach { box ->
                    lastRowOfPushedBoxes.remove(box)
                    lastRowOfPushedBoxes.addAll(getConnectedBoxes(grid, box + movement))
                    boxesThatWillBePushed.addAll(lastRowOfPushedBoxes, grid)
                }
        }

        if (canBePushed) {
            move(movement)
            moveBoxes(grid, boxesThatWillBePushed, direction)
        }
    }

    private fun getConnectedBoxes(grid: CharGrid2D, box: Cords): MutableSet<Cords> {
        when (grid.getElementAt(box.x, box.y)) {
            BOX -> return mutableSetOf(box)
            BOX_LEFT -> return mutableSetOf(box, Cords(box.x+1, box.y))
            BOX_RIGHT -> return mutableSetOf(box, Cords(box.x-1, box.y))
        }
        return mutableSetOf()
    }

    private fun moveBoxes(
        grid: CharGrid2D,
        boxToMove: Set<Item<Char>>,
        direction: Direction
    ) {
        val sortedBoxes = if (direction == UP) {
            boxToMove.sortedBy { it.y }
        } else {
            boxToMove.sortedByDescending { it.y }
        }

        sortedBoxes.forEach {
            val targetY = it.y + if (direction == UP) -1 else 1
            grid.setValue(it.x, targetY, it.value)
            grid.setValue(it.x, it.y, EMPTY)
        }

        grid.setValue(position.x, position.y, EMPTY)
    }

    private fun move(move: Cords) {
        position.x += move.x
        position.y += move.y
    }

    private fun pushHorizontally(grid: CharGrid2D, direction: Direction, move: Cords) {
        var stack = 0
        var newPosition = Cords(position.x + move.x, position.y + move.y)
        var nextElement = grid.getElementAt(newPosition.x, newPosition.y) ?: EMPTY
        while (nextElement in boxesValues) {
            while (grid.getElementAt(newPosition.x, newPosition.y)!! in boxesValues) {
                stack++
                newPosition = Cords(position.x + (move.x * stack), position.y + (move.y * stack))
            }
            stack -= 1
            nextElement = grid.getElementAt(newPosition.x, newPosition.y) ?: EMPTY
        }
        if (nextElement == WALL) {
            return
        }

        position.x += move.x
        position.y += move.y

        when (direction) {
            LEFT -> {
                for (x in stack downTo 1) {
                    grid.setValue(
                        position.x - x, position.y,
                        grid.getElementAt(position.x - x + 1, position.y)!!
                    )
                }
            }
            else -> {
                for (x in stack downTo 1) {
                    grid.setValue(
                        position.x + x, position.y,
                        grid.getElementAt(position.x + x - 1, position.y)!!
                    )
                }
            }
        }
        grid.setValue(position.x, position.y, EMPTY)
    }

    private fun addBoxToStack(toBeMoved: MutableSet<Cords>, grid: CharGrid2D, box: Cords) {
        toBeMoved.add(box)
        if (grid.getElementAt(box.x, box.y) == BOX_LEFT) {
            toBeMoved.add(Cords(box.x+1, box.y))
        }
        if (grid.getElementAt(box.x, box.y) == BOX_RIGHT) {
            toBeMoved.add(Cords(box.x-1, box.y))
        }
    }
}

fun main() {
    fun printMap(grid: CharGrid2D, robot: Robot) {
        val builder = StringBuilder()
        grid.setValue(robot.position.x, robot.position.y, ROBOT)
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                builder.append(
                    grid.getElementAt(x, y)
                )
            }
            builder.append('\n')
        }
        println(builder.toString())
        grid.setValue(robot.position.x, robot.position.y, EMPTY)
    }

    fun calculateGridScore(grid: CharGrid2D): Long {
        return grid.findAll { it == BOX || it == BOX_LEFT }.sumOf {
            (100L*it.second) + it.first
        }
    }

    fun part1(input: String): Long {
        val gridData = input.substringBefore("\r\n\r\n").split("\r\n")
        val operationsData = input.substringAfter("\r\n\r\n")

        val grid = CharGrid2D(gridData)
        val operations: List<Direction> =
            operationsData
                .mapNotNull {
                    when (it) {
                        '<' -> LEFT
                        '>' -> RIGHT
                        '^' -> UP
                        'v' -> DOWN
                        else -> null
                    }
                }

        val robot = grid.find(ROBOT)!!.let { Robot( Cords(it.first, it.second)) }
        grid.setValue(robot.position.x, robot.position.y, EMPTY)

        operations.forEach { robot.push(grid, it) }

        printMap(grid, robot)
        return calculateGridScore(grid)
    }

    fun part2(input: String): Long {
        val gridData = input
            .substringBefore("\r\n\r\n")
            .split("\r\n")
            .map { line ->
                line.flatMap { char ->
                    when (char) {
                        '#' -> listOf('#', '#')
                        'O' -> listOf('[', ']')
                        '@' -> listOf('@', ' ')
                        else -> listOf('.', '.')
                    }
                }.joinToString("")
            }
        val operationsData = input.substringAfter("\r\n\r\n")

        val grid = CharGrid2D(gridData)
        val operations: List<Direction> =
            operationsData
                .mapNotNull {
                    when (it) {
                        '<' -> LEFT
                        '>' -> RIGHT
                        '^' -> UP
                        'v' -> DOWN
                        else -> null
                    }
                }

        val robot = grid.find('@')!!.let { Robot( Cords(it.first, it.second)) }
        grid.setValue(robot.position.x, robot.position.y, EMPTY)

        operations.forEach { robot.push(grid, it) }
        printMap(grid, robot)

        return calculateGridScore(grid)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day15_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 10092L)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 9021L)

    val input = readInputAsString("aoc2024/Day15")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}