package router.tairan.com.trrouter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.trc.android.router.Router;
import com.trc.android.router.annotation.uri.RouterUri;

@RouterUri("tlkj://main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void start(Router router) {
        router.getContext().startActivity(new Intent(router.getContext(), MainActivity.class));
    }

    public void onClickToBaidu(View view) {
        Router.from(this).to(Pages.BAIDU);
    }

    public void onClickToSina(View view) {
        Router.fromCurrentContext().to(Pages.SINA);
    }

    public void onClickToUserProfile(View view) {
        Router.from(this).to(Pages.USER_PROFILE);
    }


    public void onClickToLoan(View view) {
        Router.from(this).to(Pages.LOAN);
    }


    public void addRemoteView(View view) {
        View v = (View) Router.from(this).setUri("test://custom_view").transform();
        if (null != v) {
            LinearLayout linearLayout = findViewById(R.id.root);
            linearLayout.addView(v);
        }
    }

}
