package com.example.staticanalysis.testclasses;

public class TestLoops {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += i;
        }
        System.out.println(sum);
    }
}