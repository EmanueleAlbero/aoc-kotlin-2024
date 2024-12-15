package aoc2024.day15

import common.*
import common.Direction.*

const val EMPTY = '.'
const val BOX = 'O'
const val BOX_LEFT = '['
const val BOX_RIGHT = ']'
const val WALL = '#'
const val ROBOT = '@'

data class Cords(var x: Int, var y: Int)

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

    private fun pushVertically(grid: CharGrid2D, direction: Direction, move: Cords) {
        var canBeMoved = false
        val boxToMove: MutableSet<Item<Char>> = mutableSetOf()
        var toBeMoved: MutableSet<Cords> = mutableSetOf()

        val newPosition = Cords(position.x + move.x, position.y + move.y)
        addBoxToStack(toBeMoved, grid, newPosition)
        toBeMoved.forEach { boxToMove.add(Item(it.x, it.y, null, grid.getElementAt(it.x, it.y)!!)) }
        while (!toBeMoved.any { grid.getElementAt(it.x, it.y+move.y)!!  == WALL}){

            if (toBeMoved.all { grid.getElementAt(it.x, it.y+move.y)!!  == EMPTY}){
                canBeMoved = true
                break
            }
            val toBeMoved2 = mutableSetOf<Cords>()

            toBeMoved
                .filter {
                    grid.getElementAt(it.x, it.y+move.y)!!  in boxesValues
                }
                .forEach {
                    addBoxToStack(toBeMoved2, grid, Cords(it.x, it.y+move.y))
                }

            toBeMoved = toBeMoved2
            toBeMoved.forEach { boxToMove.add(Item(it.x, it.y, null, grid.getElementAt(it.x, it.y)!!)) }
        }

        if (canBeMoved) {
            position.x += move.x
            position.y += move.y

            when (direction) {
                UP -> {
                    boxToMove
                        .sortedBy { it.y }
                        .forEach {
                            grid.setValue(it.x, it.y - 1, it.value)
                            grid.setValue(it.x, it.y, EMPTY)
                        }
                }
                else -> {
                    boxToMove
                        .sortedByDescending { it.y }
                        .forEach {
                            grid.setValue(it.x, it.y + 1, it.value)
                            grid.setValue(it.x, it.y, EMPTY)
                        }
                }
            }
            grid.setValue(position.x, position.y, EMPTY)

        }
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