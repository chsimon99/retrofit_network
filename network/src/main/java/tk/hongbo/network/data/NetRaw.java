package tk.hongbo.network.data;

import java.util.List;
import java.util.Map;

/**
 * 网络请求中透传数据
 */
public class NetRaw {
    private long sentRequestAtMillis; //真正发起请求时间戳
    private long receivedResponseAtMillis; //真正接收时间戳
    private Map<String, List<String>> requestHaders; //请求头信息
    private Map<String, List<String>> responseHaders; //接收头信息

    public NetRaw() {
    }

    public NetRaw(long sentRequestAtMillis, long receivedResponseAtMillis, Map<String,
            List<String>> requestHaders, Map<String, List<String>> responseHaders) {
        this.sentRequestAtMillis = sentRequestAtMillis;
        this.receivedResponseAtMillis = receivedResponseAtMillis;
        this.requestHaders = requestHaders;
        this.responseHaders = responseHaders;
    }

    public long getSentRequestAtMillis() {
        return sentRequestAtMillis;
    }

    public long getReceivedResponseAtMillis() {
        return receivedResponseAtMillis;
    }

    public Map<String, List<String>> getRequestHaders() {
        return requestHaders;
    }

    public Map<String, List<String>> getResponseHaders() {
        return responseHaders;
    }
}
