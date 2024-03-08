package com.example.staticanalysis.testclasses.constantpropagation;

public class ConditionalFlowTest {
    public static final int CONSTANT_A = 10;
    public static final int CONSTANT_B = 5;

    public static void main(String[] args) {
        compute(7); // Example dynamic input
    }

    public static void compute(int dynamicInput) {
        int x = CONSTANT_A + dynamicInput; // Partially dynamic 17
        int y = x - CONSTANT_B;            // Partially dynamic 12
        int z = y * 2;                     // Partially dynamic 24

        if (z > 15) {
            z = z / 2;                     // Value is 12
        } else {
            z = z / 4;                     // Value is 6
        }

        System.out.println("z: " + z);
    }

}
