package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testApp() {
        assertTrue("Test should pass", true);
    }
    @Test
    public void testAddition() {
        int result = 2 + 3;
        assertEquals("✅ Test #2: Addition test", 5, result);
    }

}