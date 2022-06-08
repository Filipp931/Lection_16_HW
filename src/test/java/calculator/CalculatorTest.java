package calculator;

import org.junit.Test;

import static org.junit.Assert.*;

public class CalculatorTest {
    @Test
    public void CalcTest(){
        Calculator calculator = new Calculator();
        System.out.println(calculator.fibonacci(4));
    }

}