import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 */
public class Part1
{
    private enum Direction
    {
        North,
        West,
        East,
        South
    }
    private record Edge(long row, long column) {}

    private record RectangleEdges(long minRow, long maxRow, long minColumn, long maxColumn) {}

    private static void printMap(Set<Edge> E) {
        long r1 = E.stream().map(Edge::row).min(Long::compare).orElseThrow();
        long r2 = E.stream().map(Edge::row).max(Long::compare).orElseThrow();
        long c1 = E.stream().map(Edge::column).min(Long::compare).orElseThrow();
        long c2 = E.stream().map(Edge::column).max(Long::compare).orElseThrow();
        for (long r = r1; r <= r2; r++) {
            StringBuilder row = new StringBuilder();
            for (long c = c1; c <= c2; c++) {
                row.append(E.contains(new Edge(r, c)) ? '#' : '.');
            }
            System.out.println(row);
        }
        System.out.println("=".repeat(80));
    }


    private static final Set<Edge> EDGES = new HashSet<>();
    private static final List<Direction> DIRECTIONS = new LinkedList<>(
            List.of(Direction.North, Direction.South, Direction.West, Direction.East)
    );

    /**
     * Заполняем входные данные
     */
    private static void readInputData(String path) throws IOException
    {
        final List<String> graph = Files.readAllLines(Path.of(path));
        for (int idxRow = 0; idxRow < graph.size(); idxRow++) {
            final String row = graph.get(idxRow);
            for (int idxCh = 0; idxCh < row.length(); idxCh++) {
                final char ch = row.charAt(idxCh);
                if (ch == '#')
                    EDGES.add(new Edge(idxRow, idxCh));
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        readInputData(args.length > 0 ? args[0] : "inp.txt");
        for (int t = 0; t < 10; t++) {
            // ассоциативный массив какая-то координата -> список +эльфов, которые хотят на нее переместиться
            Map<Edge, List<Edge>> possibleMoving = new LinkedHashMap<>();
            for (Edge edge : EDGES) {
                // Если нет соседей, то стоим на месте
                if (isNoNeighbors(edge, Direction.North)
                        && isNoNeighbors(edge, Direction.West)
                        && isNoNeighbors(edge, Direction.East)
                        && isNoNeighbors(edge, Direction.South)
                ) {
                    continue;
                }

                fillPossibleMoving(edge, possibleMoving);
            }
            // first direction the Elves considered is moved to the end of the list of directions
            scrollDirection();
            runMoving(possibleMoving);
        }

        final RectangleEdges rectangleEdges = findRectangleEdges();
        long ans = 0;
        for (long r = rectangleEdges.minRow; r <= rectangleEdges.maxRow; r++)
            for (long c = rectangleEdges.minColumn; c <= rectangleEdges.maxColumn; c++)
                if (!EDGES.contains(new Edge(r,c)))
                    ans++;
        System.out.println(ans);
    }

    private static RectangleEdges findRectangleEdges()
    {
        long minRow = Long.MAX_VALUE;
        long maxRow = Long.MIN_VALUE;
        long minColumn = Long.MAX_VALUE;
        long maxColumn = Long.MIN_VALUE;
        for (Edge edge : EDGES)
        {
            if (edge.row > maxRow)
                maxRow = edge.row;
            else if (edge.row < minRow)
                minRow = edge.row;

            if (edge.column > maxColumn)
                maxColumn = edge.column;
            else if (edge.column < minColumn)
                minColumn = edge.column;
        }
        return new RectangleEdges(minRow, maxRow, minColumn, maxColumn);
    }

    /**
     * Совершить перемещение
     */
    private static void runMoving(Map<Edge, List<Edge>> moving)
    {
        for (Entry<Edge, List<Edge>> entry : moving.entrySet()) {
            if (entry.getValue().size() == 1) {
                EDGES.remove(entry.getValue().get(0));
                EDGES.add(entry.getKey());
            }
        }
    }

    /**
     * Прокрутка направлений.
     */
    private static void scrollDirection()
    {
        final Direction first = DIRECTIONS.get(0);
        DIRECTIONS.remove(0);
        DIRECTIONS.add(first);
    }

    /**
     * Метод для проверки наличия соседей в направлении в одной из сторон света
     *
     * @param edge ребро, которое проверяем
     * @param direction направление стороны света
     * @return есть ли соседи в стороне direction
     */
    private static boolean isNoNeighbors(Edge edge, Direction direction)
    {
        return switch (direction)
                {
                    case North -> !EDGES.contains(new Edge(edge.row - 1, edge.column))
                            && !EDGES.contains(new Edge(edge.row - 1, edge.column - 1))
                            && !EDGES.contains(new Edge(edge.row - 1, edge.column + 1));

                    case West -> !EDGES.contains(new Edge(edge.row, edge.column - 1))
                            && !EDGES.contains(new Edge(edge.row - 1, edge.column - 1))
                            && !EDGES.contains(new Edge(edge.row + 1, edge.column - 1));

                    case East -> !EDGES.contains(new Edge(edge.row, edge.column + 1))
                            && !EDGES.contains(new Edge(edge.row - 1, edge.column + 1))
                            && !EDGES.contains(new Edge(edge.row + 1, edge.column + 1));

                    case South -> !EDGES.contains(new Edge(edge.row + 1, edge.column))
                            && !EDGES.contains(new Edge(edge.row + 1, edge.column - 1))
                            && !EDGES.contains(new Edge(edge.row + 1, edge.column + 1));
                };
    }

    /**
     * Получить желаемое перемещение для эльфа
     * @param edge эльф для которого ищем желаемое перемещение
     */
    private static void fillPossibleMoving(Edge edge, Map<Edge, List<Edge>> possibleMoving)
    {
        boolean moved = false;
        for (Direction direction : DIRECTIONS) {
            if (direction == Direction.North && isNoNeighbors(edge, Direction.North))
            {
                final Edge candidate = new Edge(edge.row - 1, edge.column);
                List<Edge> edges = possibleMoving.getOrDefault(candidate, new LinkedList<>());
                edges.add(edge);
                possibleMoving.put(candidate, edges);
                moved = true;
            }
            else if (direction == Direction.South && isNoNeighbors(edge, direction))
            {
                final Edge candidate = new Edge(edge.row + 1, edge.column);
                List<Edge> edges = possibleMoving.getOrDefault(candidate, new LinkedList<>());
                edges.add(edge);
                possibleMoving.put(candidate, edges);
                moved = true;
            }
            else if (direction == Direction.West && isNoNeighbors(edge, direction))
            {
                final Edge candidate = new Edge(edge.row, edge.column - 1);
                List<Edge> edges = possibleMoving.getOrDefault(candidate, new LinkedList<>());
                edges.add(edge);
                possibleMoving.put(candidate, edges);
                moved = true;
            }
            else if (direction == Direction.East && isNoNeighbors(edge, Direction.East))
            {
                final Edge candidate = new Edge(edge.row, edge.column + 1);
                List<Edge> edges = possibleMoving.getOrDefault(candidate, new LinkedList<>());
                edges.add(edge);
                possibleMoving.put(candidate, edges);
                moved = true;
            }

            if (moved)
                return;
        }
    }
}