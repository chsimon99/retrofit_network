package tk.hongbo.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import okhttp3.Request;
import okhttp3.Response;

public class BaseHeaderInterceptor<T> extends AbsRequestInterceptor {
    private Map<String, T> headers;


    public BaseHeaderInterceptor(Map<String, T> headers) {
        this(headers, Type.ADD);

    }

    public BaseHeaderInterceptor(Map<String, T> headers, Type type) {
        this.headers = headers;
        super.control = type;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        return chain.proceed(interceptor(chain.request()));

    }

    private static String getValueEncoded(String value) throws UnsupportedEncodingException {
        if (value == null) {
            return "null";
        }
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return URLEncoder.encode(newValue, "UTF-8");
            }
        }
        return newValue;
    }

    @Override
    Request interceptor(Request request) throws UnsupportedEncodingException {

        Request.Builder builder = request.newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            switch (super.control) {
                case ADD:
                    for (String headerKey : keys) {
                        builder.addHeader(headerKey, headers.get(headerKey) == null ? "" : getValueEncoded((String) headers.get(headerKey))).build();
                    }
                    break;
                case UPDATE:
                    for (String headerKey : keys) {
                        builder.header(headerKey, headers.get(headerKey) == null ? "" : getValueEncoded((String) headers.get(headerKey))).build();
                    }
                case REMOVE:
                    break;
            }
        }
        return builder.build();
    }
}
