package common

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("resources/$name.txt").readLines()

fun readInputAsString(name: String) = Path("resources/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun Any?.println() = println(this)

fun <R> benchmarkTime(blockTitle: String, timeUnit: DurationUnit = DurationUnit.MILLISECONDS, printResult: Boolean = true, vararg params: Any, function: (Array<out Any>) -> R): R {
    val result: R
    val timeTaken = measureTime {
        result = function(params)
    }
    println("<<< $blockTitle >>>")
    println("Execution time: ${timeTaken.toString(timeUnit, 2)}")
    if (printResult) {
        println("Result: $result")
    }
    println("")

    return result
}
