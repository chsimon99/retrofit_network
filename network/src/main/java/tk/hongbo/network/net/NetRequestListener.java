package tk.hongbo.network.net;

public interface NetRequestListener<M> {

    void onSuccess(M m);

    void onFailure(int status, String message, Throwable t);
}
