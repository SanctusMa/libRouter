package com.trc.android.router.util;

import android.content.Intent;
import android.support.annotation.NonNull;

public abstract class LifeCircleCallback {
    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onActivityResult(int resultCode, Intent data) {
    }

    protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }
}
