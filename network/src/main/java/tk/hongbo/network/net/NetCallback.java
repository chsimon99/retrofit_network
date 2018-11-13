package tk.hongbo.network.net;

import android.arch.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tk.hongbo.network.utils.ApiReportHelper;

public class NetCallback<T> implements Callback<T> {

    private NetDataListener listener;
    private MutableLiveData<NetData<T>> liveData;

    public NetCallback(NetDataListener listener) {
        this.listener = listener;
    }

    public NetCallback(MutableLiveData<NetData<T>> liveData) {
        this.liveData = liveData;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        pushMessage(new NetData<>(NetData.NET_STATUS_SUCCESS, response.body(), call, null));
        ApiReportHelper.getInstance().addReport(response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        pushMessage(new NetData<>(NetData.NET_STATUS_FAILURE, null, call, t));
    }

    public interface NetDataListener<T> {
        void onResult(NetData<T> netData);
    }

    private void pushMessage(NetData netData) {
        if (liveData != null) {
            liveData.postValue(netData);
        }
        if (listener != null) {
            listener.onResult(netData);
        }
    }
}
