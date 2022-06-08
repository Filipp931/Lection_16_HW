package proxy;

import calculator.Cacheable;
import storage.MYDB;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

public class CachingProxy implements InvocationHandler {
    private final Object delegate;
    MYDB mydb = MYDB.getInstance();
    private final Map<Method, HashMap<List<Object>, Object>> cache;

    public CachingProxy(Object delegate) {
        this.delegate = delegate;
        cache = checkAllMethodsAndGetCache(delegate.getClass().getInterfaces()[0].getDeclaredMethods());
    }

    private Map<Method, HashMap<List<Object>, Object>> checkAllMethodsAndGetCache(Method[] methods){
        Map<Method, HashMap<List<Object>, Object>> result = new HashMap<>();
        List<Method> methodsToCache = new ArrayList<>();
        for (Method method: methods) {
            if (method.isAnnotationPresent(Cacheable.class)){
                Cacheable cacheable = method.getAnnotation(Cacheable.class);
                if (cacheable.value() == MYDB.class) {
                    methodsToCache.add(method);
                }
            }
        }
        try {
           result = mydb.getCacheFromTable(methodsToCache);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    /**
     * Сохраняет имя метода, его аргументы и результат выполнения в кэш
     * при повторном вызове метода проверяет, есть ли результат в кэше
     * если есть, то возвращает кэшированный результат
     */
    @Override

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(cache.containsKey(method) && cache.get(method).containsKey(Arrays.asList(args)) ) {
            Object result = cache.get(method).get(Arrays.asList(args));
            System.out.println("Getting cash value from cash ");
            return result;
        } else {
            if (!cache.containsKey(method)) {
                cache.put(method, new HashMap<>() {
                    {
                        put(Arrays.asList(args), method.invoke(delegate, args));
                    }
                });
            } else {
                cache.get(method).put(Arrays.asList(args), method.invoke(delegate, args));
            }
            System.out.println("caching value");
            mydb.storeToBase(method, args, cache.get(method).get(Arrays.asList(args)));
            return cache.get(method).get(Arrays.asList(args));
        }
    }

}