package aoc2024.day14

import common.benchmarkTime
import common.println
import common.readInput

fun main() {

    data class Coordinates(val x: Long, val y: Long)
    data class Robot(val position: Coordinates, val speed: Coordinates)

    fun parseRobots(input: List<String>): MutableList<Robot> {
        val regex = Regex("p=(\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)")

        return input.mapNotNull { line ->
            regex.matchEntire(line)?.destructured?.let { (px, py, vx, vy) ->
                val position = Coordinates(px.toLong(), py.toLong())
                val speed = Coordinates(vx.toLong(), vy.toLong())
                Robot(position, speed)
            }
        }.toMutableList()
    }

    fun Long.modWithWrap(limit: Long): Long = ((this % limit) + limit) % limit

    fun Coordinates.updatePosition(speed: Coordinates, gridSize: Coordinates, time: Int) =
        Coordinates(
            (this.x + speed.x * time).modWithWrap(gridSize.x),
            (this.y + speed.y * time).modWithWrap(gridSize.y),
        )

    fun part1(input: List<String>, gridSize: Coordinates, time: Int = 100): Int {
        val robots = parseRobots(input)
        val halfX = gridSize.x / 2
        val halfY = gridSize.y / 2
        return robots.map{
            it.position.updatePosition(it.speed, gridSize, time)
        }.let { formation ->
            formation.count { it.x < halfX && it.y < halfY }
                .times(formation.count { it.x > halfX && it.y > halfY })
                .times(formation.count { it.x < halfX && it.y > halfY })
                .times(formation.count { it.x > halfX && it.y < halfY })
        }
    }

    fun Set<Coordinates>.meets(gridSize: Coordinates, criteria: (Set<Coordinates>, gridSize: Coordinates) -> Boolean): Boolean =
        criteria(this, gridSize)

    fun treeShapeCriteria(formation: Set<Coordinates>, gridSize: Coordinates): Boolean {
        val matrixSize = 3L

        return formation.any { element ->
            if (element.x < matrixSize || element.y < matrixSize ||
                element.x > gridSize.x - matrixSize || element.y > gridSize.y - matrixSize
            ) return@any false

            val hasNoHoles = (element.y - matrixSize..element.y + matrixSize).all { y ->
                (element.x - matrixSize..element.x + matrixSize).all { x ->
                    Coordinates(x, y) in formation
                }
            }
            hasNoHoles
        }
    }

    fun printFormation(gridSize: Coordinates, formation: Set<Coordinates>) {
        val builder = StringBuilder()
        for (y in 0L until gridSize.y) {
            for (x in 0L until gridSize.x) {
                builder.append(if (Coordinates(x, y) in formation) '#' else ' ')
            }
            builder.append('\n')
        }
        println(builder.toString())
    }

    fun part2(input: List<String>, gridSize: Coordinates): Int {
        val robots = parseRobots(input)
        var formation: Set<Coordinates> = setOf()
        var seconds = 0

        while(!formation.meets(gridSize, ::treeShapeCriteria)) {
            seconds++
            formation = robots.map{
                it.position.updatePosition(it.speed, gridSize, seconds)
            }.toSet()
        }

        printFormation(gridSize, formation)
        return seconds
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day14_test")
    val part1Result = part1(testInput, Coordinates(11,7))
    part1Result.println()
    check(part1Result == 12)

    val input = readInput("aoc2024/Day14")
    benchmarkTime("part1") {
        part1(input, Coordinates(101L, 103L))
    }

    benchmarkTime("part2") {
        part2(input, Coordinates(101L, 103L))
    }
}