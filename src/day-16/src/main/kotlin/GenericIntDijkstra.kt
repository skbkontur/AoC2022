import GenericIntDijkstra.GenericNodeWithCost

abstract class GenericIntDijkstra<Node : GenericIntDijkstra.DijkstraNode<Node>> :
    Dijkstra<Node, Int, GenericNodeWithCost<Node>> {
    interface DijkstraNode<Node> {
        fun neighbors(): Map<Node, Int>
    }

    data class GenericNodeWithCost<Node : DijkstraNode<Node>>(val node: Node, val cost: Int) :
        DijkstraNodeWithCost<Node, Int> {
        override fun neighbors() = node.neighbors().map { GenericNodeWithCost(it.key, it.value) }
        override fun compareTo(other: DijkstraNodeWithCost<Node, Int>) = cost.compareTo(other.cost())
        override fun node() = node
        override fun cost() = cost
    }

    override fun Int.plus(cost: Int) = this + cost
    override fun maxCost() = Int.MAX_VALUE
    override fun minCost() = 0

    override fun Node.withCost(cost: Int) = GenericNodeWithCost(this, cost)
}