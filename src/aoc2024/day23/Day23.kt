package aoc2024.day23

import common.benchmarkTime
import common.println
import common.readInput

class Node(
    val name: String,
    var linkedNodes: ArrayDeque<Node> = ArrayDeque(),
)

class Graph(
    var nodes: MutableMap<String, Node> = mutableMapOf()
)

fun main() {

    fun part1(input: List<String>): Int {
        val connections = mutableSetOf<Pair<String, String>>()
        input.forEach {
            val (left, right) = it.split("-")
            connections.add(left to right)
            connections.add(right to left)
        }

        val result: HashSet<String> = hashSetOf()
        connections
            .filter { it.first.startsWith("t") }
            .forEach { connection ->
                connections
                    .filter { it.first == connection.second }
                    .forEach { otherConnection ->
                        val a = connection.first
                        val b = connection.second
                        val c = otherConnection.second

                        if (connections.contains(a to c) && connections.contains(b to c)) {
                            val triplet = listOf(a, b, c).sorted().joinToString("-")
                            result.add(triplet)
                        }
                    }
            }

        return result.size
    }

    fun part2(input: List<String>): String {
        val graph = Graph()

        input.map {
            val (left, right) = it.split("-")
            val nodeLeft = graph.nodes.getOrPut(left) { Node(left) }
            val nodeRight = graph.nodes.getOrPut(right) { Node(right) }
            nodeLeft.linkedNodes.add(nodeRight)
            nodeRight.linkedNodes.add(nodeLeft)
        }

        fun bronKerbosch(
            R: MutableSet<Node>,
            P: MutableSet<Node>,
            X: MutableSet<Node>,
            cliques: MutableList<Set<Node>>
        ) {
            if (P.isEmpty() && X.isEmpty()) {
                cliques.add(R.toSet())
                return
            }

            val pivot = (P union X).firstOrNull() ?: return
            val nonNeighbors = P.filter { it !in pivot.linkedNodes }

            for (v in nonNeighbors) {
                val newR = R.toMutableSet()
                newR.add(v)

                val newP = P.intersect(v.linkedNodes).toMutableSet()
                val newX = X.intersect(v.linkedNodes).toMutableSet()

                bronKerbosch(newR, newP, newX, cliques)

                P.remove(v)
                X.add(v)
            }
        }

        val allNodes = graph.nodes.values.toMutableSet()
        val R = mutableSetOf<Node>()
        val P = allNodes.toMutableSet()
        val X = mutableSetOf<Node>()
        val cliques = mutableListOf<Set<Node>>()

        bronKerbosch(R, P, X, cliques)
        val maxClique = cliques.maxByOrNull { it.size }

        if (maxClique != null) {
            val sortedNames = maxClique.map { it.name }.sorted()
            return sortedNames.joinToString(",")
        }

        return ""
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("aoc2024/Day23_test")
    val part1Result = part1(testInput)
    part1Result.println()
    check(part1Result == 7)

    val part2Result = part2(testInput)
    part2Result.println()
    check(part2Result == "co,de,ka,ta")

    val input = readInput("aoc2024/Day23")
    benchmarkTime("part1") {
        part1(input)
    }

    benchmarkTime("part2") {
        part2(input)
    }
}