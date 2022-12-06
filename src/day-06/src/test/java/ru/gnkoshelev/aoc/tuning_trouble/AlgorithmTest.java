package ru.gnkoshelev.aoc.tuning_trouble;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Gregory Koshelev
 */
public class AlgorithmTest {
    private static final Algorithm algorithmPartOne = new NaiveAlgorithm(4);
    private static final Algorithm algorithmPartTwo = new NaiveAlgorithm(14);

    private static Stream<Arguments> argsPartOne() {
        return Stream.of(
                Arguments.of("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 7),
                Arguments.of("bvwbjplbgvbhsrlpgdmjqwftvncz", 5),
                Arguments.of("nppdvjthqldpwncqszvftbrmjlhg", 6),
                Arguments.of("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 10),
                Arguments.of("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 11)
        );
    }

    private static Stream<Arguments> argsPartTwo() {
        return Stream.of(
                Arguments.of("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 19),
                Arguments.of("bvwbjplbgvbhsrlpgdmjqwftvncz", 23),
                Arguments.of("nppdvjthqldpwncqszvftbrmjlhg", 23),
                Arguments.of("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 29),
                Arguments.of("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 26)
        );
    }

    @ParameterizedTest
    @MethodSource("argsPartOne")
    public void testPartOne(String input, int result) {
        assertEquals(result, algorithmPartOne.search(input.toCharArray()));
    }

    @ParameterizedTest
    @MethodSource("argsPartTwo")
    public void testPartTwo(String input, int result) {
        assertEquals(result, algorithmPartTwo.search(input.toCharArray()));
    }
}
