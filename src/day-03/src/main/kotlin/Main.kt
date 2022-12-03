import java.io.File
import java.io.InputStream
import java.util.*

fun main(args: Array<String>) {
    val inputStream: InputStream = File("src/main/assets/input.txt").inputStream()
    val input = inputStream.bufferedReader().readLines()

    firstStar(input)
    secondStar(input)
}

fun firstStar(input: List<String>) {
    var result = 0
    input.forEach { line ->
        val first = line.substring(0, line.length / 2)
        val second = line.substring(line.length / 2, line.length)
        val set = first.toHashSet()

        for (each in second) {
            if (set.contains(each)) {
                result += calculatePriority(each)
                break
            }
        }
    }

    println(result)
}

fun secondStar(input: List<String>) {
    var result = 0
    var set: Set<Char> = hashSetOf()
    input.forEachIndexed { index, line ->
        when (index % 3) {
            0 -> set = line.toHashSet()
            1 -> set = line.toHashSet().intersect(set)
            2 -> for (each in line) {
                if (set.contains(each)) {
                    result += calculatePriority(each)
                    break
                }
            }
        }
    }

    println(result)
}

fun calculatePriority(each: Char): Int {
    return if (each.isLowerCase()) {
        each - 'a' + 1
    } else {
        each - 'A' + 27
    }
}
