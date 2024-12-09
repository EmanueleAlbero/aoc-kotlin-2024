package common

import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList

data class Item<T>(
    val x: Int,
    val y: Int,
    val direction: Direction?,
    val value: T
)

enum class Algorithm {
    BFS,
    DFS,
    DIJKSTRA,
    ASTAR,
    ALL_PATHS_DFS
}

@Suppress("UNCHECKED_CAST")
abstract class Grid2D<T>(
    input: List<String>,
    private val mapper: (Char) -> T
) {
    private val data: Array<Array<Any?>>
    val width: Int
    val height: Int = input.size
    private var currentX: Int = 0
    private var currentY: Int = 0

    init {
        width = if (height > 0) input[0].length else 0

        data = Array(height) { y ->
            arrayOfNulls<Any?>(width).apply {
                for (x in 0 until width) {
                    this[x] = mapper(input[y][x])
                }
            }
        }
    }

    open fun getCurrentElement(): T? {
        return getElementAt(currentX, currentY)
    }

    open fun getElementAt(x: Int, y: Int): T? {
        return if (isValidPosition(x, y)) {
            data[y][x] as T
        } else {
            null
        }
    }

    fun canMove(direction: Direction): Boolean {
        return when (direction) {
            Direction.UP -> currentY > 0
            Direction.DOWN -> currentY < height - 1
            Direction.LEFT -> currentX > 0
            Direction.RIGHT -> currentX < width - 1
            Direction.UP_LEFT -> currentY > 0 && currentX > 0
            Direction.UP_RIGHT -> currentY > 0 && currentX < width - 1
            Direction.DOWN_LEFT -> currentY < height - 1 && currentX > 0
            Direction.DOWN_RIGHT -> currentY < height - 1 && currentX < width - 1
        }
    }

    fun move(direction: Direction): Boolean {
        if (!canMove(direction)) return false

        when (direction) {
            Direction.UP -> currentY--
            Direction.DOWN -> currentY++
            Direction.LEFT -> currentX--
            Direction.RIGHT -> currentX++
            Direction.UP_LEFT -> {
                currentY--
                currentX--
            }
            Direction.UP_RIGHT -> {
                currentY--
                currentX++
            }
            Direction.DOWN_LEFT -> {
                currentY++
                currentX--
            }
            Direction.DOWN_RIGHT -> {
                currentY++
                currentX++
            }
        }
        return true
    }

    fun traverse(
        fromX: Int, fromY: Int,
        toX: Int, toY: Int,
        action: (Int, Int, T) -> Unit
    ) {
        val startX = minOf(fromX, toX).coerceIn(0, width - 1)
        val endX = maxOf(fromX, toX).coerceIn(0, width - 1)
        val startY = minOf(fromY, toY).coerceIn(0, height - 1)
        val endY = maxOf(fromY, toY).coerceIn(0, height - 1)

        for (y in startY..endY) {
            for (x in startX..endX) {
                action(x, y, data[y][x] as T)
            }
        }
    }

    fun getCurrentPosition(): Pair<Int, Int> {
        return Pair(currentX, currentY)
    }

    fun setPosition(x: Int, y: Int):Boolean =
        if (isValidPosition(x, y)) {
            currentX = x
            currentY = y
            true
        } else {
            false
        }

    private fun isValidPosition(x: Int, y: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }

    open fun getNeighbors(directions: List<Direction>): List<Item<T>> {
        val neighbours = mutableListOf<Item<T>>()

        for (direction in directions) {
            val newX = currentX + dx(direction)
            val newY = currentY + dy(direction)
            if (isValidPosition(newX, newY)) {
                val value = data[newY][newX] as T
                neighbours.add(Item(newX, newY, direction, value))
            }
        }
        return neighbours
    }

    open fun getRow(y: Int): List<Item<T>> {
        val row = mutableListOf<Item<T>>()

        for (x in 0 until width) {
            row.add(Item(x, y, null, data[y][x] as T))
        }
        return row
    }

    open fun getColumn(x: Int): List<Item<T>> {
        val column = mutableListOf<Item<T>>()

        for (y in 0 until height) {
            column.add(Item(x, y, null, data[y][x] as T))
        }
        return column
    }

    private fun dx(direction: Direction): Int {
        return when (direction) {
            Direction.LEFT -> -1
            Direction.RIGHT -> 1
            else -> 0
        }
    }

    private fun dy(direction: Direction): Int {
        return when (direction) {
            Direction.UP -> -1
            Direction.DOWN -> 1
            else -> 0
        }
    }

    open fun find(c: T): Pair<Int, Int>? {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (data[y][x] == c) {
                    return x to y
                }
            }
        }
        return null
    }

    fun findAll(finder: (T) -> Boolean): Sequence<Pair<Int, Int>> = sequence {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (finder(data[y][x] as T)) {
                    yield(x to y)
                }
            }
        }
    }

    fun findAllItems(finder: (T) -> Boolean): Sequence<Item<T>> = sequence {
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (finder(data[y][x] as T)) {
                    yield(Item(
                        x,
                        y,
                        null,
                        (data[y][x] as T)
                    ))
                }
            }
        }
    }

    fun findPath(
        startPosition: List<Pair<Int, Int>>,
        endPosition: List<Pair<Int, Int>>,
        algorithm: Algorithm,
        moveValidator: (T, T) -> Boolean,
        costFunction: ((T, T) -> Double)? = null,
        heuristic: ((Int, Int, Int, Int) -> Double)? = null
        ): List<Pair<Int, Int>> {
        when (algorithm) {
            Algorithm.BFS -> {
                return findPathBFS(startPosition, endPosition, moveValidator)
            }
            Algorithm.DFS -> {
                return findPathDFS(startPosition, endPosition, moveValidator)
            }
            Algorithm.DIJKSTRA -> {
                if (costFunction == null) {
                    throw IllegalArgumentException("Cost function must be provided for Dijkstra's algorithm")
                }
                return findPathDijkstra(startPosition, endPosition, moveValidator, costFunction)
            }
            Algorithm.ASTAR -> {
                if (costFunction == null || heuristic == null) {
                    throw IllegalArgumentException("Cost function and heuristic must be provided for A* algorithm")
                }
                return findPathAStar(startPosition, endPosition, moveValidator, costFunction, heuristic)
            }
            Algorithm.ALL_PATHS_DFS -> {
                val allPaths = mutableListOf<List<Pair<Int, Int>>>()
                val memoizationCache = mutableMapOf<Pair<Int, Int>, List<List<Pair<Int, Int>>>>()
                for (start in startPosition) {
                    val paths = findAllPathsDFS(
                        start,
                        endPosition.first(),
                        moveValidator,
                        mutableSetOf(),
                        mutableListOf(),
                        memoizationCache
                    )
                    allPaths.addAll(paths)
                }
                val bestPath = findBestPath(allPaths)
                return bestPath ?: emptyList()
            }
            else -> {
                return emptyList()
            }
        }
    }

    fun findBestPath(allPaths: List<List<Pair<Int, Int>>>): List<Pair<Int, Int>>? {
        return allPaths.minByOrNull { it.size }
    }

    private fun findPathBFS(
        startPosition: List<Pair<Int, Int>>,
        endPosition: List<Pair<Int, Int>>,
        moveValidator: (T, T) -> Boolean,
    ): List<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

        startPosition.forEach {
            visited.add(it)
            queue.add(it)
            cameFrom[it] = null
        }

        while (queue.isNotEmpty()) {
            val (x, y) = queue.removeFirst()

            if (endPosition.contains(x to y)) {
                val path = mutableListOf<Pair<Int, Int>>()
                var current: Pair<Int, Int>? = x to y
                while (current != null) {
                    path.add(current)
                    current = cameFrom[current]
                }
                return path.asReversed()
            }

            setPosition(x, y)
            val neighbors = getNeighbors(allowedDirections)
            for (neighbor in neighbors) {
                val nextPosition = neighbor.x to neighbor.y
                if (neighbor.value != null && !visited.contains(nextPosition)) {
                    if (!moveValidator(neighbor.value, data[y][x] as T)) {
                        continue
                    }
                    visited.add(nextPosition)
                    queue.add(nextPosition)
                    cameFrom[nextPosition] = x to y
                }
            }
        }
        return emptyList()
    }

    private fun findPathDFS(
        startPosition: List<Pair<Int, Int>>,
        endPosition: List<Pair<Int, Int>>,
        moveValidator: (T, T) -> Boolean
    ): List<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val stack = ArrayDeque<Pair<Int, Int>>()
        val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

        startPosition.forEach {
            stack.add(it)
            cameFrom[it] = null
        }

        while (stack.isNotEmpty()) {
            val (x, y) = stack.removeLast()

            if (visited.contains(x to y)) continue
            visited.add(x to y)

            if (endPosition.contains(x to y)) {
                val path = mutableListOf<Pair<Int, Int>>()
                var current: Pair<Int, Int>? = x to y
                while (current != null) {
                    path.add(current)
                    current = cameFrom[current]
                }
                return path.asReversed()
            }

            setPosition(x, y)
            val neighbors = getNeighbors(allowedDirections)
            for (neighbor in neighbors) {
                val nextPosition = neighbor.x to neighbor.y
                val valuea = getElementAt(x, y)!!
                val valueb = neighbor.value
                val isMoveValid = moveValidator(valuea, valueb)
                if (!visited.contains(nextPosition) && isMoveValid) {
                    stack.add(nextPosition)
                    cameFrom[nextPosition] = x to y
                }
            }
        }
        return emptyList()
    }

    private fun findAllPathsDFS(
        currentPosition: Pair<Int, Int>,
        endPosition: Pair<Int, Int>,
        moveValidator: (T, T) -> Boolean,
        visited: MutableSet<Pair<Int, Int>>,
        currentPath: MutableList<Pair<Int, Int>>,
        memoizationCache: MutableMap<Pair<Int, Int>, List<List<Pair<Int, Int>>>>
    ): List<List<Pair<Int, Int>>> {
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
        // Controlla se i percorsi da questa posizione sono già stati calcolati
        if (memoizationCache.containsKey(currentPosition)) {
            return memoizationCache[currentPosition]!!
        }

        val pathsFromCurrent = mutableListOf<List<Pair<Int, Int>>>()

        currentPath.add(currentPosition)
        visited.add(currentPosition)

        if (currentPosition == endPosition) {
            pathsFromCurrent.add(ArrayList(currentPath))
        } else {
            val (x, y) = currentPosition
            setPosition(x, y)
            val neighbors = getNeighbors(allowedDirections)
            for (neighbor in neighbors) {
                val nextPosition = neighbor.x to neighbor.y
                if (!visited.contains(nextPosition) && moveValidator(getElementAt(x, y)!!, neighbor.value)) {
                    val pathsFromNeighbor = findAllPathsDFS(
                        nextPosition,
                        endPosition,
                        moveValidator,
                        visited,
                        currentPath,
                        memoizationCache
                    )
                    for (path in pathsFromNeighbor) {
                        val newPath = ArrayList(currentPath)
                        newPath.addAll(path.drop(currentPath.size))
                        pathsFromCurrent.add(newPath)
                    }
                }
            }
        }

        currentPath.removeAt(currentPath.size - 1)
        visited.remove(currentPosition)

        // Memorizza i percorsi calcolati nella cache
        memoizationCache[currentPosition] = pathsFromCurrent

        return pathsFromCurrent
    }

    private fun findPathDijkstra(
        startPosition: List<Pair<Int, Int>>,
        endPosition: List<Pair<Int, Int>>,
        moveValidator: (T, T) -> Boolean,
        costFunction: (T, T) -> Double
    ): List<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val distances = mutableMapOf<Pair<Int, Int>, Double>()
        val queue = PriorityQueue<Pair<Int, Int>>(compareBy { distances[it] ?: Double.MAX_VALUE })
        val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

        startPosition.forEach {
            distances[it] = 0.0
            queue.add(it)
            cameFrom[it] = null
        }

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val (x, y) = current

            if (visited.contains(current)) continue
            visited.add(current)

            if (endPosition.contains(current)) {
                val path = mutableListOf<Pair<Int, Int>>()
                var c: Pair<Int, Int>? = current
                while (c != null) {
                    path.add(c)
                    c = cameFrom[c]
                }
                return path.asReversed()
            }

            setPosition(x, y)
            val neighbors = getNeighbors(allowedDirections)
            for (neighbor in neighbors) {
                val nextPosition = neighbor.x to neighbor.y
                if (!moveValidator(getElementAt(x, y)!!, neighbor.value)) continue

                val newDist = distances[current]!! + costFunction(getElementAt(x, y)!!, neighbor.value)
                if (newDist < (distances[nextPosition] ?: Double.MAX_VALUE)) {
                    distances[nextPosition] = newDist
                    cameFrom[nextPosition] = current
                    queue.add(nextPosition)
                }
            }
        }
        return emptyList()
    }

    private fun findPathAStar(
        startPosition: List<Pair<Int, Int>>,
        endPosition: List<Pair<Int, Int>>,
        moveValidator: (T, T) -> Boolean,
        costFunction: (T, T) -> Double,
        heuristic: (Int, Int, Int, Int) -> Double
    ): List<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val gScore = mutableMapOf<Pair<Int, Int>, Double>()
        val fScore = mutableMapOf<Pair<Int, Int>, Double>()
        val queue = PriorityQueue<Pair<Int, Int>>(compareBy { fScore[it] ?: Double.MAX_VALUE })
        val cameFrom = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>?>()
        val allowedDirections = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)

        val endPos = endPosition.first()
        val endX = endPos.first
        val endY = endPos.second

        startPosition.forEach {
            gScore[it] = 0.0
            fScore[it] = heuristic(it.first, it.second, endX, endY)
            queue.add(it)
            cameFrom[it] = null
        }

        while (queue.isNotEmpty()) {
            val current = queue.poll()
            val (x, y) = current

            if (visited.contains(current)) continue
            visited.add(current)

            if (endPosition.contains(current)) {
                val path = mutableListOf<Pair<Int, Int>>()
                var c: Pair<Int, Int>? = current
                while (c != null) {
                    path.add(c)
                    c = cameFrom[c]
                }
                return path.asReversed()
            }

            setPosition(x, y)
            val neighbors = getNeighbors(allowedDirections)
            for (neighbor in neighbors) {
                val nextPosition = neighbor.x to neighbor.y
                if (!moveValidator(getElementAt(x, y)!!, neighbor.value)) continue

                val tentativeGScore = gScore[current]!! + costFunction(getElementAt(x, y)!!, neighbor.value)
                if (tentativeGScore < (gScore[nextPosition] ?: Double.MAX_VALUE)) {
                    cameFrom[nextPosition] = current
                    gScore[nextPosition] = tentativeGScore
                    fScore[nextPosition] = tentativeGScore + heuristic(neighbor.x, neighbor.y, endX, endY)
                    if (!visited.contains(nextPosition)) {
                        queue.add(nextPosition)
                    }
                }
            }
        }
        return emptyList()
    }

    open fun setValue(first: Int, second: Int, c: T) {
        data[second][first] = c
    }

    fun fold(
        fromX: Int, fromY: Int,
        toX: Int, toY: Int,
        initialValue: Any,
        action: (Int, Int, Any) -> Any
    ): Any {
        val startX = minOf(fromX, toX).coerceIn(0, width - 1)
        val endX = maxOf(fromX, toX).coerceIn(0, width - 1)
        val startY = minOf(fromY, toY).coerceIn(0, height - 1)
        val endY = maxOf(fromY, toY).coerceIn(0, height - 1)
        var accumulator = initialValue

        for (y in startY..endY) {
            for (x in startX..endX) {
                accumulator = action(x, y, accumulator)
            }
        }

        return accumulator
    }

    protected fun getChunk(x: Int, y: Int, size: Int, direction: Direction):List<T> {
        setPosition(x,y)
        val chunk = mutableListOf<T>()
        chunk.add(getElementAt(x, y)!!)
        repeat(size-1){
            if (move(direction)) {
                chunk.add(data[currentY][currentX] as T)
            }
        }
        return chunk.toList()
    }

    protected fun getChunk(size: Int, direction: Direction):List<T> = getChunk(currentX, currentY, size, direction)
}
