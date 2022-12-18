var parsedInput = ParseInput(File.ReadAllText("input.txt"));

Console.WriteLine($"Part1: {SolvePart1(parsedInput)}");
Console.WriteLine($"Part2: {SolvePart2(parsedInput)}");

#region Input

Point[] ParseInput(string input)
	=> input
		.Split(Environment.NewLine, StringSplitOptions.RemoveEmptyEntries)
		.Select(e => e.Split(","))
		.Select(e => new Point(int.Parse(e[0]), int.Parse(e[1]), int.Parse(e[2])))
		.ToArray();

#endregion

#region Part1

string SolvePart1(Point[] input) => $"{input.Length * 6 - GetPart1Score(input)}";

int GetPart1Score(Point[] input)
{
	var result = 0;

	for (var i = 0; i < input.Length; i++)
	{
		for (var j = i + 1; j < input.Length; j++)
		{
			if (input[i].IsAdjacent(input[j]))
			{
				result += 2;
			}
		}
	}

	return result;
}

#endregion

#region Part2

string SolvePart2(Point[] input) => $"{input.Length * 6 - GetPart1Score(input) - GetPart2Score(input)}";

int GetPart2Score(Point[] input)
{
	var vectorsToNeighbors = new[]
	{
		new Vector(1, 0, 0), new Vector(-1, 0, 0),
		new Vector(0, 1, 0), new Vector(0, -1, 0),
		new Vector(0, 0, 1), new Vector(0, 0, -1)
	};

	var points = input.ToHashSet();

	var minCoordinate = input.SelectMany(e => new[] {e.X, e.Y, e.Z}).Min(e => e);
	var maxCoordinate = input.SelectMany(e => new[] {e.X, e.Y, e.Z}).Max(e => e);

	return points
		.SelectMany(point => vectorsToNeighbors.Select(point.Add))
		.Distinct()
		.Except(points)
		.Where(point =>
			vectorsToNeighbors.All(vector => !CanReachSurface(vector, point, points, minCoordinate, maxCoordinate)))
		.Sum(point => points.Count(point.IsAdjacent));
}

bool CanReachSurface(Vector vector, Point point, HashSet<Point> points,
	int minCoordinate, int maxCoordinate)
{
	while (minCoordinate <= point.X && point.X <= maxCoordinate &&
	       minCoordinate <= point.Y && point.Y <= maxCoordinate &&
	       minCoordinate <= point.Z && point.Z <= maxCoordinate)
	{
		if (points.Contains(point))
		{
			return false;
		}
		point = point.Add(vector);
	}

	return true;
}

#endregion

#region Records

public record Point(int X, int Y, int Z)
{
	public Point Add(Vector vector) => new(X + vector.X, Y + vector.Y, Z + vector.Z);
	public bool IsAdjacent(Point point) => DistanceTo(point) == 1;

	private int DistanceTo(Point point) => Math.Abs(X - point.X) + Math.Abs(Y - point.Y) + Math.Abs(Z - point.Z);
}

public record Vector(int X, int Y, int Z);

#endregion
