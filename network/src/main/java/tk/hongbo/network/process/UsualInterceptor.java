package tk.hongbo.network.process;

import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import tk.hongbo.network.Net;

public class UsualInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuild = null;
        String method = oldRequest.method();
        if ("POST".equals(method)) {
            RequestBody oldBody = oldRequest.body();
            if (oldBody instanceof FormBody) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                addPostParams(formBodyBuilder);
                RequestBody formBody = formBodyBuilder.build();

                newRequestBuild = oldRequest.newBuilder();
                String postBodyString = bodyToString(oldRequest.body());
                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                newRequestBuild.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
            } else if (oldBody instanceof MultipartBody) {
                MultipartBody oldBodyMultipart = (MultipartBody) oldBody;
                List<MultipartBody.Part> oldPartList = oldBodyMultipart.parts();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                addPostMultipartBody(builder);
                for (MultipartBody.Part part : oldPartList) {
                    builder.addPart(part);
                }
                newRequestBuild = oldRequest.newBuilder();
                newRequestBuild.post(builder.build());
            } else {
                newRequestBuild = oldRequest.newBuilder();
            }
        } else {
            // 添加新的参数
            HttpUrl.Builder commonParamsUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host());
            addGetParams(commonParamsUrlBuilder);
            newRequestBuild = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(commonParamsUrlBuilder.build());
        }
        Request newRequest = newRequestBuild.build();
        okhttp3.Response response = chain.proceed(newRequest);
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }

    private void addPostMultipartBody(MultipartBody.Builder builder) {
        if (Net.getIns().getProcess() != null && Net.getIns().getProcess().getParams() != null
                && !Net.getIns().getProcess().getParams().isEmpty()) {
            for (String key : Net.getIns().getProcess().getParams().keySet()) {
                String value = Net.getIns().getProcess().getParams().get(key);
                if (!TextUtils.isEmpty(value)) {
                    RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), value);
                    builder.addPart(requestBody1);
                }
            }
        }
    }

    private void addPostParams(FormBody.Builder formBodyBuilder) {
        if (Net.getIns().getProcess() != null && Net.getIns().getProcess().getParams() != null
                && !Net.getIns().getProcess().getParams().isEmpty()) {
            for (String key : Net.getIns().getProcess().getParams().keySet()) {
                String value = Net.getIns().getProcess().getParams().get(key);
                if (!TextUtils.isEmpty(value)) {
                    formBodyBuilder.add(key, value);
                }
            }
        }
    }

    private void addGetParams(HttpUrl.Builder commonParamsUrlBuilder) {
        if (Net.getIns().getProcess() != null && Net.getIns().getProcess().getParams() != null
                && !Net.getIns().getProcess().getParams().isEmpty()) {
            for (String key : Net.getIns().getProcess().getParams().keySet()) {
                String value = Net.getIns().getProcess().getParams().get(key);
                if (!TextUtils.isEmpty(value)) {
                    commonParamsUrlBuilder.addQueryParameter(key, value);
                }
            }
        }
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
