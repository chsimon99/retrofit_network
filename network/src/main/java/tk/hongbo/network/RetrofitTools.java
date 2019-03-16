package tk.hongbo.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tk.hongbo.network.bussiness.IRequestGener;
import tk.hongbo.network.net.NetListener;
import tk.hongbo.network.request.RetrofitRequest;
import tk.hongbo.network.utils.Utils;

public final class RetrofitTools {

    private static Retrofit.Builder retrofitBuilder;
    private static OkHttpClient.Builder okhttpBuilder;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static Context mContext;
    public static IRequestGener apiManager;
    public static BaseApiService apiService;
    private final String baseUrl;

    private static final int DEFAULT_TIMEOUT = 15;
    private static final int DEFAULT_MAXIDLE_CONNECTIONS = 5;
    private static final long DEFAULT_KEEP_ALIVEDURATION = 8;
    private static final long DEFAULT_CACHEMAXSIZE = 10 * 1024 * 1024;
    private static int DEFAULT_MAX_STALE = 60 * 60 * 24 * 3;

    RetrofitTools(String baseUrl,IRequestGener apiManager,BaseApiService apiService){
        this.baseUrl = baseUrl;
        this.apiManager = apiManager;
        this.apiService = apiService;
    }

    public static final class Builder{
        private int readTimeout = DEFAULT_TIMEOUT;
        private int writeTimeout = DEFAULT_TIMEOUT;
        private int default_maxidle_connections = DEFAULT_MAXIDLE_CONNECTIONS;
        private long default_keep_aliveduration = DEFAULT_KEEP_ALIVEDURATION;
        private long cacheMaxSize = DEFAULT_CACHEMAXSIZE;
        private int cacheTimeout = DEFAULT_MAX_STALE;
        private String baseUrl;
        private Boolean isLog = false;
        private Boolean isCache = true;
        private Context context;
        private Cache cache = null;
        private ConnectionPool connectionPool;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        //        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;
        //        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE;

        public Builder(Context context){
            okhttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            if(context instanceof Activity){
                this.context = context.getApplicationContext();
            }else {
                this.context = context;
            }
        }

        /**
         * 连接时间
         * @param timeout
         * @return
         */
        public Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 写入时间
         * @param timeout
         * @return
         */
        public Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 是否打印log
         * @param isLog
         * @return
         */
        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        /**
         * 是否添加缓存
         * @param isCache
         * @return
         */
        public Builder addCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        /**
         * 添加连接线程池
         * @param connectionPool
         * @return
         */
        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) {
                throw new NullPointerException("connectionPool == null");
            }
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * base url
         * @param baseUrl
         * @return
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Utils.checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        public Builder addConverterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        public <T> Builder header(Map<String, T> headers) {
            okhttpBuilder.addInterceptor(new BaseHeaderInterceptor(Utils.checkNotNull(headers, "header == null"), AbsRequestInterceptor.Type.UPDATE));
            return this;
        }

        public <T> Builder addHeader(Map<String, T> headers) {
            okhttpBuilder.addInterceptor(new BaseHeaderInterceptor(Utils.checkNotNull(headers, "header == null")));
            return this;
        }
        public <T> Builder parameters(Map<String, T> parameters) {
            okhttpBuilder.addInterceptor(new BaseParameters(Utils.checkNotNull(parameters, "parameters == null"), AbsRequestInterceptor.Type.UPDATE));
            return this;
        }
        public <T> Builder addParameters(Map<String, T> parameters) {
            okhttpBuilder.addInterceptor(new BaseParameters(Utils.checkNotNull(parameters, "parameters == null")));
            return this;
        }
        public Builder addInterceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
            return this;
        }
        public Builder addNetworkInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                throw new NullPointerException("interceptor == null");
            }
            okhttpBuilder.addNetworkInterceptor(interceptor);
            return this;
        }


        public RetrofitTools build(){
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            if (okhttpBuilder == null) {
                throw new IllegalStateException("okhttpBuilder required.");
            }

            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder required.");
            }
            mContext = context;
            retrofitBuilder.baseUrl(baseUrl);
            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            retrofitBuilder.addConverterFactory(converterFactory);
            if (callAdapterFactory == null) {
                callAdapterFactory = RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            if (isLog) {
                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            if (connectionPool == null) {
                connectionPool = new ConnectionPool(default_maxidle_connections, default_keep_aliveduration, TimeUnit.SECONDS);
            }
            okhttpBuilder.connectionPool(connectionPool);

            /**
             * create okHttpClient
             */
            okHttpClient = okhttpBuilder.build();
            /**
             * set Retrofit client
             */

            retrofitBuilder.client(okHttpClient);

            /**
             * create Retrofit
             */
            retrofit = retrofitBuilder.build();

            /**
             *create BaseApiService;
             */
            apiManager = retrofit.create(IRequestGener.class);
            apiService = retrofit.create(BaseApiService.class);

            return new RetrofitTools(baseUrl,apiManager,apiService);
        }

        public Builder connectTimeout(int timeout, TimeUnit unit) {
            this.readTimeout = Utils.checkDuration("timeout", timeout, unit);
            if (timeout >= 0) {
                this.readTimeout = timeout;
                okhttpBuilder.connectTimeout(readTimeout, unit);
            }
            return this;
        }
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            this.writeTimeout = timeout;
            if (timeout >= 0) {
                okhttpBuilder.writeTimeout(timeout, unit);
            }
            return this;
        }
    }

    public  <T> T execut(RetrofitRequest request, BaseSubscriber<T> subscriber){
        return (T)createRx(request).compose(schedulersTransformer).subscribe(subscriber);
    }

    private Observable<ResponseBody> createRx(RetrofitRequest request) {
        if (request.method().equals("GET")) {
            return apiService.executeGet(request.url(), request.params());
        }
        if (request.method().equals("POST")) {
            return apiService.executePost(request.url(), request.params());
        }

        if (request.method().equals("PUT")) {
            return apiService.executePut(request.url(), request.params());
        }

        if (request.method().equals("DELETE")) {
            return apiService.executeDelete(request.url(), request.params());
        }
        return apiService.executeGet(request.url(), request.params());
    }

    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {

        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    public void request(String url, RequestType method, Map<String, Object> questMap, NetListener<String> listener) {
        request(url, method, questMap, "", listener);
    }
    public void requestBody(String url, RequestType method, Object body, NetListener<String> listener) {
        request(url, method, null, body, listener);
    }

    private void request(String url, RequestType method, Map<String, Object> questMap, Object body, NetListener<String> listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (questMap == null) {
            questMap = new HashMap<>();
        }
        if (method == RequestType.GET) {
            tk.hongbo.network.NetHelper.getIns().request(apiManager.get(url, questMap), listener);
        } else if (method == RequestType.POST) {
            tk.hongbo.network.NetHelper.getIns().request(apiManager.post(url, questMap), listener);
        } else if (method == RequestType.BODY) {
            tk.hongbo.network.NetHelper.getIns().request(apiManager.body(url, body), listener);
        }
    }
    public enum RequestType {
        GET, POST, BODY
    }
}
