package tk.hongbo.network.net;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.hongbo.network.data.NetRoot;
import tk.hongbo.network.utils.ApiReportHelper;
import tk.hongbo.network.utils.LogHelper;

public class NetCallback<R, B> implements Callback<R> {

    private NetDataListener listener;

    public NetCallback(NetDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<R> call, Response<R> response) {
        try {
            ApiReportHelper.getInstance().addReport(response);
            if (response.isSuccessful()) {
                if ((response.body() instanceof ResponseBody)) {
                    try {
                        String result = ((ResponseBody) response.body()).string();
                        if (listener != null) {
                            listener.onSuccess(result);
                        }
                    } catch (IOException e) {
                        if (listener != null) {
                            listener.onSuccess("");
                        }
                        LogHelper.e("Get result info failure", e);
                    }
                    return;
                }
                if (!(response.body() instanceof NetRoot)) {
                    try {
                        throw new IllegalAccessException("Call must be a NetRoot type!");
                    } catch (IllegalAccessException e) {
                        LogHelper.e("Does not support non-NetRoot format", e);
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    }
                    return;
                }
                NetRoot<B> netRoot = (NetRoot) response.body();
                if (netRoot.getStatus() == 200) {
                    if (listener != null) {
                        listener.onSuccess(netRoot.getData());
                    }
                } else {
                    if (listener != null) {
                        listener.onBusinessError(netRoot.getStatus(), netRoot.getMessage());
                    }
                }
            } else {
                if (listener != null) {
                    listener.onServiceError(response.code());
                }
            }
        } catch (Exception e) {
            LogHelper.e("Data processing exception", e);
        }
    }

    @Override
    public void onFailure(Call<R> call, Throwable t) {
        LogHelper.e("Net error", t);
        if (listener != null) {
            listener.onFailure(t);
        }
    }

    public interface NetDataListener<B> {
        void onSuccess(B t);

        void onBusinessError(int status, String message);

        void onServiceError(int status);

        void onFailure(Throwable t);
    }
}
