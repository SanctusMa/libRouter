package router.tairan.com.trrouter.interceptor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.trc.android.router.Interceptor;
import com.trc.android.router.Router;
import com.trc.android.router.annotation.interceptor.RunInMainThread;

import router.tairan.com.trrouter.Pages;

@RunInMainThread
public class RealNameAouthInterceptor implements Interceptor {
    public static boolean isRealNameOauthPast;

    @Override
    public void handle(final Router router, final Callback callback) {
        Toast.makeText(router.getContext(), "请求服务器，获取实名认证状态", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isRealNameOauthPast) {
                    callback.next(router);
                }else{
                    Router.Callback oauthCallback = new Router.Callback() {
                        @Override
                        public void onResult(boolean succeed, Bundle bundle) {
                            if (succeed) {
                                callback.next(router);
                            }else {
                                Toast.makeText(router.getContext(), "认证失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    Router.from(router.getContext()).setCallback(oauthCallback).to(Pages.REAL_NAME_OAUTH);
                }
            }
        }).start();
    }
}
