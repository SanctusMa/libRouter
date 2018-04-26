package router.tairan.com.trrouter.inceptor;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.trc.android.router.annotation.interceptor.RunInMainThread;

import com.trc.android.router.Interceptor;
import com.trc.android.router.Router;

@RunInMainThread
public class RealNameAouthInterceptor implements Interceptor {

    @Override
    public void handle(final Router router, final Callback callback) {
        Log.e(">>>", "模拟异步实名认证状态");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final boolean goOn = System.currentTimeMillis() % 2 == 0;
                callback.next(router);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(">>>", goOn ? "已认证" : "未认证");
                    }
                });

            }
        }).start();
    }
}
