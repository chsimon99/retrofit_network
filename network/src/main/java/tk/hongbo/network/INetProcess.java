package tk.hongbo.network;

import java.util.Map;

import tk.hongbo.network.net.NetData;

public interface INetProcess {

    Map<String, String> getHeaders();

    void onErrorProcess(NetData netData); //网络请求错误处理
}
