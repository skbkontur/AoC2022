package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner testScanner = new Scanner(new File("src/data/test_input.txt"));
        Scanner scanner = new Scanner(new File("src/data/input.txt"));

        new Task8(testScanner).processInt();
        new Task8(scanner).processInt();
    }
}
