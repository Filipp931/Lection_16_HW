package calculator;

import storage.MYDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Calculator implements Calc {
    @Cacheable(MYDB.class)
    @Override
    public List<Integer> fibonacci(int n) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            result.add(fibonacciNum(i));
        }
        try {
            Thread.sleep(new Random().nextInt(4000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    private int fibonacciNum(int n) {
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return fibonacciNum(n - 1) + fibonacciNum(n - 2);
        }
    }
}
