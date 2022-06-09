package storage;

import calculator.Cacheable;
import calculator.Calc;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class Cache {
    private final MYDB mydb = MYDB.getInstance();
    private final Map<Method, HashMap<List<Object>, Object>> cache;
    private List<Method> methodsToCache;
    public Cache(Object object) {
        cache = getCache(Arrays.stream(object.getClass().getInterfaces()).filter(Predicate.isEqual(Calc.class)).findAny().get().getDeclaredMethods());
    }

    public boolean contains(Method method, Object[] args){
        return cache.containsKey(method) && cache.get(method).containsKey(Arrays.asList(args));
    }

    public void cacheValue(Method method, Object[] args, Object result) {
        if (!cache.containsKey(method)) {
            cache.put(method, new HashMap<>() {
                {
                    put(Arrays.asList(args), result);
                }
            });
        } else {
            cache.get(method).put(Arrays.asList(args), result);
        }
        if(methodsToCache.contains(method)){
            try {
                mydb.storeToBase(method, args, result);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public Object getValue(Method method, Object[] args){
        return cache.get(method).get(Arrays.asList(args));
    }

    private Map<Method, HashMap<List<Object>, Object>> getCache(Method[] methods){
        Map<Method, HashMap<List<Object>, Object>> result = new ConcurrentHashMap<>();
        scanMethods(methods);
        try {
            result = mydb.getCacheFromTable(methodsToCache);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    private void scanMethods(Method[] methods){
        methodsToCache = new ArrayList<>();
        for (Method method: methods) {
            if (method.isAnnotationPresent(Cacheable.class)){
                Cacheable cacheable = method.getAnnotation(Cacheable.class);
                if (cacheable.value() == MYDB.class) {
                    methodsToCache.add(method);
                }
            }
        }

    }
}
