package aoc2024.day05

import common.*

class PageRule(
    val value: Int,
    val next: MutableList<Int> = mutableListOf<Int>(),
    val previous: MutableList<Int> = mutableListOf<Int>(),
)

class PrintRules(
    val rules: MutableMap<Int, PageRule> = mutableMapOf()
)

class PrintInstructions(
    val instructions: List<PrintOrder> = emptyList()
)

class PrintOrder(
    val pages : List<Int> = emptyList()
)

infix fun PrintRules.reads(input: String): PrintRules {
        input
            .substringBefore("\r\n\r\n")
            .split("\r\n")
            .forEach() {
                val (from, to) =
                    it.split("|")
                        .map { values -> values.toInt() }

                val fromNode = rules.getOrPut(from) { PageRule(from) }
                val toNode = rules.getOrPut(to) { PageRule(to) }
                fromNode.next.add(to)
                toNode.previous.add(from)
            }
    return this
}

infix fun PrintInstructions.reads(input: String): PrintInstructions {
    val pages = input
        .substringAfter("\r\n\r\n")
        .split("\r\n")
        .map {
            PrintOrder(it.split(",").map { page -> page.toInt() })
        }
    return PrintInstructions(pages)
}

infix fun PrintOrder.respect(testPrintRules: PrintRules): Boolean {
    this.pages.indices
        .forEachIndexed() { index, _ ->
            if (!isPageInCorrectOrder(index, testPrintRules)) return false
        }
    return true
}

private fun PrintOrder.isPageInCorrectOrder(pageIndex: Int, testPrintRules: PrintRules): Boolean {
    for (j in pageIndex + 1 until this.pages.size) {
        val rule = testPrintRules.rules[this.pages[j]]
        if (rule != null && rule.next.contains(this.pages[pageIndex])) {
            return false
        }
    }
    return true
}

class Node(
    val value: Int,
    val ingress: MutableList<Int> = mutableListOf<Int>(),
    val egress: MutableList<Int> = mutableListOf<Int>(),
)
class Graph(val size: Int) {
    val nodes = mutableMapOf<Int, Node>()

    fun addArc(from: Int, to: Int) {
        val fromNode = nodes.getOrPut(from) { Node(from) }
        val toNode = nodes.getOrPut(to) { Node(to) }
        fromNode.egress.add(to)
        toNode.ingress.add(from)
    }

    fun getNodeValues(): List<Int> = nodes.keys.toList()
}

fun buildGraphFromRules(rules: PrintRules, pagesSet: List<Int>): Graph {
    val graph = Graph(pagesSet.size)
    for (page in pagesSet) {
        val rule = rules.rules[page]
        if (rule != null) {
            for (nextPage in rule.next) {
                if (nextPage in pagesSet) {
                    graph.addArc(page, nextPage)
                }
            }
        }
    }
    return graph
}

fun topSort(graph: Graph): List<Int> {
    val visited = mutableSetOf<Int>()
    val stack = mutableListOf<Int>()

    fun dfs(node: Int) {
        visited.add(node)
        for (neighbor in graph.nodes[node]?.egress ?: emptyList<Int>()) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor)
            }
        }
        stack.add(node)
    }

    for (node in graph.getNodeValues()) {
        if (!visited.contains(node)) {
            dfs(node)
        }
    }
    return stack.reversed()
}


infix fun PrintOrder.fixBy(testPrintRules: PrintRules): PrintOrder {
    val graph = buildGraphFromRules(testPrintRules, this.pages)
    val sortedPages = topSort(graph)
    return PrintOrder(sortedPages)
}

fun main() {

    fun part1(printRules: PrintRules, printInstructions: PrintInstructions) =
        printInstructions.instructions.filter {
            it respect printRules
        }.sumOf {
            it.pages[it.pages.size/2]
        }

    fun part2(printRules: PrintRules, printInstructions: PrintInstructions) =
        printInstructions.instructions.filterNot {
            it respect printRules
        }.sumOf {
            (it fixBy printRules)
                .pages[it.pages.size/2]
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInputAsString("aoc2024/Day05_test")
    val testPrintRules = PrintRules() reads testInput
    val testPrintInstructions = PrintInstructions() reads testInput
    val part1Result = part1(testPrintRules, testPrintInstructions)
    part1Result.println()
    check(part1Result == 143)

    val part2Result = part2(testPrintRules, testPrintInstructions)
    part2Result.println()
    check(part2Result == 123)

    val input = readInputAsString("aoc2024/Day05")
    val printRules = PrintRules() reads input
    val printInstructions = PrintInstructions() reads input

    benchmarkTime("part1") {
        part1(printRules, printInstructions)
    }

    benchmarkTime("part2") {
        part2(printRules, printInstructions)
    }
}
