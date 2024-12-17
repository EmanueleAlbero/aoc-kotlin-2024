package aoc2024.day17

import common.benchmarkTime
import common.println
import common.readInputAsString

fun main() {
    val regex = Regex(
        """^Register A:\s+(\d+)\r?\nRegister B:\s+(\d+)\r?\nRegister C:\s+(\d+)\r?\n\s*Program:\s+(.+)$""".trimMargin(),
        RegexOption.MULTILINE
    )

    class Memory(
        var registerA: Long,
        var registerB: Long,
        var registerC: Long,
        var pointer: Int,
        val intructions: List<Long>
    ) {
        fun getCombo(operand: Long): Long {
            return when (operand) {
                4L -> registerA
                5L -> registerB
                6L -> registerC
                7L -> throw IllegalArgumentException("Invalid operand")
                else -> operand
            }
        }
    }

    fun executeProgram(memory: Memory, breakIfDifferent: Boolean = false, stopAfterFirstOutput: Boolean = false): List<Long> {
        val output = mutableListOf<Long>()
        var index = 0
        while (memory.pointer < memory.intructions.size) {
            val opCode = memory.intructions[memory.pointer]
            val operand = memory.intructions[memory.pointer + 1]
            memory.pointer += 2

            when (opCode) {
                0L -> {
                    memory.registerA = memory.registerA shr memory.getCombo(operand).toInt()
                }
                1L -> {
                    memory.registerB = memory.registerB.xor(operand)
                }
                2L -> {
                    memory.registerB = memory.getCombo(operand) and 0b111L
                }
                3L -> {
                    if (memory.registerA != 0L) {
                        memory.pointer = operand.toInt()
                    }
                }
                4L -> {
                    memory.registerB = memory.registerB.xor(memory.registerC)
                }
                5L -> {
                    val value = memory.getCombo(operand) and 0b111L
                    if (breakIfDifferent && memory.intructions[index] != value) {
                        if (output.size == memory.intructions.size) {
                            return output.drop(1)
                        }
                        break
                    }
                    output.add(value)
                    index++

                    if (stopAfterFirstOutput) {
                        return output
                    }
                }
                6L -> {
                    memory.registerB = memory.registerA shr memory.getCombo(operand).toInt()
                }
                7L -> {
                    memory.registerC = memory.registerA shr memory.getCombo(operand).toInt()
                }
                else -> throw IllegalArgumentException("Unknown opCode: $opCode")
            }
        }
        return output
    }


    fun part1(input: String): String {
        val matchResult = regex.matchEntire(input) ?: return ""
        val memory = Memory(
            matchResult.groupValues[1].toLong(),
            matchResult.groupValues[2].toLong(),
            matchResult.groupValues[3].toLong(),
            0,
            matchResult.groupValues[4].split(",").map { it.toLong() }
        )

        return executeProgram(memory).joinToString(",") { it.toString() }
    }

    fun part2(input: String): Long {
        val matchResult = regex.matchEntire(input) ?: return -1
        val initialB = matchResult.groupValues[2].toLong()
        val initialC = matchResult.groupValues[3].toLong()
        val instructions = matchResult.groupValues[4].split(",").map { it.toLong() }

        data class State(val index: Int, val a: Long)

        val pending = ArrayDeque<State>()
        pending.add(State(instructions.size, 0L))
        val valid = mutableListOf<Long>()

        while (pending.isNotEmpty()) {
            val (index, currentA) = pending.removeFirst()
            if (index == 0) {
                valid += currentA
                continue
            }

            val desiredOutput = instructions[index - 1]

            for (i in 0L until 8L) {
                val prevA = (currentA shl 3) or i
                val memory = Memory(prevA, initialB, initialC, 0, instructions)

                val out = executeProgram(memory, stopAfterFirstOutput = true)
                if (out.size == 1 && out[0] == desiredOutput && memory.registerA == currentA) {
                    pending.addFirst(State(index - 1, prevA))
                }
            }
        }

        return if (valid.isNotEmpty()) valid.min() else -1
    }


    val testInput = readInputAsString("aoc2024/Day17_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == "4,6,3,5,6,3,5,2,1,0")

    val testInput2 = readInputAsString("aoc2024/Day17_test2")
    val part2Result = part2(testInput2)
    part2Result.println()
    check(part2Result == 117440L)

    val input = readInputAsString("aoc2024/Day17")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}
