using FluentAssertions;
using NUnit.Framework;

namespace Day7;

public class Day7SolutionShould

{
    [TestCaseSource(nameof(GetPart1TestData))]
    public void SolvePart1(string inputFileName, int expectedResult)
    {
        var lines = File.ReadAllLines(inputFileName);
        Day7Solution.SolvePart1(lines).Should().Be(expectedResult);
    }

    [TestCaseSource(nameof(GetPart2TestData))]
    public void SolvePart2(string inputFileName, int expectedResult)
    {
        var lines = File.ReadAllLines(inputFileName);
        Day7Solution.SolvePart2(lines).Should().Be(expectedResult);
    }

    private static IEnumerable<TestCaseData> GetPart1TestData()
    {
        yield return new TestCaseData("sample.txt", 95437).SetName("On sample");
        yield return new TestCaseData("input.txt", 1845346).SetName("On personal input");
    }

    private static IEnumerable<TestCaseData> GetPart2TestData()
    {
        yield return new TestCaseData("sample.txt", 24933642).SetName("On sample");
        yield return new TestCaseData("input.txt", 3636703).SetName("On personal input");
    }
}