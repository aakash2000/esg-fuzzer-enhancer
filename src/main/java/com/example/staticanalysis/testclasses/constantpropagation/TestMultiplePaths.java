package com.example.staticanalysis.testclasses;

public class TestMultiplePaths {
    public static void main(String[] args) {
        int a = 5;
        int b;
        if (a == 5) {
            b = 10;
        } else {
            b = 20;
        }
        System.out.println(b);
    }
}
