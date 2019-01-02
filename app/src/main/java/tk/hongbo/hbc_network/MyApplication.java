package tk.hongbo.hbc_network;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

import tk.hongbo.network.INetProcess;
import tk.hongbo.network.Net;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Net.init(this, "https://api5.huangbaoche.com/", new INetProcess() {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put("Accept-Encoding", "identity");
                map.put("appVersion", "7.4.0");
                map.put("idfa", "352936090691741");
                map.put("ak", "bb50c67b3f7d9143b77272da961de3f9");
                map.put("deviceId", "352936090691741");
                map.put("appChannel", "examination");
                map.put("ut", "5fd52c099a549f71f973dea09b422c8d");
                return map;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("channelId", "18");
                return map;
            }
        });
    }
}
