package tk.hongbo.network.net;

import tk.hongbo.network.Net;
import tk.hongbo.network.utils.ToastUtils;

public abstract class NetListener<M> implements NetRequestListener<M> {

    @Override
    public void onFailure(M m, String error) {
        ToastUtils.makeToast(Net.getIns().getApplication(), error).show();
    }

    @Override
    public void onError(int code, Throwable t) {
        String msg = "访问API错误";
        if (code > 0) {
            msg = "服务器忙翻了(" + code + ")";
        }
        ToastUtils.makeToast(Net.getIns().getApplication(), msg).show();
    }
}
