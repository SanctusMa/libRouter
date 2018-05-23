package router.tairan.com.trrouter;

import android.app.Application;

import java.util.HashMap;

import com.trc.android.router.RedirectAdapter;
import com.trc.android.router.Router;
import com.trc.android.router.RouterConfig;
import com.trc.android.router.TargetLostListener;

public class RouterHelper {
    public static HashMap<String, String> redirectMap = new HashMap<>();
    public static RedirectAdapter redirectAdapter = new RedirectAdapter() {
        @Override
        public Router adapt(Router router) {
            String url = router.toUriStr();
            for (String targetUrl : redirectMap.keySet()) {
                if (url.contains(targetUrl)) {
                    return Router.from(router.getContext()).setUri(redirectMap.get(targetUrl));
                }
            }
            return router;
        }
    };
    private static TargetLostListener targetLostListener = new TargetLostListener() {
        @Override
        public void onTargetLost(Router router) {
            //Do something 例如利用隐士Intent交给系统处理
        }
    };

    static {
        redirectMap.put("tlkj://trc.com", "http://www.baidu.com");
    }

    public static void init(Application application) {
        RouterConfig.getInstance()
                .init(application)
                .setTargetLostListener(targetLostListener)
                .setDefaultScheme("tlkj")
//                .addInterceptor(RandomLoginInterceptor.class)
                .setRedirectAdapter(redirectAdapter);
    }
}
