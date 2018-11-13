package tk.hongbo.network.net;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.Call;

public class NetData<T> {

    public static final int NET_STATUS_SUCCESS = 0;
    public static final int NET_STATUS_FAILURE = -1;

    private int netStatus;
    private T data;
    private Call<T> call;
    private Throwable t;

    public NetData(@NetData.NetStatus int netStatus, T data, Call<T> call, Throwable t) {
        this.netStatus = netStatus;
        this.data = data;
        this.call = call;
        this.t = t;
    }

    @IntDef({NET_STATUS_SUCCESS, NET_STATUS_FAILURE})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface NetStatus {
    }

    public int getNetStatus() {
        return netStatus;
    }

    public void setNetStatus(int netStatus) {
        this.netStatus = netStatus;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Call<T> getCall() {
        return call;
    }

    public void setCall(Call<T> call) {
        this.call = call;
    }

    public Throwable getT() {
        return t;
    }

    public void setT(Throwable t) {
        this.t = t;
    }
}
