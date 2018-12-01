package tk.hongbo.network.helper;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import tk.hongbo.network.Net;
import tk.hongbo.network.bussiness.IRequestGener;
import tk.hongbo.network.net.NetListener;

public class NetHelper {

    private volatile static NetHelper helper;

    private IRequestGener service;

    public static NetHelper get() {
        if (helper == null) {
            synchronized (NetHelper.class) {
                if (helper == null) {
                    helper = new NetHelper();
                }
            }
        }
        return helper;
    }

    private NetHelper() {
        this.service = Net.getIns().getRetrofit().create(IRequestGener.class);
    }

    public void request(String url, RequestType method, Map<String, Object> questMap, NetListener<String> listener) {
        request(url, method, questMap, "", listener);
    }

    public void requestBody(String url, RequestType method, Object body, NetListener<String> listener) {
        request(url, method, null, body, listener);
    }

    public void request(String url, RequestType method, Map<String, Object> questMap, Object body, NetListener<String> listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (questMap == null) {
            questMap = new HashMap<>();
        }
        if (method == RequestType.GET) {
            tk.hongbo.network.NetHelper.getIns().request(service.get(url, questMap), listener);
        } else if (method == RequestType.POST) {
            tk.hongbo.network.NetHelper.getIns().request(service.post(url, questMap), listener);
        } else if (method == RequestType.BODY) {
            tk.hongbo.network.NetHelper.getIns().request(service.body(url, body), listener);
        }
    }

    public enum RequestType {
        GET, POST, BODY;
    }
}
