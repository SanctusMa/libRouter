package router.tairan.com.trrouter.interceptor;

import android.util.Log;

import com.trc.android.router.annotation.interceptor.RunInMainThread;

import com.trc.android.router.Interceptor;
import com.trc.android.router.Router;

@RunInMainThread
public class RandomLoginInterceptor implements Interceptor {

    @Override
    public void handle(final Router router, final Callback callback) {
        final boolean goOn = System.currentTimeMillis() % 2 == 0;
        Log.e(">>>", goOn ? "已登陆" : "未登录");
        callback.next(router);
    }
}
