using System.Diagnostics;
using System.Numerics;

Monkey ParseMonkey(string input)
{
    var splitResult = input.Split(':', ' ')
        .Where(x => !string.IsNullOrEmpty(x))
        .ToArray();
    var name = splitResult[0];

    if (splitResult.Length == 2)
        return new ValueMonkey(name, int.Parse(splitResult[1]));

    var left = splitResult[1];
    var right = splitResult[3];
    var operation = GetOperation(splitResult[2]);
    return new OperationMonkey(name, left, right, operation);
}

Func<BigInteger, BigInteger, BigInteger> GetOperation(string op)
{
    return op switch
    {
        "-" => (a, b) => a - b,
        "+" => (a, b) => a + b,
        "/" => (a, b) => a / b,
        "*" => (a, b) => a * b,
        _ => throw new Exception($"Unknown operation {op}")
    };
}

BigInteger MonkeyValue(Dictionary<string, Monkey> monkeys, string monkeyName)
{
    var monkey = monkeys[monkeyName];
    return monkey switch
    {
        ValueMonkey valueMonkey => valueMonkey.Value,
        OperationMonkey operationMonkey => operationMonkey.Operation(
            MonkeyValue(monkeys, operationMonkey.Left),
            MonkeyValue(monkeys, operationMonkey.Right)
        ),
        _ => throw new Exception($"Unknown monkey type {monkey.GetType()}")
    };
}

var monkeys = File.ReadAllLines("input.txt")
    .Select(ParseMonkey)
    .ToDictionary(x => x.Name);

#region Easy

var swEasy = Stopwatch.StartNew();
Console.WriteLine(MonkeyValue(monkeys, "root"));
Console.WriteLine($"Elapsed {swEasy.Elapsed}");

#endregion

#region Hard

var swHard = Stopwatch.StartNew();
var rootMonkey = (OperationMonkey)monkeys["root"];
var me = (ValueMonkey)monkeys["humn"];
rootMonkey.Operation = (a, b) => a.CompareTo(b);
var left = 0L;
var right = long.MaxValue / 2;
while (left + 1 < right)
{
    var test = (left + right) / 2;
    me.Value = test;
    // > and < may vary from input to input
    // since some inputs define increasing function and some define decreasing one
    if (MonkeyValue(monkeys, "root") > 0)
        left = test;
    else
        right = test;
}

Console.WriteLine(right);
Console.WriteLine($"Elapsed {swHard.Elapsed}");

#endregion

public abstract class Monkey
{
    protected Monkey(string name)
    {
        Name = name;
    }

    public string Name { get; }
}

public class ValueMonkey : Monkey
{
    public BigInteger Value { get; set; }

    public ValueMonkey(string name, BigInteger value)
        : base(name)
    {
        Value = value;
    }
}

public class OperationMonkey : Monkey
{
    public string Left { get; }
    public string Right { get; }
    public Func<BigInteger, BigInteger, BigInteger> Operation { get; set; }

    public OperationMonkey(
        string name,
        string left,
        string right,
        Func<BigInteger, BigInteger, BigInteger> operation
    ) : base(name)
    {
        Left = left;
        Right = right;
        Operation = operation;
    }
}