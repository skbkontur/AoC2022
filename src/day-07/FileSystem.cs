namespace Day7;
public class FileSystem
{
    private readonly Directory RootDirectory = new("/", null!);
    internal record File(string Name, int Size);
    public record Directory(string Name, Directory Parent)
    {
        internal readonly List<Directory> ChildDirectories = new();
        internal readonly List<File> Files = new();

        #region Size

        private int? size;
        public int Size => size ??= Files.Sum(file => file.Size) + ChildDirectories.Sum(directory => directory.Size);

        #endregion
        
        #region Tree traversal
        internal IEnumerable<Directory> Flatten()
            => ChildDirectories.SelectMany(directory => directory.Flatten()).Prepend(this);
        #endregion
    }

    public IEnumerable<Directory> AllDirectories => RootDirectory.Flatten();

    public static FileSystem Restore(string[] lines)
    {
        var fileSystem = new FileSystem();
        var currentDirectory = fileSystem.RootDirectory;
        foreach (var line in lines)
        {
            var parts = line.Split(' ', StringSplitOptions.RemoveEmptyEntries);
            switch (parts[0])
            {
                case "$":
                    currentDirectory = parts[1] switch
                    {
                        "cd" => parts[2] switch
                        {
                            "/" => fileSystem.RootDirectory,
                            ".." => currentDirectory.Parent,
                            _ => currentDirectory.ChildDirectories.Single(directory => directory.Name == parts[2])
                        },
                        _ => currentDirectory
                    };

                    break;
                case "dir":
                    currentDirectory.ChildDirectories.Add(new Directory(parts[1], currentDirectory));
                    break;
                default:
                    currentDirectory.Files.Add(new File(parts[1], int.Parse(parts[0])));
                    break;
            }
        }
        return fileSystem;
    }
}