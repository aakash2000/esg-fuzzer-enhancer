package com.example.staticanalysis.testclasses.constantpropagation;

public class TestSwitch {
    public static void main(String[] args) {
        int a = 2;
        switch (a) {
            case 1:
                a = 3;
                break;
            case 2:
                a = 4;
                break;
            default:
                a = 5;
                break;
        }
        System.out.println(a);
    }
}
