package tk.hongbo.network.other;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import tk.hongbo.network.data.NetRoot;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<NetRoot<R>>> {

    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<NetRoot<R>> adapt(Call<R> call) {
        final MutableLiveData<NetRoot<R>> liveDataResponse = new MutableLiveData<>();
        call.enqueue(new LiveDataResponseCallCallback(liveDataResponse));
        return liveDataResponse;
    }

    private static class LiveDataResponseCallCallback<R> implements Callback<R> {
        private final MutableLiveData<NetRoot<R>> liveData;

        LiveDataResponseCallCallback(MutableLiveData<NetRoot<R>> liveData) {
            this.liveData = liveData;
        }

        @Override
        public void onResponse(Call<R> call, Response<R> response) {
            if (call.isCanceled()) return;
            NetRoot<R> netRoot = new NetRoot<>();
            if (response.isSuccessful()) {
                netRoot = (NetRoot) response.body();
            } else {
                try {
                    netRoot.setMessage(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                netRoot.setData(null);
                netRoot.setStatus(response.code());
            }
            liveData.postValue(netRoot);
        }

        @Override
        public void onFailure(Call<R> call, Throwable t) {
            if (call.isCanceled()) return;
            NetRoot netRoot = new NetRoot<>();
            netRoot.setStatus(500);
            netRoot.setData(null);
            netRoot.setMessage(t.getMessage());
            liveData.postValue(netRoot);
        }
    }
}
