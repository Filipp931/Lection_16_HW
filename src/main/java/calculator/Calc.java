package calculator;

import storage.MYDB;

import java.util.List;

public interface Calc {
    @Cacheable(MYDB.class)
    List<Integer> fibonacci(int n);
}
