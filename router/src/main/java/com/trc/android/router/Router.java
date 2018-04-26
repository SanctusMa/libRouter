package com.trc.android.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.view.Window;

import com.trc.android.router.annotation.uri.RouterHost;
import com.trc.android.router.annotation.uri.RouterPath;
import com.trc.android.router.annotation.uri.RouterScheme;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Router {

    private Context context;
    private List<Class<? extends Interceptor>> inceptorClassList = Collections.EMPTY_LIST;
    private String scheme = RouterConfig.getInstance().getDefaultScheme();
    private String host;
    private String path;
    private Callback callback;
    private ArrayMap<String, String> params;

    private Router(Context context) {
        this.context = context;
    }


    public static Router from(@Nullable Context context) {
        if (null == context) {
            context = RouterConfig.getCurrentActivity();
            if (null == context) {
                context = RouterConfig.getApplication();
            }
        }
        return new Router(context);
    }


    public Router setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public Router setHost(String host) {
        this.host = host;
        return this;
    }

    public Router setPath(String path) {
        this.path = path;
        return this;
    }

    public Router setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public Router setParams(String key, Object value) {
        if (null == params)
            params = new ArrayMap();
        params.put(key, String.valueOf(value));
        return this;
    }

    public Router setInterceptors(Class<? extends Interceptor>... interceptorClasses) {
        if (null == inceptorClassList) {
            for (Class<? extends Interceptor> clazz : interceptorClasses)
                inceptorClassList.add(clazz);
        }
        return this;
    }

    public boolean go() {
        return RouterManager.route(this);
    }

    public String toUriStr() {
        StringBuilder sb = new StringBuilder(scheme);
        sb.append("://").append(host);
        if (!host.endsWith("/"))
            sb.append("/");
        if (null != path) {
            sb.append(path);
        }
        if (null != params && !params.isEmpty()) {
            sb.append('?');
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry<String, String> entrySet = it.next();
                sb.append(entrySet.getKey()).append('=').append(entrySet.getValue());
                if (it.hasNext()) sb.append('&');
            }
        }
        return sb.toString();
    }

    public Router setUri(Uri uri) {
        this.scheme = uri.getScheme();
        this.host = uri.getHost();
        this.path = uri.getPath();
        for (String key : uri.getQueryParameterNames()) {
            String value = uri.getQueryParameter(key);
            if (null == params)
                params = new ArrayMap();
            params.put(key, String.valueOf(value));
        }
        return this;
    }

    public boolean to(Uri uri) {
        setUri(uri);
        return go();
    }

    public boolean to(String uri) {
        setUri(uri);
        return go();
    }

    public Router setUri(String uriStr) {
        return setUri(Uri.parse(uriStr));
    }


    boolean match(Class<?> clazz) {
        boolean atLeastMatchOne = false;
        if (clazz.isAnnotationPresent(RouterScheme.class)) {
            boolean matchScheme = arrayContains(clazz.getAnnotation(RouterScheme.class).value(), scheme);
            if (!matchScheme) {
                return false;
            }
            atLeastMatchOne = true;
        }
        if (clazz.isAnnotationPresent(RouterHost.class)) {
            boolean matchHost = arrayContains(clazz.getAnnotation(RouterHost.class).value(), host);
            if (!matchHost) {
                return false;
            }
            atLeastMatchOne = true;
        }
        if (clazz.isAnnotationPresent(RouterPath.class)) {
            boolean matchPath = arrayContains(clazz.getAnnotation(RouterPath.class).value(), path);
            if (!matchPath) {
                return false;
            }
            atLeastMatchOne = true;
        }
        return atLeastMatchOne;
    }

    public Context getContext() {
        return context;
    }

    private boolean arrayContains(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) return true;
        }
        return false;
    }

    public List<Class<? extends Interceptor>> getInceptorClasses() {
        return inceptorClassList;
    }

    public Callback getCallback() {
        return callback;
    }


    public ArrayMap<String, String> getParams() {
        return params;
    }

    public interface Callback {
        void onResult(boolean succeed, Bundle bundle);
    }


    public void startActivity(final Intent intent, final LifeCircleFragment.Callback lifeCircleCallback) {
        if (context instanceof FragmentActivity) {
            LifeCircleFragment fragment = new LifeCircleFragment();
            fragment.setCallback(lifeCircleCallback, intent);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(Window.ID_ANDROID_CONTENT, fragment).commit();

        } else {
            ((Activity) context).startActivityForResult(intent, 0);
        }
    }
}