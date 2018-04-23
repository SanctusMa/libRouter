package router.tairan.com.trrouter;

import android.app.Application;

import java.util.HashMap;

import router.tairan.com.router.RedirectAdapter;
import router.tairan.com.router.Router;
import router.tairan.com.router.RouterConfig;
import router.tairan.com.trrouter.inceptor.RandomLoginInterceptor;

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

    static {
        redirectMap.put("tlkj://trc.com", "http://www.baidu.com");
    }

    public static void init(Application application) {
        RouterConfig.getInstance()
                .init(application)
                .setDefaultScheme("tlkj")
                .addInceptor(RandomLoginInterceptor.class)
                .regist(MainActivity.class, OnlyHostActivity.class, NoSchemeActivity.class, WebViewActivity.class)
                .setRedirectAdapter(redirectAdapter);
    }
}
