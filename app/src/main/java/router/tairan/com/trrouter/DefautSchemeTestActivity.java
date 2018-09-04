package router.tairan.com.trrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trc.android.router.annotation.uri.RouterDes;
import com.trc.android.router.annotation.uri.RouterUri;

@RouterUri("hello")
@RouterDes("默认Scheme测试页面")
public class DefautSchemeTestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defaut_scheme_test);
    }
}
