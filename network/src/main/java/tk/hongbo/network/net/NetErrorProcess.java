package tk.hongbo.network.net;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import java.lang.ref.WeakReference;

import tk.hongbo.network.Net;
import tk.hongbo.network.R;

/**
 * 默认API接口处理
 * Created by HONGBO on 2018/4/30 17:23.
 */
public class NetErrorProcess {

    private WeakReference<Activity> weakActivity;
    private NetData netData;

    public NetErrorProcess(Activity activity, NetData netData) {
        this.weakActivity = new WeakReference<>(activity);
        this.netData = netData;
    }

    public void show() {
        switch (netData.getNetStatus()) {
            case NetData.NET_STATUS_FAILURE:
                if (weakActivity != null && weakActivity.get() != null) {
                    Snackbar.make(weakActivity.get().getCurrentFocus(), Net.getIns().getApplication()
                            .getString(R.string.request_error), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
