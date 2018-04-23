package router.tairan.com.router.annotation.uri;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RouterPath {
    String[] value();
}
