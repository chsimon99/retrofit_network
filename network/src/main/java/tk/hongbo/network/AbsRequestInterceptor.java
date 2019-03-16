package tk.hongbo.network;

import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.Request;

public abstract class AbsRequestInterceptor implements Interceptor {
    public enum Type {
        ADD, UPDATE, REMOVE
    }

    public Type control;

    public Type getControlType() {
        return control;
    }

    public void setControlType(Type control) {
        this.control = control;
    }

    abstract Request interceptor(Request request) throws UnsupportedEncodingException;
}
