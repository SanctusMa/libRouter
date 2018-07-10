package router.tairan.com.trrouter;

import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.trc.android.router.Router;
import com.trc.android.router.annotation.uri.RouterHost;
import com.trc.android.router.annotation.uri.RouterScheme;

@RouterScheme("test")
@RouterHost("custom_view")
public class RemoteViewDemo extends android.support.v7.widget.AppCompatTextView {

    public RemoteViewDemo(Context context) {
        super(context);
        setText("This view can be declared in other module so that we can cut off the dependency of codding level");
    }

    //必须实现此方法，返回类型可为任意类型
    public static RemoteViewDemo transformObject(Router router) {
        return new RemoteViewDemo(router.getContext());
    }
}
