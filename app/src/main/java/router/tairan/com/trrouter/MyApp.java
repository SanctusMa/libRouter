package router.tairan.com.trrouter;

import android.app.Application;

import com.trc.android.router.annotation.compile.RouterAppModule;
import com.trc.android.router.annotation.uri.RouterUri;

@RouterAppModule
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       RouterHelper.init(this);
    }


}
