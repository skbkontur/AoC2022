package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Tatyana Tokmyanina
 */
public class Task {
    Scanner sc;
    int firstResult;
    int secondResult;
    List<String> lst = new ArrayList<>();

    public Task(Scanner sc) {
        this.sc = sc;
    }

    public void readData(){
        while (sc.hasNextLine()) {
            lst.add(sc.nextLine());
        }
    }

    public void part1Void() {}
    public void part2Void() {}

    public int part1(){
        return firstResult;
    }
    public int part2() {
        return secondResult;
    }

    public void processInt() {
        readData();
        System.out.println(part1());
        System.out.println(part2());
    }

    public void processVoid() {
        readData();
        part1Void();
        part2Void();
    }
}
