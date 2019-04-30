package tk.hongbo.network.request;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.internal.http.HttpMethod;

public final class RetrofitRequest {

    private static RetrofitRequest.Builder builder;
    private static Map<String, Object> params;
    private final String url;
    private final String method;
    private final Headers headers;

    private RetrofitRequest(RetrofitRequest.Builder builder){
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.params = builder.params;

    }

    public String url() {
        return url;
    }

    public String method() {
        return method;
    }

    public Headers headers() {
        return headers;
    }

    public String header(String name) {
        return headers.get(name);
    }

    public List<String> headers(String name) {
        return headers.values(name);
    }

    public Map<String, Object> params() {
        return params;
    }

    public static class Builder{
        private String url;
        private String method;
        private Headers.Builder headers;
        private Map<String, Object> params;

        public Builder() {
            this.method = "GET";
            this.headers = new Headers.Builder();
            this.params = new HashMap<>();
        }

        public RetrofitRequest.Builder url(HttpUrl url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            this.url = url.url().toString();
            return this;
        }

        public RetrofitRequest.Builder url(String url) {
//            if (url == null) {
//                throw new NullPointerException("url == null");
//            }
//            // Silently replace websocket URLs with HTTP URLs.
//            if (url.regionMatches(true, 0, "ws:", 0, 3)) {
//                url = "http:" + url.substring(3);
//            } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
//                url = "https:" + url.substring(4);
//            }
//
//            HttpUrl parsed = HttpUrl.parse(url);
//            if (parsed == null) {
//                throw new IllegalArgumentException("unexpected url: " + url);
//            }
//            return url(parsed);
            this.url = url;
            return this;
        }

        public RetrofitRequest.Builder url(URL url) {
            if (url == null) {
                throw new NullPointerException("url == null");
            }
            HttpUrl parsed = HttpUrl.get(url);
            if (parsed == null) {
                throw new IllegalArgumentException("unexpected url: " + url);
            }
            return url(parsed);
        }

        public RetrofitRequest.Builder setHeader(String name, String value) {
            headers.set(name, value);
            params.put(name,value);
            return this;
        }

        public RetrofitRequest.Builder addHeader(String name, String value) {
            headers.add(name, value);
            params.put(name,value);
            return this;
        }

        public RetrofitRequest.Builder removeHeader(String name) {
            headers.removeAll(name);
            params.remove(name);
            return this;
        }

        public RetrofitRequest.Builder addParams(Map<String, String> headers) {
            if (headers != null && headers.size() > 0) {
                Set<String> keys = headers.keySet();
                for (String headerKey : keys) {
                    this.params.put(headerKey, headers.get(headerKey) == null ? "" : headers.get(headerKey));
                }
            }
            return this;
        }

        public RetrofitRequest.Builder addHeaders(Map<String, String> headers) {
            if (headers != null && headers.size() > 0) {
                Set<String> keys = headers.keySet();
                for (String headerKey : keys) {
                    this.headers.add(headerKey, headers.get(headerKey) == null ? "" : headers.get(headerKey));
                    this.params.put(headerKey, headers.get(headerKey) == null ? "" : headers.get(headerKey));
                }
            }
            return this;
        }

        public RetrofitRequest.Builder get() {
            return method("GET", null);
        }

        public RetrofitRequest.Builder head() {
            return method("HEAD", null);
        }

        public RetrofitRequest.Builder post(Map<String, Object> body) {
            return method("POST", body);
        }

        public RetrofitRequest.Builder delete(Map<String, Object> body) {
            return method("DELETE", body);
        }

        public RetrofitRequest.Builder method(String method, Map<String, Object> body) {
            if (method == null) {
                throw new NullPointerException("method == null");
            }
            if (method.length() == 0) {
                throw new IllegalArgumentException("method.length() == 0");
            }
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            }
            if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            }
            this.method = method;
            if(body!= null){
                this.params = body;
            }
            return this;
        }

        public RetrofitRequest build() {
            if (url == null) {
                throw new IllegalStateException("url == null");
            }
            return new RetrofitRequest(this);
        }
    }
}
