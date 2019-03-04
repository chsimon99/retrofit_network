package tk.hongbo.network.net;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.hongbo.network.data.NetRoot;
import tk.hongbo.network.data.NetRaw;
import tk.hongbo.network.utils.ApiReportHelper;
import tk.hongbo.network.utils.Log;

public class NetCallback<R, B> implements Callback<R> {

    private NetDataListener listener;

    public NetCallback(NetDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<R> call, Response<R> response) {
        try {
            ApiReportHelper.getInstance().addReport(response);
            NetRaw netRaw = new NetRaw();
            try {
                netRaw = new NetRaw(response.raw().sentRequestAtMillis(), response.raw().receivedResponseAtMillis(),
                        response.raw().request().headers().toMultimap(), response.raw().headers().toMultimap());
            } catch (Exception ex) {
            }
            if (response.isSuccessful()) {
                if ((response.body() instanceof ResponseBody)) {
                    try {
                        String result = ((ResponseBody) response.body()).string();
                        if (listener != null) {
                            listener.onSuccess(result, netRaw);
                        }
                    } catch (IOException e) {
                        if (listener != null) {
                            listener.onSuccess("", netRaw);
                        }
                        Log.e("Get result info failure", e);
                    }
                    return;
                }
                if (!(response.body() instanceof NetRoot)) {
                    try {
                        throw new IllegalAccessException("Call must be a NetRoot type!");
                    } catch (IllegalAccessException e) {
                        Log.e("Does not support non-NetRoot format", e);
                        if (listener != null) {
                            listener.onFailure(e, netRaw);
                        }
                    }
                    return;
                }
                NetRoot<B> netRoot = (NetRoot) response.body();
                if (netRoot.getStatus() == 200) {
                    if (listener != null) {
                        listener.onSuccess(netRoot.getData(), netRaw);
                    }
                } else {
                    if (listener != null) {
                        listener.onBusinessError(netRoot.getStatus(), netRoot.getMessage(), netRaw);
                    }
                }
            } else {
                if (listener != null) {
                    listener.onServiceError(response.code(), netRaw);
                }
            }
        } catch (Exception e) {
            Log.e("Data processing exception", e);
        }
    }

    @Override
    public void onFailure(Call<R> call, Throwable t) {
        Log.e("Net error", t);
        if (listener != null) {
            NetRaw netRaw = new NetRaw();
            try {
                netRaw = new NetRaw(Long.valueOf(call.request().header("ts")), System.currentTimeMillis(),
                        call.request().headers().toMultimap(), null);
            } catch (Exception e) {
            }
            listener.onFailure(t, netRaw);
        }
    }

    public interface NetDataListener<B> {
        void onSuccess(B t, NetRaw netRaw);

        void onBusinessError(int status, String message, NetRaw netRaw);

        void onServiceError(int status, NetRaw netRaw);

        void onFailure(Throwable t, NetRaw netRaw);
    }
}
