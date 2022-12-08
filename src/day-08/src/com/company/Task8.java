package com.company;

import java.util.Scanner;

/**
 * @author Tatyana Tokmyanina
 */
public class Task8 extends Task {
    int[][] trees;
    boolean[][] visible;

    public Task8(Scanner sc) {
        super(sc);
    }

    @Override
    public void readData() {
        super.readData();
        init();
    }

    @Override
    public int part1() {
        int count = 0;
        count += lookHorizontally();
        count += lookVertically();
        return count;
    }

    @Override
    public int part2() {
        int max = 0;
        for (int i = 0; i < trees.length; i++) {
            for (int j = 0; j < trees[0].length; j++) {
                int result = lookOfTree(i, j);
                if (result > max) {
                    max = result;
                }
            }
        }
        return max;
    }

    private void init() {
        int rows = lst.size();
        int cols = lst.get(0).length();
        trees = new int[rows][cols];
        visible = new boolean[rows][cols];
        int row = 0;
        int col = 0;
        for (String s : lst) {
            for (char cur : s.toCharArray()) {
                trees[row][col] = cur - '0';
                visible[row][col] = false;
                col++;
            }
            col = 0;
            row++;
        }
    }

    private void printTrees() {
        for (int[] tree : trees) {
            for (int j = 0; j < trees[0].length; j++) {
                System.out.print(tree[j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private void printVisible() {
        for (int i = 0; i < trees.length; i++) {
            for (int j = 0; j < trees[0].length; j++) {
                if (visible[i][j]) {
                    System.out.print(1);
                } else {
                    System.out.print(0);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private int lookHorizontally() {
        int count = 0;
        for (int i = 0; i < trees.length; i++) {
            count += lookLeft(i);
            count += lookRight(i);
        }
        return count;
    }

    private int lookVertically() {
        int count = 0;
        for (int i = 0; i < trees[0].length; i++) {
            count += lookUp(i);
            count += lookDown(i);
        }
        return count;
    }

    private int lookOfTree(int x, int y) {
        int result = 1;
        result *= getViewingDistance(0, 1, x, y);
        result *= getViewingDistance(0, -1, x, y);
        result *= getViewingDistance(1, 0, x, y);
        result *= getViewingDistance(-1, 0, x, y);
        return result;
    }

    private int lookLeft(int x) {
        return look(0, 1, x, 0);
    }

    private int lookRight(int x) {
        return look(0, -1, x, trees[0].length - 1);
    }

    private int lookUp(int y) {
        return look(-1, 0, trees.length - 1, y);
    }

    private int lookDown(int y) {
        return look(1, 0, 0, y);
    }

    private int getViewingDistance(int dx, int dy, int x, int y) {
        int curHeight = trees[x][y];
        int score = 0;
        x += dx;
        y += dy;
        while (isCorrectIndex(x, y)) {
            score++;
            if (trees[x][y] >= curHeight) {
                break;
            }
            x += dx;
            y += dy;
        }
        return score;
    }

    private int look(int dx, int dy, int x, int y) {
        int count = 0;
        int max = -1;
        while (isCorrectIndex(x, y)) {
            if (trees[x][y] > max) {
                if (!visible[x][y]) {
                    count++;
                }
                visible[x][y] = true;
                max = trees[x][y];
            }
            x += dx;
            y += dy;
        }
        return count;
    }

    private boolean isCorrectIndex(int x, int y) {
        if (x < 0) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        if (x >= trees.length) {
            return false;
        }
        return y < trees[0].length;
    }
}
