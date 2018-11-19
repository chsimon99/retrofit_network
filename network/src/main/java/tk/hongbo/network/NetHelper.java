package tk.hongbo.network;

import retrofit2.Call;
import tk.hongbo.network.net.NetCallback;
import tk.hongbo.network.net.NetListener;

public class NetHelper<R, M> {

    private static volatile NetHelper netRequest;

    public static NetHelper getIns() {
        if (netRequest == null) {
            synchronized (NetHelper.class) {
                if (netRequest == null) {
                    netRequest = new NetHelper();
                }
            }
        }
        return netRequest;
    }

    public void request(Call<R> call, NetListener<M> listener) {
        call.enqueue(new NetCallback<R, M>(new NetCallback.NetDataListener<M>() {

            @Override
            public void onSuccess(M t) {
                if (listener != null) {
                    listener.onSuccess(t);
                }
            }

            @Override
            public void onBusinessError(int status, String message) {
                if (listener != null) {
                    listener.onFailure(status, message, null);
                }
            }

            @Override
            public void onServiceError(int status) {
                if (listener != null) {
                    listener.onFailure(status, "", new IllegalStateException("Server Exception"));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (listener != null) {
                    listener.onFailure(0, "", t);
                }
            }
        }));
    }
}
