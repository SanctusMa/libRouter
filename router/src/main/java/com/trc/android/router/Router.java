package com.trc.android.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.view.Window;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Router {

    private static final String HASH_KEY = "#";
    private static final String HASH_KEY_PLACEHOLDER = "HASH_KEY_PLACEHOLDER";
    private static final ArrayMap<String, String> EMPTY_MAP = new ArrayMap<>(0);
    private Context context;
    private List<Class<? extends Interceptor>> interceptorClassList;
    String scheme = RouterConfig.getInstance().getDefaultScheme();
    String host;
    String path;
    private Callback callback;
    private ArrayMap<String, String> params;
    private int intentFlag;
    private HashMap<String, Object> extraMap;
    private Uri originUri;

    private Router(Context context) {
        this.context = context;
    }

    private TargetLostListener targetLostListener;

    //通过Router传递一些对象，比如WevView/Fragment/Activity/Model对象等
    public Router putExtra(String key, @Nullable Object obj) {
        if (null == obj) {
            return this;
        } else if (null == extraMap) {
            extraMap = new HashMap(2);
        }
        extraMap.put(key, obj);
        return this;
    }

    //通过Router传递一些对象，比如WevView/Fragment/Activity/Model对象等
    public @Nullable Object getExtra(String key) {
        if (null == extraMap) return null;
        else return extraMap.get(key);
    }

    public static Router fromCurrentContext() {
        Context context = RouterConfig.getCurrentActivity();
        if (null == context) {
            context = RouterConfig.getApplication();
        }
        return new Router(context);
    }

    public static Router from(@Nullable Context context) {
        if (null == context) {
            return fromCurrentContext();
        } else {
            return new Router(context);
        }
    }

    public Router setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 拼接在URI中的参数
     * @param value 只接受基本类型,并最终转化成String类型
     */
    public Router setParam(String key, Object value) {
        if (null == value) {
            return this;
        } else if (null == params) {
            params = new ArrayMap();
        }
        params.put(key, String.valueOf(value));
        return this;
    }

    //拼接在URI中的参数
    public String getParam(String key) {
        return params == null ? "" : params.get(key);
    }

    public Router setInterceptors(Class<? extends Interceptor>... interceptorClasses) {
        if (null == interceptorClassList) {
            interceptorClassList = Arrays.asList(interceptorClasses);
        } else {
            for (Class<? extends Interceptor> clazz : interceptorClasses)
                interceptorClassList.add(clazz);
        }
        return this;
    }

    public boolean go() {
        return RouterManager.route(this);
    }

    public Uri toUri() {
        if (null != originUri) {
            return originUri;
        } else {
            return Uri.parse(toUriStr());
        }
    }

    public String toUriStr() {
        if (null != originUri) {
            return originUri.toString();
        } else {
            StringBuilder sb = new StringBuilder(scheme);
            sb.append("://").append(host);
            if (null != path) {
                if (!path.startsWith("/")) sb.append('/');
                sb.append(path);
            }
            if (null != params && !params.isEmpty()) {
                sb.append('?');
                Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entrySet = it.next();
                    sb.append(entrySet.getKey()).append('=').append(entrySet.getValue());
                    if (it.hasNext()) sb.append('&');
                }
            }
            return sb.toString();
        }
    }

    public Router setIntentFlag(int flag) {
        intentFlag = flag;
        return this;
    }

    public int getIntentFlag() {
        return intentFlag;
    }

    public Router setUri(Uri uri) {
        this.originUri = uri;
        boolean hasHashKey = uri.getFragment() != null;
        if (hasHashKey) {
            uri = Uri.parse(uri.toString().replaceFirst(HASH_KEY, HASH_KEY_PLACEHOLDER));
        }
        this.scheme = uri.getScheme();
        this.host = uri.getHost();
        this.path = uri.getPath();
        if (hasHashKey) {
            path = path.replaceFirst(HASH_KEY_PLACEHOLDER, HASH_KEY);
        }
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

    /**
     * 找到Router匹配的Class，然后调用该Class的toRemoteObject(Router router)静态方法返回一个包装好的对象
     */
    public Object transform() {
        try {
            Class matchedClass = RouterManager.getMatchedClass(this);
            assert matchedClass != null;
            Method method = matchedClass.getMethod("transformObject", Router.class);
            return method.invoke(matchedClass, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Router setUri(String uriStr) {
        return setUri(Uri.parse(uriStr));
    }

    public Context getContext() {
        return context;
    }

    List<Class<? extends Interceptor>> getInterceptorClasses() {
        return interceptorClassList;
    }

    public Callback getCallback() {
        return callback;
    }

    public TargetLostListener getTargetLostListener() {
        return targetLostListener;
    }

    public Router setTargetLostListener(TargetLostListener targetLostListener) {
        this.targetLostListener = targetLostListener;
        return this;
    }

    public interface Callback {
        void onResult(boolean succeed, Bundle bundle);
    }
}
