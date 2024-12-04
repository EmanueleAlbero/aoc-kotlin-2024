package aoc2024.day04

import common.*

fun main() {

    fun countXMASWordFromHere(cGrid: CharGrid2D, x: Int, y: Int): Int {
        val xmasWords = listOf("XMAS", "XMAS".reversed())
        return Direction.entries.count {
            cGrid.getStringChunk(x, y, 4, it) in xmasWords
        }
    }

    fun isNewXMasWord(cGrid: CharGrid2D, x: Int, y: Int): Boolean {
        val xmasWords = listOf("MAS", "MAS".reversed())
        cGrid.setPosition(x,y)
        if (!cGrid.canMove(Direction.UP_LEFT)) return false
        if (!cGrid.canMove(Direction.UP_RIGHT)) return false

        val cross1 = cGrid.getStringChunk(x-1,y-1,3, Direction.DOWN_RIGHT)
        val cross2 = cGrid.getStringChunk(x+1,y-1,3, Direction.DOWN_LEFT)

        return (cross1 in xmasWords) && (cross2 in xmasWords)
    }

    fun foldGrid(
        cGrid: CharGrid2D,
        targetChar: Char,
        action: (cGrid: CharGrid2D, x: Int, y: Int) -> Int
    ): Int {
        return cGrid.fold(0, 0, cGrid.width - 1, cGrid.height - 1, 0) { x, y, acc ->
            val checkChar = cGrid.getElementAt(x, y) ?: ' '
            val currentValue: Int = acc as Int
            if (checkChar == targetChar) {
                currentValue + action(cGrid, x, y)
            } else {
                currentValue
            }
        } as Int
    }

    fun part1(cGrid: CharGrid2D): Int {
        return foldGrid(cGrid, 'X') { grid, x, y ->
            countXMASWordFromHere(grid, x, y)
        }
    }

    fun part2(cGrid: CharGrid2D): Int {
        return foldGrid(cGrid, 'A') { grid, x, y ->
            if (isNewXMasWord(grid, x, y)) 1 else 0
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day04_test")
    val testGrid = CharGrid2D(testInput) { c -> c }
    val part1Result = part1(testGrid)
    part1Result.println()
    check(part1Result == 18)

    val part2Result = part2(testGrid)
    part2Result.println()
    check(part2Result == 9)

    val input = readInput("aoc2024/Day04")
    val realGrid = CharGrid2D(input) { c -> c }
    benchmarkTime("part1") {
        part1(realGrid)
    }

    benchmarkTime("part2") {
        part2(realGrid)
    }
}