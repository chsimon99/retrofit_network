package tk.hongbo.network.net;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.hongbo.network.data.NetRoot;
import tk.hongbo.network.utils.ApiReportHelper;

public class NetCallback<ROOT, B> implements Callback<ROOT> {

    private NetDataListener listener;

    public NetCallback(NetDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<ROOT> call, Response<ROOT> response) {
        ApiReportHelper.getInstance().addReport(response);
        if (response.isSuccessful()) {
            if ((response.body() instanceof ResponseBody)) {
                try {
                    listener.onSuccess(((ResponseBody) response.body()).string());
                } catch (IOException e) {
                    listener.onSuccess("");
                }
                return;
            }
            if (!(response.body() instanceof NetRoot)) {
                try {
                    throw new IllegalAccessException("Call must be a NetRoot type!");
                } catch (IllegalAccessException e) {
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
    }

    @Override
    public void onFailure(Call<ROOT> call, Throwable t) {
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
