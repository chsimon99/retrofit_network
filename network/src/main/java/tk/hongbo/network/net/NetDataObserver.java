package tk.hongbo.network.net;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

/**
 * Created by HONGBO on 2018/4/30 17:46.
 */
public abstract class NetDataObserver<M> implements Observer<NetData<M>> {

    private Activity owner;

    public NetDataObserver(Activity owner) {
        this.owner = owner;
    }

    @Override
    public void onChanged(@Nullable NetData<M> root) {
        if (root.getNetStatus() == NetData.NET_STATUS_SUCCESS) {
            onSuccess(root.getData());
        } else {
            onFailure(root);
        }
    }

    public abstract void onSuccess(M data);

    /**
     * 请求错误处理
     *
     * @param root
     */
    public void onFailure(NetData<M> root) {
        //请求错误处理
        new NetErrorProcess(owner, root).show();
    }
}
