package router.tairan.com.trrouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.trc.android.router.annotation.uri.RouterHost;
import com.trc.android.router.annotation.uri.RouterPath;

@RouterHost("some_page_name")
@RouterPath("a/b/c")
public class NoSchemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_scheme);
    }
}
