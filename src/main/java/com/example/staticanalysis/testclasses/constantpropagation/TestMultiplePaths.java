package com.example.staticanalysis.testclasses.constantpropagation;

public class TestMultiplePaths {
    public static void main(String[] args) {
        int a = args.length;
        int b;
        if (a == 5) {
            b = 10;
        } else {
            b = 20;
        }
        System.out.println(b);
    }
}
