package tk.hongbo.network.utils;

import android.util.Log;

import tk.hongbo.network.Net;

public class LogHelper {

    public static final void d(String message) {
        if (Net.getIns().isDebug()) {
            Log.d(Net.TAG, message);
        }
    }

    public static final void e(String message, Throwable throwable) {
        if (Net.getIns().isDebug()) {
            Log.e(Net.TAG, message, throwable);
        }
    }
}
