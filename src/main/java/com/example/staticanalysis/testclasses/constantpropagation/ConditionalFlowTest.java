package com.example.staticanalysis.testclasses;

public class ConditionalFlowTest {
    public static final int CONSTANT_A = 10;
    public static final int CONSTANT_B = 5;

    public static void compute(int dynamicInput) {
        int x = CONSTANT_A + dynamicInput; // Partially dynamic
        int y = x - CONSTANT_B;            // Depends on dynamicInput
        int z = y * 2;                     // Depends on dynamicInput

        if (z > 10) {
            z = z / 2;                     // Outcome depends on dynamicInput
        } else {
            z = z / 4;                     // Outcome depends on dynamicInput
        }

        System.out.println("Result: " + z);
    }

    public static void main(String[] args) {
        compute(2); // Example dynamic input
    }
}
