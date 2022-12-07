namespace Day7;

public static class Day7Solution
{
    public static int SolvePart1(string[] lines)
    {
        var fileSystem = FileSystem.Restore(lines);
        return fileSystem
            .AllDirectories
            .Where(directory => directory.Size < 100_000)
            .Sum(directory => directory.Size);
    }

    public static int SolvePart2(string[] lines)
    {
        var fileSystem = FileSystem.Restore(lines);
        var directorySizes = fileSystem
            .AllDirectories
            .Select(directory => directory.Size)
            .ToArray();
        var spaceToFree = 30_000_000 - (70_000_000 - directorySizes[0]);
        return directorySizes.Where(size => size > spaceToFree).Min();
    }
}