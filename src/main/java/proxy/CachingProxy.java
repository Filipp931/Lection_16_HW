package proxy;


import storage.Cache;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class CachingProxy implements InvocationHandler {
    private final Object delegate;
    Cache cache;

    public CachingProxy(Object delegate) {
        this.delegate = delegate;
        cache = new Cache(delegate);
    }

    /**
     * Сохраняет имя метода, его аргументы и результат выполнения в кэш
     * при повторном вызове метода проверяет, есть ли результат в кэше
     * если есть, то возвращает кэшированный результат
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(cache.contains(method, args)){
            System.out.println("Getting cash value from cash ");
        } else {
            System.out.println("Caching value");
            cache.cacheValue(method, args, method.invoke(delegate, args));
        }

        return cache.getValue(method, args);
    }

}