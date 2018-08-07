package com.trc.android.router;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class Router {

    private static final String METHOD_TRANSFORM_OBJECT = "transformObject";
    private boolean isUriSet;
    private Context context;
    private List<Class<? extends Interceptor>> interceptorClassList;
    private Callback callback;
    private StringBuilder urlBuilder;
    /**
     * 最终拼接成的链接{@link #toUriStr()}
     */
    private String url;
    private SimpleArrayMap<String, Object> extraMap;

    private Router(Context context) {
        this.context = context;
    }

    private TargetLostListener targetLostListener;

    //通过Router传递一些对象，比如WevView/Fragment/Activity/Model对象等
    public Router putExtra(String key, @Nullable Object obj) {
        if (null == obj) {
            return this;
        } else if (null == extraMap) {
            extraMap = new SimpleArrayMap<>(2);
        }
        extraMap.put(key, obj);
        return this;
    }

    //通过Router传递一些对象，比如WevView/Fragment/Activity/Model对象等
    @Nullable
    public Object getExtra(String key) {
        if (null == extraMap) return null;
        else return extraMap.get(key);
    }

    /**
     * 等同于Router.from(router.getContext())
     */
    public static Router from(Router router) {
        return from(router.context);
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
     *
     * @param value 只接受基本类型,并最终转化成String类型
     */
    public Router setParam(String key, Object value) {
        url = null;
        if (null == value) {
            return this;
        } else {
            if (null == urlBuilder) urlBuilder = new StringBuilder();
            if (urlBuilder.indexOf("?") > 0) {
                char lastChar = urlBuilder.charAt(urlBuilder.length() - 1);
                if (lastChar != '?')
                    urlBuilder.append('&');
            } else if (urlBuilder.length() > 0) {
                urlBuilder.append('?');
            }
            urlBuilder.append(key).append('=').append(value);
            return this;
        }
    }

    /**
     * 拼接在URI中的参数
     */
    public String getParam(String key) {
        return UriUtil.getParam(toUriStr(), key);
    }

    /**
     * 设置拦截器
     */
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
        return Uri.parse(toUriStr());
    }

    public String toUriStr() {
        assert urlBuilder != null;
        if (url == null) url = urlBuilder.toString();
        return url;
    }


    public Router setUri(String uri) {
        if (isUriSet) throw new IllegalStateException("路由地址不能修改");
        isUriSet = true;
        url = null;
        if (null == urlBuilder) urlBuilder = new StringBuilder(uri.length());
        if (urlBuilder.length() == 0) {
            urlBuilder.append(uri);
        } else {
            char lastChar = uri.charAt(uri.length() - 1);
            if (uri.indexOf('?') > 0) {
                if (lastChar != '&')
                    urlBuilder.insert(0, '&');
            } else {
                if (lastChar != '?')
                    urlBuilder.insert(0, '?');
            }
            urlBuilder.insert(0, uri);
        }
        return this;
    }

    public Router setUri(Uri uri) {
        setUri(uri.toString());
        return this;
    }

    /**
     * {@link #to(Uri)}
     */
    public boolean to(Uri uri) {
        return to(uri.toString());
    }

    /**
     * 找到Router匹配的Class，然后调用该Class的start(Router router)静态方法返回一个包装好的对象
     */
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
            Method method = matchedClass.getDeclaredMethod(METHOD_TRANSFORM_OBJECT, Router.class);
            return method.invoke(matchedClass, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    TargetLostListener getTargetLostListener() {
        return targetLostListener;
    }

    public Router setTargetLostListener(TargetLostListener targetLostListener) {
        this.targetLostListener = targetLostListener;
        return this;
    }

    public interface Callback {
        void onResult(boolean succeed, Map map);
    }
    @Nullable
    public String getScheme(){
        return UriUtil.getScheme(toUriStr());
    }
}
