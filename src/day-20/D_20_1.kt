import java.io.File

fun main(args: Array<String>) {
    val numbers = File("input.txt").readLines()
        .filter { it.isNotBlank() }
        .mapIndexed { i, it -> i to it.toInt() }
        .toMutableList()
    val n = numbers.size
    for (i in numbers.indices) {
        var index = numbers.indexOfFirst { it.first == i }
        var swapsCount = numbers[index].second

        while (swapsCount > 0) {
            val nextIndex = (index + 1) % n
            val tmp = numbers[index]
            numbers[index] = numbers[nextIndex]
            numbers[nextIndex] = tmp
            index = nextIndex
            swapsCount--
        }

        while (swapsCount < 0) {
            val nextIndex = (index + n - 1) % n
            val tmp = numbers[index]
            numbers[index] = numbers[nextIndex]
            numbers[nextIndex] = tmp
            index = nextIndex
            swapsCount++
        }
    }
    val zeroIndex = numbers.indexOfFirst { it.second == 0 }
    val resultIndices = listOf(zeroIndex + 1000, zeroIndex + 2000, zeroIndex + 3000)
    val result = resultIndices.map { numbers[it % n].second }.toList()
    println(result)
    println(result.sum())
}
