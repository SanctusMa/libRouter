package router.tairan.com.trrouter;

import android.app.Application;

import java.util.HashMap;

import router.tairan.com.router.Interceptor;
import router.tairan.com.router.RedirectAdapter;
import router.tairan.com.router.Router;
import router.tairan.com.router.RouterConfig;
import router.tairan.com.trrouter.inceptor.RandomLoginInterceptor;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       RouterHelper.init(this);
    }


}
