package tk.hongbo.network.net;

import android.text.TextUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import tk.hongbo.network.Net;
import tk.hongbo.network.data.NetRaw;
import tk.hongbo.network.utils.ToastUtils;

public abstract class NetListener<M> implements NetRequestListener<M> {
    @Override
    public void onFailure(int status, String message, Throwable t, NetRaw netRaw) {
        if (TextUtils.isEmpty(message)) {
            onError(status, t);
        } else {
            ToastUtils.makeToast(Net.getIns().getApplication(), message).show();
        }
    }

    private void onError(int code, Throwable t) {
        String msg;
        if (code > 0) {
            msg = "服务器忙翻了(" + code + ")";
        } else {
            msg = handleException(t);
        }
        ToastUtils.makeToast(Net.getIns().getApplication(), msg).show();
    }

    private String handleException(Throwable error) {
        String msg = "访问API错误";
        if (error instanceof SocketTimeoutException) {
            msg = "网络超时";
        } else if (error instanceof ConnectTimeoutException) {
            msg = "连接超时";
        } else if (error instanceof MalformedURLException) {
            msg = "访问地址异常";
        } else if (error instanceof SocketException) {
            msg = "服务器状态异常";
        } else if (error instanceof SSLHandshakeException) {
            msg = "安全证书异常";
        } else if (error instanceof UnknownHostException) {
            msg = "不能访问服务器地址";
        }
        return msg;
    }
}
