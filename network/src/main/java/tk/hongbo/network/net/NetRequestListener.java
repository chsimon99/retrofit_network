package tk.hongbo.network.net;

public interface NetRequestListener<M> {

    void onSuccess(M m);

    void onFailure(M m, String error);

    void onError(int code, Throwable t);
}
