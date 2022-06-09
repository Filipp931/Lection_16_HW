package proxy;

import calculator.Calc;
import calculator.Calculator;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;


public class CachingProxyTest {
    Calc calculator;
    @Before
    public void init(){
        Calc temp = new Calculator();
        CachingProxy cachingProxy = new CachingProxy(temp);
       calculator = (Calc) Proxy.newProxyInstance(temp.getClass().getClassLoader(),
                temp.getClass().getInterfaces(),
                cachingProxy);
    }
    @Test
        public void CachingProxySingleTest() {
        System.out.println(calculator.fibonacci(4));
        System.out.println(calculator.fibonacci(4));
        System.out.println(calculator.fibonacci(6));
    }

}


