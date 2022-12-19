class Main

const val totalTime = 26

var valves: Map<String, Valve> = emptyMap()
var paths: Map<String, Map<ValveNode, Int>> = emptyMap()

fun main() {
    preprocessInput()

    val bestPressure = partTwoMaxPressure("AA", "AA", 0, 0, emptySet())
    println(bestPressure)
}

fun partOneMaxPressure(atValve: String, minute: Int, openedValves: Set<String>): Int {
    return availablePaths(atValve, openedValves, minute).maxOfOrNull { (node, travelCosts) ->

        val minuteIfOpen = minute + travelCosts + 1

        potentialPressure(node.name, minuteIfOpen) + partOneMaxPressure(node.name, minuteIfOpen, openedValves.plus(node.name))

    } ?: 0
}








fun partTwoMaxPressure(atValve: String, elAtValve: String, minute: Int, elMinute: Int, openedValves: Set<String>): Int {
    val myOptions = availablePaths(atValve, openedValves, minute)
    val elephantOptions = availablePaths(elAtValve, openedValves, elMinute)

    return myOptions.maxOfOrNull { (node, travelCosts) ->
        val minuteIfOpen = minute + travelCosts + 1

        potentialPressure(node.name, minuteIfOpen) + (elephantOptions.minus(node).maxOfOrNull { (elNode, elTravelCosts) ->
            val elMinuteIfOpen = elMinute + elTravelCosts + 1
            potentialPressure(elNode.name, elMinuteIfOpen) + partTwoMaxPressure(
                node.name, elNode.name, minuteIfOpen, elMinuteIfOpen, openedValves.plus(node.name).plus(elNode.name)
            )
        } ?: 0)
    } ?: 0
}

//region Helpers

fun potentialPressure(nodeName: String, minute: Int): Int {
    val valve = valve(nodeName)
    return (totalTime - minute) * valve.flowRate
}

fun availablePaths(valve: String, openedValves: Set<String>, minute: Int): Map<ValveNode, Int> {
    return paths(valve).filter {
        valve(it.key.name).flowRate > 0
        && !openedValves.contains(it.key.name)
        && (it.value + minute + 1) < totalTime
    }
}

fun valve(name: String): Valve {
    return valves.getValue(name)
}

fun paths(name: String): Map<ValveNode, Int> {
    return paths.getValue(name)
}

data class Valve(val name: String, val flowRate: Int, val leadTo: List<String>)

data class ValveNode(val name: String): GenericIntDijkstra.DijkstraNode<ValveNode> {
    lateinit var valvesMap: Map<String, Valve>

    fun usingValves(valvesMap: Map<String, Valve>) = apply { this.valvesMap = valvesMap }

    override fun neighbors(): Map<ValveNode, Int> {
        return valvesMap.getValue(name)
            .leadTo
            .map { ValveNode(it).usingValves(valvesMap) }
            .associateWith { 1 }
    }
}

class ValveDijkstra: GenericIntDijkstra<ValveNode>()

fun preprocessInput() {
    val input = Main::class.java.getResource("input.txt")?.readText()?.split("\n") ?: emptyList()

    valves = input.map(::parseRow).associateBy { it.name }

    val solver = ValveDijkstra()
    paths = valves.values.map { it.name }.associateWith { solver.solve(ValveNode(it).usingValves(valves)) }
}

fun parseRow(input: String): Valve {
    val parts = input.split(";")

    val name = parts[0].substring(6, 8)
    val flowRate = parts[0].substring(23)

    val valveNameRegex = Regex("([A-Z][A-Z])")

    val leadTo = valveNameRegex.findAll(parts[1]).map { x -> x.groupValues.first() }.toList()
    return Valve(name, flowRate.toInt(), leadTo)
}
//endregion