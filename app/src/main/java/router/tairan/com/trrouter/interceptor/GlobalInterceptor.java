package router.tairan.com.trrouter.interceptor;

import android.widget.Toast;

import com.trc.android.router.Chain;
import com.trc.android.router.Interceptor;
import com.trc.android.router.Router;

public class GlobalInterceptor implements Interceptor{
    @Override
    public void handle(Router router, Chain chain) {
        Toast.makeText(router.getContext(), "我是全局拦截器额", Toast.LENGTH_SHORT).show();
        chain.proceed(router);
    }
}
