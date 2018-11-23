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
import tk.hongbo.network.other.LiveDataCallAdapterFactory;
import tk.hongbo.network.process.UsualInterceptor;
import tk.hongbo.network.utils.Log;

public class Net {

    private static final int cacheSize = 10 * 1024 * 1024;

    private static volatile Net net;

    public static void init(Application application, String baseUrl, INetProcess process) {
        AppInfo.application = application;
        AppInfo.baseUrl = baseUrl;
        AppInfo.process = process;
    }

    public static void init(Application application, String baseUrl, INetProcess process, boolean showLogging) {
        init(application, baseUrl, process);
        AppInfo.showLogging = showLogging;
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
                        .addInterceptor(new UsualInterceptor())
                        .addInterceptor(httpLoggingInterceptor.setLevel(AppInfo.showLogging ?
                                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                        .build();
            }
            AppInfo.retrofit = new Retrofit.Builder()
                    .baseUrl(AppInfo.baseUrl)
                    .client(AppInfo.okHttpClient)
                    .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
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

    public void setDebug(boolean isDebug) {
        AppInfo.isDebug = isDebug;
        Log.setIsDebug(isDebug);
    }

    public boolean isDebug() {
        return AppInfo.isDebug;
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
        private static volatile boolean isDebug = true;
        public static volatile Retrofit retrofit;
        private static volatile OkHttpClient okHttpClient;
        private static String baseUrl = "https://api5.huangbaoche.com/";
        private static INetProcess process;
        private static volatile boolean showLogging = true;
    }

    static Interceptor interceptor = chain -> {
        Request original = chain.request();
        //通用Header
        Request.Builder requestBuild = original.newBuilder();
        requestBuild.addHeader("Accept-Encoding", "identity");
        requestBuild.addHeader("os", "android");
        requestBuild.addHeader("ts", String.valueOf(System.currentTimeMillis()));
        if (AppInfo.process != null && AppInfo.process.getHeaders() != null && !AppInfo.process.getHeaders().isEmpty()) {
            for (String key : AppInfo.process.getHeaders().keySet()) {
                String value = AppInfo.process.getHeaders().get(key);
                if (!TextUtils.isEmpty(value)) {
                    requestBuild.addHeader(key, value);
                }
            }
        }
        return chain.proceed(requestBuild.build());
    };

    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> {
        Log.d(message);
    });
}
