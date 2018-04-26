package com.trc.android.router;

public interface Interceptor {
    void handle(Router router, Callback callback);

    interface Callback {
        void next(Router router);
    }
}
