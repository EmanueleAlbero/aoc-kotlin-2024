package aoc2024.day09

import common.benchmarkTime
import common.println
import common.readInputAsString

fun main() {

    fun part1(input: String): Long {
        val values = (input + "0").toList().map(){ it.digitToInt() }
        val array: ArrayDeque<Int> = ArrayDeque()

        var left = -2
        var right = values.size + 1
        var leftSpaceAvailable = 0
        var rightSpaceRequired = 0
        var rightValue = 0
        while (left+2 < (right -1)) {
            if (leftSpaceAvailable == 0) {
                left += 2
                val leftValue = values[left]
                leftSpaceAvailable = values[left + 1]
                repeat(leftValue) { array.add(left / 2) }
            }
            if (rightSpaceRequired == 0) {
                right -= 2
                rightValue = (right - 1) / 2
                rightSpaceRequired = values[right - 1]
            }

            if (leftSpaceAvailable > 0 && rightSpaceRequired > 0) {
                if (rightSpaceRequired > leftSpaceAvailable) {
                    rightSpaceRequired -= leftSpaceAvailable
                    repeat(leftSpaceAvailable) { array.add(rightValue) }
                    leftSpaceAvailable = 0
                } else {
                    leftSpaceAvailable -= rightSpaceRequired
                    repeat(rightSpaceRequired) { array.add(rightValue) }
                    rightSpaceRequired = 0
                }
            }
        }
        if (rightSpaceRequired > 0) {
            repeat(rightSpaceRequired) { array.add(rightValue) }
        }

        return array.foldIndexed(0) { index, acc, value -> acc + (value*index) }
    }

    data class Segment(val id: Int, val fileLength: Int, val freeLength: Int)

    class Checksum(){
        private var size = 0L
        private var checksum = 0L
        fun addChunk(value: Int, length: Int) {
            repeat(length) { checksum += value * (size + it) }
            size += length
        }

        fun getChecksum(): Long {
            return checksum
        }
    }

    fun part2(input: String): Long {
        val segments = (input + "0")
            .chunked(2)
            .mapIndexed { index, chars ->
                Segment(
                    id = index,
                    fileLength = chars[0].digitToInt(),
                    freeLength = chars[1].digitToInt()
                )
            }.toMutableList()

        var currentIndex = 0
        val checksum = Checksum()

        checksum.addChunk(segments[currentIndex].id, segments[currentIndex].fileLength)
        while (currentIndex < segments.size) {
            var foundFit = false
            var searchIndex = segments.lastIndex
            while (searchIndex > currentIndex && segments[currentIndex].freeLength > 0) {
                val candidate = segments[searchIndex]
                if (candidate.id > 0 && candidate.fileLength <= segments[currentIndex].freeLength) {
                    foundFit = true
                    break
                }
                searchIndex--
            }

            if (!foundFit) {
                if (segments[currentIndex].freeLength > 0) {
                    checksum.addChunk(0, segments[currentIndex].freeLength)
                }
                currentIndex++
                if (currentIndex == segments.size) break
                checksum.addChunk(segments[currentIndex].id, segments[currentIndex].fileLength)
                continue
            }

            val candidate = segments[searchIndex]
            checksum.addChunk(segments[searchIndex].id, segments[searchIndex].fileLength)
            val updatedFree = segments[currentIndex].freeLength - candidate.fileLength
            segments[currentIndex] = segments[currentIndex].copy(freeLength = updatedFree)
            segments[searchIndex] = segments[searchIndex].copy(id = 0)
        }

        return checksum.getChecksum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day09_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 1928L)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == 2858L)

    val input = readInputAsString("aoc2024/Day09")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}