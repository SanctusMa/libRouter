package router.tairan.com.trrouter.inceptor;

import android.util.Log;

import router.tairan.com.router.Interceptor;
import router.tairan.com.router.Router;
import router.tairan.com.router.annotation.interceptor.RunInMainThread;

@RunInMainThread
public class RandomLoginInterceptor implements Interceptor {

    @Override
    public void handle(final Router router, final Callback callback) {
        final boolean goOn = System.currentTimeMillis() % 2 == 0;
        Log.e(">>>", goOn ? "已登陆" : "未登录");
        callback.next(router);
    }
}
