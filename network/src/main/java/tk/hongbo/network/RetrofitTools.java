package tk.hongbo.network;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tk.hongbo.network.bussiness.IRequestGener;
import tk.hongbo.network.callback.ResponseCallback;
import tk.hongbo.network.data.OssTokenBean;
import tk.hongbo.network.data.OssTokenKeyBean;
import tk.hongbo.network.net.NetListener;
import tk.hongbo.network.request.RetrofitRequest;
import tk.hongbo.network.request.RetrofitRequestBody;
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

    public static Context getmContext() {
        return mContext;
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
        private int maxRequest = 0;
        private int maxRequestsPerHost = 0;
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

        /**
         * 设置最大请求数
         * @param max
         * @return
         */
        public Builder setMaxRequest(int max){
            if(max > 0){
                maxRequest = max;
            }
            return this;
        }

        /**
         * 设置最大PerHost
         * @param max
         * @return
         */
        public Builder setMaxRequestsPerHost(int max){
            if(max > 0){
                maxRequestsPerHost = max;
            }
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
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            if (connectionPool == null) {
                connectionPool = new ConnectionPool(default_maxidle_connections, default_keep_aliveduration, TimeUnit.SECONDS);
            }
            okhttpBuilder.connectionPool(connectionPool);

            if(maxRequest > 0 && maxRequestsPerHost > 0){
                Dispatcher dispatcher = new Dispatcher();
                dispatcher.setMaxRequests(maxRequest);
                dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
                okhttpBuilder.dispatcher(dispatcher);
            }

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
//            this.readTimeout = Utils.checkDuration("timeout", timeout, unit);
            this.readTimeout = timeout;
            if (timeout >= 0) {
                okhttpBuilder.connectTimeout(readTimeout, unit);
                okhttpBuilder.readTimeout(readTimeout, unit);
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


    public <T> T json(String url, String jsonStr, BaseSubscriber<Response<ResponseBody>> subscriber){
        return (T)apiService.postRequestBody(url,Utils.createJson(jsonStr)).compose(schedulersTransformer).subscribe(subscriber);
    }

    public <T> T executResponse(RetrofitRequest request, BaseSubscriber<Response<ResponseBody>> subscriber){
        return (T)createRxResponse(request).compose(schedulersTransformer).subscribe(subscriber);
    }

    public <T> T execut(RetrofitRequest request, BaseSubscriber<T> subscriber){
        return (T)createRx(request).compose(schedulersTransformer).subscribe(subscriber);
    }

    private Observable<Response<ResponseBody>> createRxResponse(RetrofitRequest request) {
        if (request.method().equals("GET")) {
            return apiService.executeGet(request.url(),request.params());
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
        return apiService.executePost(request.url(), request.params());
    }

    private Observable<ResponseBody> createRx(RetrofitRequest request) {
        if (request.method().equals("POST")) {
            return apiService.post(request.url(), request.params());
        }
        if (request.method().equals("GET")) {
            return apiService.get(request.url(),request.params());
        }
        return apiService.post(request.url(), request.params());
    }

    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {

        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * RXJAVA schedulersTransformer
     * Schedulers.io()
     */
    final Observable.Transformer schedulersTransformerDown = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .subscribeOn(Schedulers.newThread())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    };

    public <T> T rxUpload(String url, ContentType type, File file, ResponseCallback<T, ResponseBody> callBack){
        return rxUpload(url,url,type,file,callBack);
    }

    private <T> T rxUpload(Object tag, String url, ContentType type, File file, ResponseCallback<T, ResponseBody> callBack) {
        if (!file.exists()) {
            throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
        }
        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }
        RetrofitRequestBody requestBody = Utils.createRequestBody(file, type, callBack);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(Utils.typeToStringName(type), file.getName(), requestBody);

        return rxUpload(tag, url, body, callBack);
    }

//    public <T> T rxUploadForAli(ContentType type, File file,OssTokenBean ossTokenBean, OssTokenKeyBean ossTokenKeyBean, ResponseCallback<T, ResponseBody> callBack) {
//        if (!file.exists()) {
//            throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
//        }
//        if (callBack == null) {
//            callBack = ResponseCallback.CALLBACK_DEFAULT;
//        }
//        RetrofitRequestBody requestBody = Utils.createRequestBody(file, type, callBack);
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("OSSAccessKeyId", ossTokenBean.getOssTokenParamBean().getOssAccessKeyId())
//                        .createFormData("policy", ossTokenBean.getOssTokenParamBean().getPolicy())
//                        .createFormData("Signature", ossTokenBean.getOssTokenParamBean().getSignature())
//                        .createFormData("key", ossTokenKeyBean.getKey())
//                        .createFormData("file", file.getName(), requestBody);
//        return rxUpload(ossTokenBean.getAddress(), ossTokenBean.getAddress(), body, callBack);
//    }

    public <T> T rxUploadForAli(String url,MultipartBody.Part body,ResponseCallback<T, ResponseBody> callBack){
        if (callBack == null) {
            callBack = ResponseCallback.CALLBACK_DEFAULT;
        }

        return rxUpload(url, url, body, callBack);
    }

    /**
     * Novate rxUpload by post With Part
     * @param tag         request tag
     * @param url         path or url
     * @param requestBody requestBody
     * @param callBack    ResponseCallback
     * @param <T>         T return parsed data
     * @return Subscription
     */
    private <T> T rxUpload(Object tag, String url, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiService.uploadFlieWithPart(url, requestBody)
                .compose(schedulersTransformer)
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    private Observable<ResponseBody> downObservable;
    private Map<Object, Observable<ResponseBody>> downMaps = new HashMap<Object, Observable<ResponseBody>>(){};
    /**
     * Rx Download file
     *
     * @param callBack
     */
    public <T> T rxDownload(String url, final ResponseCallback callBack) {
        return rxDownload(url, url, callBack);
    }

    /**
     * Rx Download file
     *
     * @param tag      request Tag
     * @param callBack
     */
    public <T> T rxDownload(Object tag, String url, final ResponseCallback callBack) {
        if (downMaps.get(tag) == null) {
            downObservable = apiService.downloadFile(url);
        } else {
            downObservable = downMaps.get(tag);
        }
        downMaps.put(tag, downObservable);
        return (T) downObservable.compose(schedulersTransformerDown)
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    public String getExecute(String url,Map<String, Object> map){
        try {
            Response<ResponseBody> response = apiService.getCall(url, map).execute();
            if (response.isSuccessful()){
                if ((response.body() instanceof ResponseBody)) {
                    String result = response.body().string();
                    return result;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }









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
            tk.hongbo.network.NetHelper.getIns().request(apiManager.get(url, questMap), url,listener);
        } else if (method == RequestType.POST) {
            tk.hongbo.network.NetHelper.getIns().request(apiManager.post(url, questMap), url,listener);
        } else if (method == RequestType.BODY) {
            tk.hongbo.network.NetHelper.getIns().request(apiManager.body(url, body), url,listener);
        }
    }
    public enum RequestType {
        GET, POST, BODY
    }
}
