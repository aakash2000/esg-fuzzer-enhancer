package com.example.staticanalysis.testclasses.constantpropagation;

public class ConditionalFlowTest {
    public static final int CONSTANT_A = 10;
    public static final int CONSTANT_B = 5;

    public static void main(String[] args) {
        compute(args.length); // Example dynamic input
    }

    public static void compute(int dynamicInput) {
        int x = CONSTANT_A + dynamicInput; // Partially dynamic 12
        int y = x - CONSTANT_B;            // Depends on dynamicInput 7
        int z = y * 2;                     // Depends on dynamicInput 14

        if (z > 15) {
            z = z / 2;                     // Outcome depends on dynamicInput 7
        } else {
            z = z / 4;                     // Outcome depends on dynamicInput 3
        }

        System.out.println("Result: " + z);
    }

}
