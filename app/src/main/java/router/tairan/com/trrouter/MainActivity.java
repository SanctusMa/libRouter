package router.tairan.com.trrouter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import router.tairan.com.router.Router;
import router.tairan.com.router.annotation.uri.RouterHost;
import router.tairan.com.router.annotation.uri.RouterScheme;

@RouterScheme("tlkj")
@RouterHost("main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void start(Router router) {
        router.getContext().startActivity(new Intent(router.getContext(), MainActivity.class));
    }

    public void toNextPage(View view) {
        //scheme如果没有则使用RouterConfig的defaultScheme
        Router.from(this).setHost("some_page_name").go();
    }

    public void toNextPage2(View view) {
        Router.from(this)
                .setScheme("tlkj")
                .setHost("hostactivity")
                .setParams("userId", 110)
                .go();
    }

    public void toNextPage3(View view) {
        Router.from(this)
                .setUri("tlkj://trc.com")
                .go();
    }


}
