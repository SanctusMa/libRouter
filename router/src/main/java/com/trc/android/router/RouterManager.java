package com.trc.android.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.trc.android.router.annotation.interceptor.RouterInterceptor;
import com.trc.android.router.annotation.interceptor.RunInChildThread;
import com.trc.android.router.annotation.interceptor.RunInMainThread;
import com.trc.android.router.annotation.uri.RouterHost;
import com.trc.android.router.annotation.uri.RouterPath;
import com.trc.android.router.annotation.uri.RouterScheme;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;


public class RouterManager {
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
            TargetLostListener targetLostListener = router.getTargetLostListener();
            if (null == targetLostListener)
                targetLostListener = RouterConfig.getInstance().getTargetLostListener();
            if (null != targetLostListener) {
                targetLostListener.onTargetLost(router);
            } else {
                Router.Callback callback = router.getCallback();
                if (callback != null) {
                    callback.onResult(false, null);
                }
            }
            return false;
        }
    }

    @NonNull
    private static LinkedList<Class<? extends Interceptor>> getInceptorClasses(Router router, Class<?> clazz) {
        LinkedList<Class<? extends Interceptor>> list = new LinkedList<>(RouterConfig.getInstance().getInterceptorClasses());
        if (null != router.getInterceptorClasses())
            list.addAll(router.getInterceptorClasses());
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
                    Method method = targetClass.getMethod(TARGET_CLASS_START_METHOD_NAME, Router.class);
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


    static Class getMatchedClass(Router router) {
        for (Class clazz : RouterConfig.getInstance().getClasses()) {
            if (match(router, clazz)) {
                return clazz;
            }
        }
        return null;
    }

    static boolean match(Router router, Class<?> clazz) {
        boolean atLeastMatchOne = false;
        if (clazz.isAnnotationPresent(RouterScheme.class)) {
            boolean matchScheme = arrayContains(clazz.getAnnotation(RouterScheme.class).value(), router.scheme);
            if (!matchScheme) {
                return false;
            }
            atLeastMatchOne = true;
        }
        if (clazz.isAnnotationPresent(RouterHost.class)) {
            boolean matchHost = arrayContains(clazz.getAnnotation(RouterHost.class).value(), router.host);
            if (!matchHost) {
                return false;
            }
            atLeastMatchOne = true;
        }
        if (clazz.isAnnotationPresent(RouterPath.class)) {
            boolean matchPath = arrayContains(clazz.getAnnotation(RouterPath.class).value(), router.path);
            if (!matchPath) {
                return false;
            }
            atLeastMatchOne = true;
        }
        return atLeastMatchOne;
    }

    static boolean arrayContains(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) return true;
        }
        return false;
    }

}
