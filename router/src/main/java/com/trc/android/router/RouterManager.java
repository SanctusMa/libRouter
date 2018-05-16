package com.trc.android.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.trc.android.router.annotation.interceptor.RouterInterceptor;
import com.trc.android.router.annotation.interceptor.RunInChildThread;
import com.trc.android.router.annotation.interceptor.RunInMainThread;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;


public class RouterManager {
    private static final Class[] TARGET_CLASS_START_METHOD_PARAMS = new Class[]{Router.class};
    private static final String TARGET_CLASS_START_METHOD_NAME = "start";

    /**
     * @param router
     * @return true 是否找到了处理Router的类
     */
    public static boolean route(Router router) {
        router = RouterConfig.getInstance().getRedirectAdapter().adapt(router);
        Class<?> clazz = getMatchedClass(router);
        if (null != clazz) {
            LinkedList<Class<? extends Interceptor>> list = getInceptorClasses(router, clazz);
            resolveByInterceptor(clazz, router, list.iterator());
            return true;
        } else {
            RouterHandler noTargetHandler = RouterConfig.getInstance().getNoTargetHandler();
            if (null != noTargetHandler) {
                noTargetHandler.handle(router);
                return true;
            } else {
                Router.Callback callback = router.getCallback();
                if (callback != null) {
                    callback.onResult(false, null);
                }
                return false;
            }
        }
    }

    @NonNull
    private static LinkedList<Class<? extends Interceptor>> getInceptorClasses(Router router, Class<?> clazz) {
        LinkedList<Class<? extends Interceptor>> list = new LinkedList<>(RouterConfig.getInstance().getInterceptorClasses());
        list.addAll(router.getInceptorClasses());
        if (clazz.isAnnotationPresent(RouterInterceptor.class)) {
            for (Class<? extends Interceptor> c : clazz.getAnnotation(RouterInterceptor.class).value()) {
                list.add(c);
            }
        }
        return list;
    }

    private static void resolveByInterceptor(final Class<?> targetClass, final Router router, final Iterator<Class<? extends Interceptor>> iterator) {
        try {
            if (iterator.hasNext()) {
                final Class<? extends Interceptor> interceptorClass = iterator.next();
                if (Looper.myLooper() == Looper.getMainLooper()) {//当前在主线程
                    if (interceptorClass.isAnnotationPresent(RunInChildThread.class)) {//如果要求在子线程运行
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handleByInterceptor(interceptorClass, router, targetClass, iterator);
                            }
                        }).start();
                    } else {
                        handleByInterceptor(interceptorClass, router, targetClass, iterator);
                    }
                } else {//当前在子线程
                    if (interceptorClass.isAnnotationPresent(RunInMainThread.class)) {//要求在主线程
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                handleByInterceptor(interceptorClass, router, targetClass, iterator);
                            }
                        });
                    } else {
                        handleByInterceptor(interceptorClass, router, targetClass, iterator);
                    }
                }
            } else {
                try {
                    Method method = targetClass.getMethod(TARGET_CLASS_START_METHOD_NAME, TARGET_CLASS_START_METHOD_PARAMS);
                    method.invoke(targetClass, router);
                } catch (NoSuchMethodException e) {
                    //如果是Activity，则直接跳转过去
                    if (Activity.class.isAssignableFrom(targetClass)) {
                        Intent intent = new Intent(router.getContext(), targetClass);
                        if (router.getIntentFlag() != 0)
                            intent.setFlags(router.getIntentFlag());
                        router.getContext().startActivity(intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void handleByInterceptor(Class<? extends Interceptor> inceptorClass, Router router, final Class<?> targetClass, final Iterator<Class<? extends Interceptor>> iterator) {
        try {
            inceptorClass.newInstance().handle(router, new Interceptor.Callback() {
                @Override
                public void next(Router router) {
                    resolveByInterceptor(targetClass, router, iterator);
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
