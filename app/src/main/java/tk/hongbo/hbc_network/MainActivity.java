package tk.hongbo.hbc_network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import tk.hongbo.hbc_network.entity.LastOfferLimitEntity;
import tk.hongbo.hbc_network.request.IRequestAccess;
import tk.hongbo.network.INetProcess;
import tk.hongbo.network.Net;
import tk.hongbo.network.net.NetCallback;
import tk.hongbo.network.net.NetData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Net.init(getApplication(), "https://api5.huangbaoche.com/", new INetProcess() {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> map = new HashMap<>();
                map.put("Accept-Encoding", "identity");
                map.put("appVersion", "7.2.0");
                map.put("idfa", "352936090691741");
                map.put("ak", "bb50c67b3f7d9143b77272da961de3f9");
                map.put("deviceId", "352936090691741");
                map.put("appChannel", "10006");
                map.put("ut", "ad21a746c9c50ff4dac6528ec03c1f47");
                return map;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("channelId", "18");
                return map;
            }

            @Override
            public void onErrorProcess(NetData netData) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        test();
    }

    private void test() {
        IRequestAccess service = Net.getIns().getRetrofit().create(IRequestAccess.class);
//        service.getSingleLastLimit(8).enqueue(new NetCallback<>((NetCallback.NetDataListener<LastOfferLimitEntity>) netData -> {
//            if (netData.getData() != null && netData.getData().getData() != null && netData.getData().getData().getLastOfferLimitTip() != null) {
//                Log.d("test", netData.getData().getData().getLastOfferLimitTip());
//            }
//        }));
        service.reportApp("100241", 1).enqueue(new NetCallback<>(netData -> Log.d("test", "已汇报数据")));
    }
}
