import java.io.File

fun getCyclicShift(numbers: List<Pair<Int, Long>>, count: Int): MutableList<Pair<Int, Long>>{
    val shiftedNumbers = numbers.toMutableList()
    for (j in numbers.indices) {
        val newIndex = (j + numbers.size + count) % numbers.size
        shiftedNumbers[newIndex] = numbers[j]
    }
    return shiftedNumbers
}

fun main(args: Array<String>) {
    val decryptionKey = 811589153L
    var numbers = File("input.txt").readLines()
        .filter { it.isNotBlank() }
        .mapIndexed { i, it -> i to it.toLong() * decryptionKey }
        .toMutableList()
    val n = numbers.size
    for (k in 1 .. 10) {
        for (i in numbers.indices) {
            var index = numbers.indexOfFirst { it.first == i }
            var swapsCount = (numbers[index].second % (n * (n - 1))).toInt()
            val shiftSize = swapsCount / n
            val shiftedNumbers = getCyclicShift(numbers.filter { it.first != i }, -shiftSize)
            numbers = shiftedNumbers.take(index).plus(numbers[index]).plus(shiftedNumbers.drop(index)).toMutableList()
            swapsCount %= n

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
    }
    val zeroIndex = numbers.indexOfFirst { it.second == 0L }
    val resultIndices = listOf(zeroIndex + 1000, zeroIndex + 2000, zeroIndex + 3000)
    val result = resultIndices.map { numbers[it % n].second }.toList()
    println(result)
    println(result.sum())
}
