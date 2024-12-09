package aoc2024.day09

import common.benchmarkTime
import common.println
import common.readInputAsString

fun main() {

    data class Segment(var id: Int, val fileLength: Int, var freeLength: Int)

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

    fun part1(input: String): Long {
        val values = (input + "0").toList().map(){ it.digitToInt() }
        val checksum = Checksum()

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
                checksum.addChunk(left/2, leftValue)
            }
            if (rightSpaceRequired == 0) {
                right -= 2
                rightValue = (right - 1) / 2
                rightSpaceRequired = values[right - 1]
            }

            if (leftSpaceAvailable > 0 && rightSpaceRequired > 0) {
                if (rightSpaceRequired > leftSpaceAvailable) {
                    rightSpaceRequired -= leftSpaceAvailable
                    checksum.addChunk(rightValue, leftSpaceAvailable)
                    leftSpaceAvailable = 0
                } else {
                    leftSpaceAvailable -= rightSpaceRequired
                    checksum.addChunk(rightValue, rightSpaceRequired)
                    rightSpaceRequired = 0
                }
            }
        }
        checksum.addChunk(rightValue, rightSpaceRequired)

        return checksum.getChecksum()
    }

    fun part2(input: String): Long {
        val buckets: Array<ArrayDeque<Pair<Int, Int>>> = Array(10){ ArrayDeque<Pair<Int, Int>>() }
        val segments = (input + "0")
            .chunked(2)
            .mapIndexed { index, chars ->
                buckets[chars[0].digitToInt()].add(index to chars[0].digitToInt())
                Segment(
                    id = index,
                    fileLength = chars[0].digitToInt(),
                    freeLength = chars[1].digitToInt(),
                )
            }.toTypedArray()

        var currentIndex = 0
        val checksum = Checksum()

        fun getIndexForFreeLength(freeLength: Int): Int? {
            var nextElement = Pair(-1, -1)
            for (i in 0 .. freeLength) {
                if (buckets[i].isNotEmpty()) {
                    buckets[i].lastOrNull() ?.let {
                        if (it.first > nextElement.first) nextElement = it
                    }
                }
            }
            if (nextElement.first >= 0) {
                buckets[nextElement.second].removeLast()
                return nextElement.first
            } else return null
        }

        checksum.addChunk(segments[currentIndex].id, segments[currentIndex].fileLength)
        var currentElement = segments[currentIndex]
        while (true) {
            var searchIndex = getIndexForFreeLength(currentElement.freeLength)

            if (searchIndex != null && searchIndex < currentIndex) {
                searchIndex = null
            }

            if (searchIndex != null) {
                val searchedElement = segments[searchIndex]
                checksum.addChunk(searchedElement.id, searchedElement.fileLength)
                currentElement.freeLength -= searchedElement.fileLength
                searchedElement.id = 0
            }

            if (currentElement.freeLength == 0 || searchIndex == null) {
                if (currentElement.freeLength > 0) {
                    checksum.addChunk(0, currentElement.freeLength)
                }
                currentIndex++
                if (currentIndex == segments.size) break
                currentElement = segments[currentIndex]
                checksum.addChunk(currentElement.id, currentElement.fileLength)
            }
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