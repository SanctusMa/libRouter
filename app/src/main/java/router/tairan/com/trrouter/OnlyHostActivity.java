package router.tairan.com.trrouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import router.tairan.com.router.Router;
import router.tairan.com.router.annotation.uri.RouterHost;
import router.tairan.com.router.annotation.interceptor.RouterInterceptor;
import router.tairan.com.router.annotation.uri.RouterScheme;
import router.tairan.com.trrouter.inceptor.RealNameAouthInterceptor;

@RouterScheme("tlkj")
@RouterHost("hostactivity")
@RouterInterceptor(RealNameAouthInterceptor.class)
public class OnlyHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_host);
    }

    public static void start(Router router) {
        router.getContext().startActivity(new Intent(router.getContext(), OnlyHostActivity.class));
    }
}
