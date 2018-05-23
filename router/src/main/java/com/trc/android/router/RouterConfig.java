package com.trc.android.router;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Keep;


import java.util.LinkedList;
import java.util.List;


public class RouterConfig {
    private static Activity sCurrentActivity;
    private static RouterConfig instance;
    private static Class[] classes = new Class[0];
    private LinkedList<Class<? extends Interceptor>> inceptorClassList = new LinkedList<>();
    private String defaultScheme = "default";
    private RedirectAdapter redirectAdapter = new RedirectAdapter() {
        @Override
        public Router adapt(Router router) {
            return router;
        }
    };
    private TargetLostListener targetLostListener;

    public static RouterConfig getInstance() {
        if (null == instance) {
            synchronized (RouterConfig.class) {
                if (null == instance) {
                    instance = new RouterConfig();
                    try {
                        Class addressListClazz = Class.forName("com.trc.android.router.build.AddressList");
                        classes = (Class[]) addressListClazz.getMethod("getAnnotatedClasses").invoke(addressListClazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return instance;
    }

    private RouterConfig() {
    }

    private static Application sApplication;

    public RouterConfig init(Application application) {
        sApplication = application;
        sApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        return this;
    }


    public RouterConfig setDefaultScheme(String scheme) {
        this.defaultScheme = scheme;
        return this;
    }


    public RouterConfig setTargetLostListener(TargetLostListener handler) {
        this.targetLostListener = handler;
        return this;
    }

    public TargetLostListener getTargetLostListener() {
        return targetLostListener;
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static Activity getCurrentActivity() {
        return sCurrentActivity;
    }

    public String getDefaultScheme() {
        return this.defaultScheme;
    }


    @Keep
    public RouterConfig addInterceptor(Class<? extends Interceptor>... inceptorClasses) {
        for (Class z : inceptorClasses) {
            inceptorClassList.add(z);
        }
        return this;
    }

    public Class[] getClasses() {
        return classes;
    }

    public List<Class<? extends Interceptor>> getInterceptorClasses() {
        return inceptorClassList;
    }

    public RouterConfig setRedirectAdapter(RedirectAdapter adapter) {
        redirectAdapter = adapter;
        return this;
    }


    RedirectAdapter getRedirectAdapter() {
        return redirectAdapter;
    }
}
