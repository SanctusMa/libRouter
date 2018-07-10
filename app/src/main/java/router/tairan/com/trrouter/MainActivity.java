package router.tairan.com.trrouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.trc.android.router.TargetLostListener;
import com.trc.android.router.annotation.uri.RouterHost;
import com.trc.android.router.annotation.uri.RouterScheme;

import com.trc.android.router.Router;

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
//        Router.from(this).setHost("some_page_name").go();
        Router.from(this).setHost("qwerqz").setTargetLostListener(new TargetLostListener() {
            @Override
            public void onTargetLost(Router router) {
                Toast.makeText(router.getContext(), ">>>", Toast.LENGTH_LONG).show();
            }
        }).go();
    }

    public void toNextPage2(View view) {
        Router.fromCurrentActivity()
                .setScheme("tlkj")
                .setHost("hostactivity")
                .setParams("userId", 110)
                .setCallback(new Router.Callback() {
                    @Override
                    public void onResult(boolean succeed, Bundle bundle) {
                        if (succeed) {
                            String name = bundle.getString("name");
                            Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .go();
    }

    public void toNextPage3(View view) {
        Router.from(this).to("tlkj://trc.com");
    }


    public void addRemoteView(View view) {
        View v = (View) Router.from(this).setUri("test://custom_view").transform();
        if (null != v) {
            LinearLayout linearLayout = findViewById(R.id.root);
            linearLayout.addView(v);
        }
    }
}
