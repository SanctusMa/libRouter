package router.tairan.com.trrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trc.android.router.annotation.interceptor.RouterInterceptor;
import com.trc.android.router.annotation.uri.RouterDes;
import com.trc.android.router.annotation.uri.RouterMeta;
import com.trc.android.router.annotation.uri.RouterUri;

import router.tairan.com.trrouter.interceptor.RealNameOauthInterceptor;

@RouterUri(Pages.LOAN)
@RouterDes("贷款页面")
@RouterMeta(
        "needLogin=true 链接如果包含此参数，处理该链接时需要APP是登录状态才能加载，否则直接跳转到登录页面。如果登录成功后直接打开目标链接或直接关闭登录\n" +
                "needShare=true 链接如果包含此参数，处理该链接时需要在右上角显示分享图标\n" +
                "hideToolbar=true  入口链接，如果包含此参数，不显示toolbar，导航交给H5处理\n" +
                "configToolbar=BASE64_ENCODED_JSON 入口链接，如果包含此参数，则配置相应\n" +
                "toolbarTitle=URL_ENCODED_TITLE 入口链接，如果包含此参数，则Toolbar始终显示此标题\n" +
                "registLifecircle=true 如果包含此参数，则APP会调用在Web页面恢复到前台时调用H5的onResume()方法，在Web页面切换后台时调用H5的onPause()方法")
@RouterInterceptor(RealNameOauthInterceptor.class)//跳转该页面需要登录且经过实名认证
public class LoanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);
    }
}
