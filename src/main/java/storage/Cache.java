package storage;

import calculator.Cacheable;
import storage.MYDB;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

public class Cache {
    private MYDB mydb = MYDB.getInstance();
    private final Map<Method, HashMap<List<Object>, Object>> cache;

    public Cache(Object object) {
        cache = getCache(object.getClass().getInterfaces()[0].getMethods());
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
    }

    public Object getValue(Method method, Object[] args){
        return cache.get(method).get(Arrays.asList(args));
    }

    private Map<Method, HashMap<List<Object>, Object>> getCache(Method[] methods){
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
}
