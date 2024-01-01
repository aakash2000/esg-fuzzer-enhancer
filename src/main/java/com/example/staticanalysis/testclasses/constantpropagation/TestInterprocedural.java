package com.example.staticanalysis.testclasses;

public class TestInterprocedural {
    static int getConstant() {
        return 10;
    }

    public static void main(String[] args) {
        int a = getConstant();
        int b = a + 5;
        System.out.println(b);
    }
}
