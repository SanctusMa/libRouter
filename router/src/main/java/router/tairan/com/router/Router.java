package router.tairan.com.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import router.tairan.com.router.annotation.uri.RouterHost;
import router.tairan.com.router.annotation.uri.RouterPath;
import router.tairan.com.router.annotation.uri.RouterScheme;

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

    public void go() {
        RouterManager.route(this);
    }

    public String toUriStr() {
        StringBuilder sb = new StringBuilder(scheme);
        sb.append("://").append(host);
        if (null != path && !path.isEmpty()) {
            sb.append('/').append("setPath");
        }
        if (null != params) {
            sb.append('?');
            for (Map.Entry<String, String> entrySet : params.entrySet()) {
                sb.append(entrySet.getKey()).append('=').append(entrySet.getValue());
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

    public Router setUri(String uriStr) {
        return setUri(Uri.parse(uriStr));
    }


    boolean match(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RouterScheme.class)) {
            boolean matchScheme = arrayContains(clazz.getAnnotation(RouterScheme.class).value(), scheme);
            if (!matchScheme) {
                return false;
            }
        }
        if (clazz.isAnnotationPresent(RouterHost.class)) {
            boolean matchHost = arrayContains(clazz.getAnnotation(RouterHost.class).value(), host);
            if (!matchHost) {
                return false;
            }
        }
        if (clazz.isAnnotationPresent(RouterPath.class)) {
            boolean matchPath = arrayContains(clazz.getAnnotation(RouterPath.class).value(), path);
            if (!matchPath) {
                return false;
            }
        }
        return true;
    }

    public Context getContext() {
        return context;
    }

    private boolean arrayContains(String[] array, String value) {
        for (String item : array) {
            if (value.equals(item)) return true;
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

    public abstract class LifeCircleCallback {
        protected void onResume() {
        }

        protected void onPause() {
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        }

        protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        }
    }

}
