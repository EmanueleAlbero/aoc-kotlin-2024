package aoc2024.day15

import common.*
import common.Direction.*

fun main() {

    val boxesValues = intArrayOf(1,3,4)

    data class Cords(var x: Int, var y: Int)

    fun Direction.toCords(): Cords = when (this) {
        LEFT -> Cords(-1,0)
        RIGHT -> Cords(1,0)
        UP -> Cords(0,-1)
        DOWN -> Cords(0,1)
        else -> Cords(0,0)
    }

    class Robot(val position: Cords, speed: Int) {
        fun push(grid: NumberGrid2D, direction: Direction) {
            val move = direction.toCords()
            val nextElement = grid.getElementAt(position.x + move.x, position.y + move.y)
            if (nextElement == -1) {
                return
            }
            if (nextElement == 0) {
                position.x += move.x
                position.y += move.y
                grid.setValue(position.x, position.y, 0)
                return
            }

            when (direction) {
                LEFT, RIGHT -> pushHorizontally(grid, direction, move)
                else -> pushVertically(grid, direction, move)
            }
        }

        private fun pushVertically(grid: NumberGrid2D, direction: Direction, move: Cords) {
            var canBeMoved = false
            var boxToMove: MutableSet<Item<Int>> = mutableSetOf()
            var toBeMoved: MutableSet<Cords> = mutableSetOf()

            var newPosition = Cords(position.x + move.x, position.y + move.y)
            addBox(toBeMoved, grid, newPosition)
            toBeMoved.forEach { boxToMove.add(Item<Int>(it.x, it.y, null, grid.getElementAt(it.x, it.y)!!)) }
            while (!toBeMoved.any { it -> grid.getElementAt(it.x, it.y+move.y)!!  == -1}){

                if (toBeMoved.all { it -> grid.getElementAt(it.x, it.y+move.y)!!  == 0}){
                    canBeMoved = true
                    break
                }
                var toBeMoved2 = mutableSetOf<Cords>()

                toBeMoved
                    .filter {
                        grid.getElementAt(it.x, it.y+move.y)!!  in boxesValues
                    }
                    .forEach {
                        addBox(toBeMoved2, grid, Cords(it.x, it.y+move.y))
                    }

                toBeMoved = toBeMoved2
                toBeMoved.forEach { boxToMove.add(Item<Int>(it.x, it.y, null, grid.getElementAt(it.x, it.y)!!)) }
            }

            if (canBeMoved) {
                position.x += move.x
                position.y += move.y

                when (direction) {
                    UP -> {
                        boxToMove
                            .sortedBy { it.y }
                            .forEach() {
                                grid.setValue(it.x, it.y - 1, it.value)
                                grid.setValue(it.x, it.y, 0)
                            }
                    }
                    else -> {
                        boxToMove
                            .sortedByDescending { it.y }
                            .forEach() {
                                grid.setValue(it.x, it.y + 1, it.value)
                                grid.setValue(it.x, it.y, 0)
                            }
                    }
                }
                grid.setValue(position.x, position.y, 0)

            }
        }

        private fun pushHorizontally(grid: NumberGrid2D, direction: Direction, move: Cords) {
            var stack = 0

            var newPosition = Cords(position.x + move.x, position.y + move.y)
            var nextElement = grid.getElementAt(newPosition.x, newPosition.y) ?: 0
            while (nextElement in boxesValues) {
                while (grid.getElementAt(newPosition.x, newPosition.y)!! in boxesValues) {
                    stack++
                    newPosition = Cords(position.x + (move.x * stack), position.y + (move.y * stack))
                }
                stack -= 1
                nextElement = grid.getElementAt(newPosition.x, newPosition.y) ?: 0
            }
            if (nextElement == -1) {
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
            grid.setValue(position.x, position.y, 0)
        }

        private fun addBox(toBeMoved: MutableSet<Cords>, grid: NumberGrid2D, box: Cords) {
            toBeMoved.add(box)
            if (grid.getElementAt(box.x, box.y) == 3) {
                toBeMoved.add(Cords(box.x+1, box.y))
            }
            if (grid.getElementAt(box.x, box.y) == 4) {
                toBeMoved.add(Cords(box.x-1, box.y))
            }

        }
    }

    fun printMap(grid: NumberGrid2D, robot: Robot) {
        val builder = StringBuilder()
        grid.setValue(robot.position.x, robot.position.y, 2)
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                builder.append(
                    when (grid.getElementAt(x, y)) {
                        -1 -> '#'
                        0 -> '.'
                        1 -> 'O'
                        2 -> '@'
                        3 -> '['
                        4 -> ']'
                        else -> ' '
                    }
                )
            }
            builder.append('\n')
        }
        println(builder.toString())
        grid.setValue(robot.position.x, robot.position.y, 0)
    }

    fun calculateGridScore(grid: NumberGrid2D): Long {
        return grid.findAll { it == 1 || it == 3 }.sumOf {
            (100L*it.second) + it.first
        }
    }

    fun part1(input: String): Long {
        val gridData = input.substringBefore("\r\n\r\n").split("\r\n")
        val operationsData = input.substringAfter("\r\n\r\n")
        val grid =
            NumberGrid2D(
                gridData
            ) {
                when (it) {
                    '#' -> -1;
                    'O' -> 1;
                    '@' -> 2;
                    else -> 0
                }
            }
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

        var robot = grid.find(2)!!.let { Robot( Cords(it.first, it.second), 0) }
        grid.setValue(robot.position.x, robot.position.y, 0)

        //printMap(grid, robot)
        operations.forEachIndexed() { index, it ->
            robot.push(grid, it)
            /*if (index < operations.size - 1)
                operations[index+1].println()
            printMap(grid, robot)
            readln()*/
        }

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
        val grid =
            NumberGrid2D(
                gridData
            ) {
                when (it) {
                    '#' -> -1;
                    'O' -> 1;
                    '@' -> 2;
                    '[' -> 3;
                    ']' -> 4;
                    else -> 0
                }
            }
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

        var robot = grid.find(2)!!.let { Robot( Cords(it.first, it.second), 0) }
        grid.setValue(robot.position.x, robot.position.y, 0)

        operations.forEachIndexed() { index, it ->
            robot.push(grid, it)
        }

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