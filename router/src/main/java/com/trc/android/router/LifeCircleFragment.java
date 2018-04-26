package com.trc.android.router;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public class LifeCircleFragment extends Fragment {
    private Callback callback;
    private Intent intent;
    public static final int REQUEST_CODE = 100;
    boolean isFirstResume = true;

    public void setCallback(Callback callback, Intent intent) {
        this.callback = callback;
        this.intent = intent;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            startActivityForResult(intent, REQUEST_CODE);
            isFirstResume = false;
        } else {
            if (null != callback && null != intent) {
                callback.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        if (null != callback) {
            super.onPause();
            if (null != callback) {
                callback.onPause();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != callback) {
            if (requestCode == REQUEST_CODE)
                callback.onActivityResult(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (null != callback) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            callback.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public abstract static class Callback {
        protected void onResume() {
        }

        protected void onPause() {
        }

        protected void onActivityResult(int resultCode, Intent data) {
        }

        protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        }

    }
}

