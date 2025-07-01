// src/test/java/com/example/CalculatorTest.java (Unit Test)
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {
    
    @Test
    public void testAddition() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3));
        System.out.println("✅ Unit Test: Addition passed");
    }
    
    @Test
    public void testSubtraction() {
        Calculator calc = new Calculator();
        assertEquals(1, calc.subtract(3, 2));
        System.out.println("✅ Unit Test: Subtraction passed");
    }
    
    @Test
    public void testMultiplication() {
        Calculator calc = new Calculator();
        assertEquals(6, calc.multiply(2, 3));
        System.out.println("✅ Unit Test: Multiplication passed");
    }
}

// src/test/java/com/example/CalculatorIT.java (Integration Test)
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorIT {
    
    @Test
    public void testComplexCalculation() {
        Calculator calc = new Calculator();
        // Simulate a more complex integration scenario
        int result = calc.add(calc.multiply(2, 3), calc.subtract(10, 5));
        assertEquals(11, result); // (2*3) + (10-5) = 6 + 5 = 11
        System.out.println("✅ Integration Test: Complex calculation passed");
    }
    
    @Test
    public void testDivisionIntegration() {
        Calculator calc = new Calculator();
        assertEquals(2.0, calc.divide(6, 3), 0.001);
        System.out.println("✅ Integration Test: Division passed");
    }
}

// src/main/java/com/example/Calculator.java (Main Class)
package com.example;

public class Calculator {
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return (double) a / b;
    }
}

// src/main/java/com/example/Application.java (Spring Boot Application)
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("🚀 Java Application Started Successfully!");
    }
}