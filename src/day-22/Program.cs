// See https://aka.ms/new-console-template for more information

using System.Text.RegularExpressions;
using static System.Math;

var dirs = new V[] { new(1, 0), new(0, 1), new(-1, 0), new(0, -1) };

var parts = File.ReadAllText("input.txt").Split("\n\n");
var map = parts[0].Split('\n');
var pathData = parts[1];
var path = ParsePath();

# region Solution

Console.WriteLine($"Part 1: {SolvePart1()}");
Console.WriteLine($"Part 2: {SolvePart2()}");


int SolvePart1()
{
    var curPos = new V(map[0].IndexOf('.'), 0);
    var curDir = 0;
    var mapSize = Max(map.Length, map.Max(x => x.Length));

    foreach (var (rotate, steps) in path)
    {
        if (rotate == 'L')
            curDir = TurnLeft(curDir);
        else if (rotate == 'R')
            curDir = TurnRight(curDir);
        else
        {
            for (int i = 0; i < steps; i++)
            {
                var nextPos = curPos;
                do
                {
                    nextPos = (nextPos + dirs[curDir]).Mod(mapSize);
                } while (At(nextPos) == default);

                if (At(nextPos) == '#')
                    break;

                curPos = nextPos;
            }
        }
    }

    return GetScore(curPos, curDir);
}

int SolvePart2()
{
    var faceSize = map.Min(x => x.Trim().Length);
    var facesMap = ParseFacesMap();
    var facesStarts = facesMap.Select(f => f * faceSize).ToArray();
    var faceTransitions = BuildFaceTransitions();

    var curFace = 0;
    var curPos = new V(0, 0);
    var curDir = 0;

    foreach (var (rotate, steps) in path)
    {
        if (rotate == 'L')
            curDir = TurnLeft(curDir);
        else if (rotate == 'R')
            curDir = TurnRight(curDir);
        else
        {
            for (int i = 0; i < steps; i++)
            {
                var nextFace = curFace;
                var nextDir = curDir;
                var nextPos = curPos;

                nextPos += dirs[curDir];
                if (!nextPos.InRange(faceSize - 1))
                    (nextFace, nextDir, nextPos) = MakeTransition(curFace, curDir, curPos);

                if (IsWall(nextFace, nextPos))
                    break;

                curFace = nextFace;
                curDir = nextDir;
                curPos = nextPos;
            }
        }
    }

    return GetScore(ToMapPosition(curFace, curPos), curDir);

    (int toFace, int toDir, V to) MakeTransition(int fromFace, int dir, V from)
    {
        var (toFace, toDir) = faceTransitions[(fromFace, dir)];
        var to = (from + dirs[dir]).Mod(faceSize);
        for (var dd = dir; dd != toDir; dd = TurnRight(dd))
            to = RotateRight(to);
        return (toFace, toDir, to);
    }

    V[] ParseFacesMap()
    {
        /*
                ...#
                .#..  <=== tile at (2,0)
                #...
                ....
        ...#.......#
        ........#...  <=== tiles at (0,1); (1,1); (2,1) 
        ..#....#....
        ..........#.
                ...#....
                .....#..  <=== tiles at (2,2) and (3,2)
                .#......
                ......#.
         
         so, result must be = [(2,0), (0,1), (1,1), (2,1), (2,2), (3,2) ]
         */
        var result = new List<V>();
        for (int y = 0; y < 6; y++)
        for (int x = 0; x < 6; x++)
        {
            var v = new V(x, y);
            var pos = v * faceSize;
            if (At(pos) != default)
                result.Add(v);
        }

        return result.ToArray();
    }

    V ToMapPosition(int face, V positionInsideFace) => facesStarts[face] + positionInsideFace;

    bool IsWall(int face, V v) => At(ToMapPosition(face, v)) == '#';

    V RotateRight(V v)
    {
        /* Повернуть координату внутри тайла размера faceSize
         
         
           +-----------+       +-----------+
           |   *       |       |           |
           |           |  -->  |         * |
           |           |       |           |
           |           |       |           |
           |           |       |           |
           +-----------+       +-----------+
           
           ####
           ####
           ####
           ####
           
                    |
                    |
                    |     * (x0, y0)
            --------+-------->
                    |        x
                    |
                    | * (x = -y0, y = x0)
                  y V
           
        */
        var shifted = v * 2 - new V(faceSize - 1, faceSize - 1);
        var rotated = new V(-shifted.Y, shifted.X);
        var shiftedBack = (rotated + new V(faceSize - 1, faceSize - 1)) / 2;
        return shiftedBack;
    }

    Dictionary<(int face, int dir), (int face, int dir)> BuildFaceTransitions()
    {
        var faceAndDirectionToNextFace = new Dictionary<(int face, int dir), int>();
        for (int face = 0; face < facesMap.Length; face++)
        {
            for (int dir = 0; dir < 4; dir++)
            {
                var n = facesMap[face] + dirs[dir];
                var nf = Array.IndexOf(facesMap, n);
                if (nf != -1)
                    faceAndDirectionToNextFace.Add((face, dir), nf);
            }
        }

        var cubeCoords = new Dictionary<int, (V3 coord, int[] rotationDirs)>
        {
            [0] = (new V3(0, 0, 1), Array.Empty<int>())
        };
        var cubeRotations = new Func<V3, V3>[]
        {
            v => v.RotateRight(),
            v => v.RotateDown(),
            v => v.RotateLeft(),
            v => v.RotateUp(),
        };

        while (cubeCoords.Count < facesMap.Length)
        {
            for (int face = 0; face < facesMap.Length; face++)
            {
                if (!cubeCoords.TryGetValue(face, out var cubeCoord))
                    continue;

                for (int dir = 0; dir < 4; dir++)
                {
                    if (faceAndDirectionToNextFace.TryGetValue((face, dir), out var nextFace) &&
                        !cubeCoords.ContainsKey(nextFace))
                    {
                        var rotationDirs = cubeCoord.rotationDirs.Append(Opposite(dir)).ToArray();
                        var coord = new V3(0, 0, 1);
                        foreach (var rotationDir in rotationDirs.Reverse())
                            coord = cubeRotations[Opposite(rotationDir)](coord);
                        cubeCoords[nextFace] = (coord, rotationDirs);
                    }
                }
            }
        }

        for (int face = 0; face < facesMap.Length; face++)
        {
            var rotationDirs = cubeCoords[face].rotationDirs;
            var rotatedCoordToFace = cubeCoords.Select(x =>
            {
                var coord = x.Value.coord;
                foreach (var rotationDir in rotationDirs)
                    coord = cubeRotations[rotationDir](coord);
                return (face: x.Key, coord);
            }).ToDictionary(x => x.coord, x => x.face);

            for (int dir = 0; dir < 4; dir++)
            {
                if (faceAndDirectionToNextFace.ContainsKey((face, dir)))
                    continue;

                var nextCoord = cubeRotations[dir](new V3(0, 0, 1));
                var nextFace = rotatedCoordToFace[nextCoord];
                faceAndDirectionToNextFace.Add((face, dir), nextFace);
            }
        }

        var dirByFacePair = faceAndDirectionToNextFace
            .ToDictionary(
                x => (x.Key.face, x.Value),
                x => x.Key.dir);

        return faceAndDirectionToNextFace
            .ToDictionary(
                x => (x.Key.face, x.Key.dir),
                x => (x.Value, Opposite(dirByFacePair[(x.Value, x.Key.face)])));
    }
}

#endregion

#region Helper methods

int GetScore(V pos, int dir) => (pos.Y + 1) * 1000 + (pos.X + 1) * 4 + dir;

int TurnLeft(int dir) => (dir - 1).Mod(4);
int TurnRight(int dir) => (dir + 1).Mod(4);
int Opposite(int dir) => (dir + 2).Mod(4);

char At(V pos)
{
    if (pos.Y < 0 || pos.Y >= map.Length)
        return default;
    if (pos.X < 0 || pos.X >= map[pos.Y].Length)
        return default;
    var ch = map[pos.Y][pos.X];
    return ch is '#' or '.' ? ch : default;
}

#endregion

# region Parsing

(char rotate, int steps)[] ParsePath()
{
    return new Regex(@"\d+|L|R")
        .Matches(pathData)
        .Select(x => x.Value)
        .Select(x => x switch
        {
            "L" or "R" => (x[0], 0),
            _ => (default, int.Parse(x)),
        })
        .ToArray();
}

# endregion

#region Helpers classes

public record V(int X, int Y)
{
    public static V operator +(V a, V b) => new(a.X + b.X, a.Y + b.Y);
    public static V operator -(V a, V b) => new(a.X - b.X, a.Y - b.Y);
    public static V operator *(V a, int k) => new(a.X * k, a.Y * k);
    public static V operator /(V a, int k) => new(a.X / k, a.Y / k);
    public bool InRange(int range) => X >= 0 && X <= range && Y >= 0 && Y <= range;
    public V Mod(int modulus) => new(X.Mod(modulus), Y.Mod(modulus));
}

public record V3(int X, int Y, int Z)
{
    public static V3 operator +(V3 a, V3 b) => new(a.X + b.X, a.Y + b.Y, a.Z + b.Z);
    public static V3 operator -(V3 a, V3 b) => new(a.X - b.X, a.Y - b.Y, a.Z - b.Z);
    public static V3 operator *(V3 a, int k) => new(a.X * k, a.Y * k, a.Z * k);
    public V3 RotateUp() => new(X, Z, -Y);
    public V3 RotateDown() => new(X, -Z, Y);
    public V3 RotateRight() => new(Z, Y, -X);
    public V3 RotateLeft() => new(-Z, Y, X);
}

public static class Helpers
{
    public static int Mod(this int value, int modulus) => (value % modulus + modulus) % modulus;
}

#endregion