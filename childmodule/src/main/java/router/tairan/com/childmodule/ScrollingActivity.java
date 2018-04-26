package router.tairan.com.childmodule;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.trc.android.router.LifeCircleFragment;
import com.trc.android.router.Router;
import com.trc.android.router.annotation.uri.RouterHost;

@RouterHost("qwerq")
public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public static void start(Router router){
        router.startActivity(new Intent(router.getContext(), ScrollingActivity.class), new LifeCircleFragment.Callback() {
            @Override
            protected void onActivityResult(int resultCode, Intent data) {
                super.onActivityResult(resultCode, data);
            }
        });
    }
}
