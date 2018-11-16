package tk.hongbo.network;

import retrofit2.Call;
import retrofit2.Response;
import tk.hongbo.network.data.BaseEntiry;
import tk.hongbo.network.net.NetCallback;
import tk.hongbo.network.net.NetListener;

public class NetHelper<R> {

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

    public void request(Call<R> call, NetListener<R> listener) {
        call.enqueue(new NetCallback<>(new NetCallback.NetDataListener<R>() {
            @Override
            public void onSuccess(R r, Call<R> call, Response<R> response) {
                if (listener != null) {
                    listener.onSuccess(r);
                }
            }

            @Override
            public void onBusinessError(R r, Call<R> call, Response<R> response) {
                if (listener != null) {
                    listener.onFailure(r, ((BaseEntiry) r).getMessage());
                }
            }

            @Override
            public void onServiceError(int code, Call call, Response response) {
                if (listener != null) {
                    listener.onError(code, null);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (listener != null) {
                    listener.onError(0, t);
                }
            }
        }));
    }
}
