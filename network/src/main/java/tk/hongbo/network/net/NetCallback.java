package tk.hongbo.network.net;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.hongbo.network.data.BaseEntiry;
import tk.hongbo.network.utils.ApiReportHelper;

public class NetCallback<T> implements Callback<T> {

    private NetDataListener listener;

    public NetCallback(NetDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        ApiReportHelper.getInstance().addReport(response);
        if (response.isSuccessful()) {
            if (response.body() instanceof BaseEntiry) {
                if (((BaseEntiry) response.body()).getStatus() == 200) {
                    if (listener != null) {
                        listener.onSuccess(response.body(), call, response);
                    }
                } else {
                    if (listener != null) {
                        listener.onBusinessError(response.body(), call, response);
                    }
                }
            } else {
                try {
                    throw new IllegalAccessException("The Entity must extends BaseEntity!");
                } catch (IllegalAccessException e) {
                    if (listener != null) {
                        listener.onFailure(call, e);
                    }
                }
            }
        } else {
            if (listener != null) {
                listener.onServiceError(response.code(), call, response);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (listener != null) {
            listener.onFailure(call, t);
        }
    }

    public interface NetDataListener<T> {
        void onSuccess(T t, Call<T> call, Response<T> response);

        void onBusinessError(T t, Call<T> call, Response<T> response);

        void onServiceError(int code, Call<T> call, Response<T> response);

        void onFailure(Call<T> call, Throwable t);
    }
}
