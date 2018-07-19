package com.trc.android.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.trc.android.router.annotation.interceptor.RouterInterceptor;
import com.trc.android.router.annotation.interceptor.RunInChildThread;
import com.trc.android.router.annotation.interceptor.RunInMainThread;
import com.trc.android.router.annotation.uri.RouterUri;
import com.trc.android.router.util.LifeCircleUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


public class RouterManager {
    private static final String TARGET_CLASS_START_METHOD_NAME = "start";
    private static final HashMap<String, Class> classCacheMap = new HashMap<>();

    /**
     * @param router
     * @return true 是否找到了处理Router的类
     */
    public static boolean route(Router router) {
        router = RouterConfig.getInstance().getRedirectAdapter().adapt(router);
        Class<?> clazz = getMatchedClass(router);
        if (null != clazz) {
            LinkedList<Class<? extends Interceptor>> list = getInterceptorClasses(router, clazz);
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
    private static LinkedList<Class<? extends Interceptor>> getInterceptorClasses(Router router, Class<?> clazz) {
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
                        Context context = router.getContext();
                        Intent intent = new Intent(context, targetClass);
                        if (router.getIntentFlag() != 0)
                            intent.setFlags(router.getIntentFlag());
                        context.startActivity(intent);
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


    //返回匹配程度最高的Class
    static Class getMatchedClass(Router router) {
        String uri = router.toUriStr();
        Class targetClass = classCacheMap.get(uri);
        if (null == targetClass) {
            int lastMatch = 0;
            for (Class clazz : RouterConfig.getInstance().getClasses()) {
                int match = match(uri, clazz);
                if (match > lastMatch) {
                    lastMatch = match;
                    targetClass = clazz;
                }
            }
            if (null != targetClass) classCacheMap.put(uri, targetClass);
        }
        return targetClass;
    }

    /**
     * @return 0表示不匹配，>0表示匹配程度，匹配程度越高该值越大
     */
    static int match(String targetUri, Class<?> clazz) {
        //进行URI包含(startsWith)匹配
        String[] uris = clazz.getAnnotation(RouterUri.class).value();
        for (String item : uris) {
            if (targetUri.startsWith(item)) {
                if (targetUri.length() == item.length()) return Integer.MAX_VALUE;//精准匹配
                else return item.length();//包含匹配
            }
        }
        return 0;
    }

}
