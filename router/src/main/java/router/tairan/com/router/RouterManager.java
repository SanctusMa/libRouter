package router.tairan.com.router;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

import router.tairan.com.router.annotation.interceptor.RunInChildThread;
import router.tairan.com.router.annotation.interceptor.RunInMainThread;
import router.tairan.com.router.annotation.interceptor.RouterInterceptor;

public class RouterManager {
    private static final Class[] TARGET_CLASS_START_METHOD_PARAMS = new Class[]{Router.class};
    private static final String TARGET_CLASS_START_METHOD_NAME = "start";

    public static void route(Router router) {
        router = RouterConfig.getInstance().getRedirectAdapter().adapt(router);
        Class<?> clazz = getMatchedClass(router);
        if (null != clazz) {
            //处理全局inceptor
            LinkedList<Class<? extends Interceptor>> list = getInceptorClasses(router, clazz);
            resolveByInceptor(clazz, router, list.iterator());
        }
    }

    @NonNull
    private static LinkedList<Class<? extends Interceptor>> getInceptorClasses(Router router, Class<?> clazz) {
        LinkedList<Class<? extends Interceptor>> list = new LinkedList<>(RouterConfig.getInstance().getInceptorClasses());
        list.addAll(router.getInceptorClasses());
        if (clazz.isAnnotationPresent(RouterInterceptor.class)) {
            for (Class<? extends Interceptor> c : clazz.getAnnotation(RouterInterceptor.class).value()) {
                list.add(c);
            }
        }
        return list;
    }

    private static void resolveByInceptor(final Class<?> targetClass, final Router router, final Iterator<Class<? extends Interceptor>> iterator) {
        try {
            if (iterator.hasNext()) {
                final Class<? extends Interceptor> inceptorClass = iterator.next();
                if (Looper.myLooper() == Looper.getMainLooper()) {//当前在主线程
                    if (inceptorClass.isAnnotationPresent(RunInChildThread.class)) {//如果要求在子线程运行
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handleByInceptor(inceptorClass, router, targetClass, iterator);
                            }
                        }).start();
                    } else {
                        handleByInceptor(inceptorClass, router, targetClass, iterator);
                    }
                } else {//当前在子线程
                    if (inceptorClass.isAnnotationPresent(RunInMainThread.class)) {//要求在主线程
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                handleByInceptor(inceptorClass, router, targetClass, iterator);
                            }
                        });
                    } else {
                        handleByInceptor(inceptorClass, router, targetClass, iterator);
                    }
                }
            } else {
                Method method = targetClass.getMethod(TARGET_CLASS_START_METHOD_NAME, TARGET_CLASS_START_METHOD_PARAMS);
                method.invoke(targetClass, router);
            }
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException) {
                Log.e(">>>", "");
            }
            e.printStackTrace();
        }

    }

    private static void handleByInceptor(Class<? extends Interceptor> inceptorClass, Router router, final Class<?> targetClass, final Iterator<Class<? extends Interceptor>> iterator) {
        try {
            inceptorClass.newInstance().handle(router, new Interceptor.Callback() {
                @Override
                public void next(Router router) {
                    resolveByInceptor(targetClass, router, iterator);
                }
            });
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }


    private static Class getMatchedClass(Router router) {
        for (Class clazz : RouterConfig.getInstance().getClasses()) {
            if (router.match(clazz)) {
                return clazz;
            }
        }
        return null;
    }


}
