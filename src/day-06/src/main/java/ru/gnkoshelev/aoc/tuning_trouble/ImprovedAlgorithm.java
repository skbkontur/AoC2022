package ru.gnkoshelev.aoc.tuning_trouble;

/**
 * @author Gregory Koshelev
 */
public class ImprovedAlgorithm implements Algorithm {
    private final int bufferSize;

    public ImprovedAlgorithm(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public int search(char[] chars) {
        if (chars.length < bufferSize) {
            throw new IllegalArgumentException("Input length must be at least " + bufferSize);
        }
        window:
        for (int i = bufferSize - 1; i < chars.length; i++) {
            int mask = 0;
            for (int j = i; j > i - bufferSize; j--) {
                if (mask == (mask = mask | (1 << (chars[j] - 'a')))) {
                    continue window;
                }
            }
            return i + 1;
        }
        return -1;
    }
}
