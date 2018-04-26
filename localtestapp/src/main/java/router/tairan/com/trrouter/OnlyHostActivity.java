package router.tairan.com.trrouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trc.android.router.annotation.uri.RouterHost;
import com.trc.android.router.annotation.uri.RouterScheme;

import com.trc.android.router.LifeCircleFragment;
import com.trc.android.router.Router;

@RouterScheme("tlkj")
@RouterHost("hostactivity")
public class OnlyHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_host);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("name", "Hunter");
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public static void start(final Router router) {
        Context context = router.getContext();
        if (context instanceof Activity) {
            router.startActivity(new Intent(context, OnlyHostActivity.class), new LifeCircleFragment.Callback() {
                @Override
                protected void onActivityResult(int resultCode, Intent data) {
                    Router.Callback callback = router.getCallback();
                    if (null != callback) {
                        if (resultCode == RESULT_OK) {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", data.getStringExtra("name"));
                            callback.onResult(true, bundle);
                        } else {
                            callback.onResult(false, null);
                        }
                    }
                }
            });
        } else {
            context.startActivity(new Intent(context, OnlyHostActivity.class));
        }
    }
}
