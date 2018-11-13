package tk.hongbo.network;

import android.app.Application;
import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Net {

    private static final int cacheSize = 10 * 1024 * 1024;

    private static volatile Net net;

    public static void init(Application application, String baseUrl, INetProcess process) {
        AppInfo.application = application;
        AppInfo.baseUrl = baseUrl;
        AppInfo.process = process;
    }

    private Net() {
        if (AppInfo.retrofit == null) {
            if (AppInfo.okHttpClient == null) {
                AppInfo.okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .cache(new Cache(AppInfo.application.getExternalCacheDir().getAbsoluteFile(), cacheSize))
                        .addInterceptor(interceptor)
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build();
            }
            AppInfo.retrofit = new Retrofit.Builder()
                    .baseUrl(AppInfo.baseUrl)
                    .client(AppInfo.okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    public Retrofit getRetrofit() {
        return AppInfo.retrofit;
    }

    public Application getApplication() {
        return AppInfo.application;
    }

    public INetProcess getProcess() {
        return AppInfo.process;
    }

    public static Net getIns() {
        if (AppInfo.application == null) {
            throw new NullPointerException("Application cannot be empty, it must be initialized in the application of the APP first");
        }
        if (net == null) {
            synchronized (Net.class) {
                if (net == null) {
                    net = new Net();
                }
            }
        }
        return net;
    }

    private static class AppInfo {
        private static Application application;
        public static volatile Retrofit retrofit;
        private static volatile OkHttpClient okHttpClient;
        private static String baseUrl = "https://api5.huangbaoche.com/";
        private static INetProcess process;
    }

    static Interceptor interceptor = chain -> {
        Request original = chain.request();
        Request.Builder requestBuild = original.newBuilder();
        requestBuild.addHeader("Accept-Encoding", "identity");
        requestBuild.addHeader("os", "android");
        requestBuild.addHeader("ts", String.valueOf(System.currentTimeMillis()));
        if (AppInfo.process != null && AppInfo.process.getHeaders() != null) {
            for (String key : AppInfo.process.getHeaders().keySet()) {
                String value = AppInfo.process.getHeaders().get(key);
                if (!TextUtils.isEmpty(value)) {
                    requestBuild.addHeader(key, value);
                }
            }
        }
        return chain.proceed(requestBuild.build());
    };
}
