package tk.hongbo.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class BaseParameters<T> extends AbsRequestInterceptor {

    private Map<String, T> parameters;


    public BaseParameters(Map<String, T> headers) {
        this(headers, Type.ADD);
    }

    public BaseParameters(Map<String, T> headers, Type type) {
        this.parameters = headers;
        control = type;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(interceptor(chain.request()));

    }

    @Override
    Request interceptor(Request original) throws UnsupportedEncodingException {
        Request.Builder builder1 = original.newBuilder();
        HttpUrl originalHttpUrl = original.url();
        HttpUrl.Builder builder = originalHttpUrl.newBuilder();

        if (parameters != null && parameters.size() > 0) {
            Set<String> keys = parameters.keySet();
            switch (super.control) {
                case ADD:
                    for (String headerKey : keys) {
                        builder.addQueryParameter(headerKey, parameters.get(headerKey) == null ? "" : (String) parameters.get(headerKey)).build();
                    }
                    break;
                case UPDATE:
                    for (String headerKey : keys) {
                        builder.setQueryParameter(headerKey, parameters.get(headerKey) == null ? "" : (String) parameters.get(headerKey)).build();
                    }
                case REMOVE:
                    for (String headerKey : keys) {
                        builder.removeAllQueryParameters(headerKey).build();
                    }
                    break;
            }

        }
        HttpUrl url = builder.build();
        Request.Builder requestBuilder = original.newBuilder()
                .url(url);
        return requestBuilder.build();
    }
}
