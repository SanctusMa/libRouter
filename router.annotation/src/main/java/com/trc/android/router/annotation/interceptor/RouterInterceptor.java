package com.trc.android.router.annotation.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 注解在Interceptor接口的实现类上
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RouterInterceptor {
    Class[] value();
}
