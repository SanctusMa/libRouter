package router.tairan.com.trrouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import router.tairan.com.router.Router;
import router.tairan.com.router.annotation.uri.RouterHost;

@RouterHost("trc.com")
public class RedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
    }

    public static void start(Router router) {
        Intent intent = new Intent(router.getContext(), RedirectActivity.class);
        router.getContext().startActivity(intent);
    }
}
