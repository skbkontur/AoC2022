using System;
using System.IO;
using System.Linq;
using FluentAssertions;
using NUnit.Framework;

namespace AoC_2021
{
    [TestFixture]
    public class Task02_2
    {
        [Test]
        [TestCase(
            @"A Y
B X
C Z",
            12)]
        [TestCase(
            @"Task02.txt"
            ,
            11657)]
        public void Task(string input, int expected)
        {
            input = File.Exists(input) ? File.ReadAllText(input) : input;

            var rounds = input.Split("\r\n", StringSplitOptions.RemoveEmptyEntries)
                .Select(x => (Elf: x[0] - 'A', Me: x[2] - 'X'))
                .ToArray();

            var score = 0;
            foreach (var round in rounds)
            {
                var (elf, me) = round;

                score += me * 3;

                score += (elf + (me - 1) + 3) % 3;
                score += 1;
            }

            score.Should().Be(expected);
        }
    }
}