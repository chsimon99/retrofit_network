package tk.hongbo.network.net;

import tk.hongbo.network.data.NetRaw;

public interface NetRequestListener<M> {

    void onSuccess(M m, NetRaw netRaw);

    void onFailure(int status, String message, Throwable t, NetRaw netRaw);
}
