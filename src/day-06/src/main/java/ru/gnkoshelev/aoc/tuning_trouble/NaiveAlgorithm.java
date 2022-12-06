package ru.gnkoshelev.aoc.tuning_trouble;

/**
 * @author Gregory Koshelev
 */
public class NaiveAlgorithm implements Algorithm {
    private final int bufferSize;

    public NaiveAlgorithm(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public int search(char[] chars) {
        if (chars.length < bufferSize) {
            throw new IllegalArgumentException("Input length must be at least " + bufferSize);
        }
        window:
        for (int i = bufferSize - 1; i < chars.length; i++) {
            for (int j = i; j > i - bufferSize + 1; j--) {
                for (int k = j - 1; k >= i - bufferSize + 1; k--) {
                    if (chars[j] == chars[k]) {
                        continue window;
                    }
                }
            }
            return i + 1;
        }
        return -1;
    }
}
