package com.example.android.bakingapp;


import android.os.Handler;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.IdlingResource.SimpleIdlingResource;

public class RecipeDownloader {
    private static final int DELAY_MILLIS = 2000;

    interface DelayerCallback{
        void onDone(boolean isDone);
    }

    static void downLoadRecipe(final DelayerCallback delayerCallback,
                               @Nullable final SimpleIdlingResource simpleIdlingResource) {

        if (simpleIdlingResource != null) {
            simpleIdlingResource.setIdleState(false);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (delayerCallback != null) {
                    delayerCallback.onDone(true);
                    if (simpleIdlingResource != null) {
                        simpleIdlingResource.setIdleState(true);
                    }
                }
            }
        }, DELAY_MILLIS);
    }
}
