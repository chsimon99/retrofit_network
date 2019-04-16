package tk.hongbo.hbc_network;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import tk.hongbo.network.BaseSubscriber;
import tk.hongbo.network.RetrofitTools;
import tk.hongbo.network.callback.RxFileCallBack;
import tk.hongbo.network.request.RetrofitRequest;
import tk.hongbo.network.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    String baseUrl = "https://api5-test.huangbaoche.com/";
    int poolNum = 5;
    int keepAlive = 5;
    private RetrofitTools mTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    private void resquestByOkhttp() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        Map<String, Object> param = new HashMap<>();
        param.put("areaCode","86");
        param.put("mobile","18210101010");
        param.put("password","a000000");


        headers.put("ak", "94b44ff84e6c81ca6a4f5452f6ab7a6e");
        headers.put("ut", "76b8866eede9f9765963200baceadb25");
        headers.put("ver", "5.1.0");
        headers.put("ts", String.valueOf(System.currentTimeMillis()));
        headers.put("idfa", "866697029636913");
        headers.put("os", "1");
        headers.put("lc", "ch");
        headers.put("source", "g");
        headers.put("appChannel", "adnroid");
        headers.put("osVersion", "6.0");


        //                .addInterceptor(null)
        //                .addNetworkInterceptor(null)
        //默认5个线程池
        //默认不填就是RxJavaCallAdapterFactory
        //默认不填就是GsonConverterFactory
        //默认false
        mTools = new RetrofitTools.Builder(this)
                .connectTimeout(30)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .addHeader(headers)
//                .addInterceptor(null)
//                .addNetworkInterceptor(null)
                .connectionPool(new ConnectionPool(poolNum, keepAlive, TimeUnit.MINUTES))//默认5个线程池
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//默认不填就是RxJavaCallAdapterFactory
                .addConverterFactory(GsonConverterFactory.create())//默认不填就是GsonConverterFactory
                .addLog(false)//默认false
                .build();

//        tools.request("ucenter/v1.0/c/user/information", RetrofitTools.RequestType.POST, null, new NetListener<String>() {
//            @Override
//            public void onSuccess(String s, NetRaw netRaw) {
//                Log.e("------------","------onSuccess----=="+s.toString());
//            }
//        });
//        CallApiManager.get().cancel("ucenter/v1.0/c/user/information");
        RetrofitRequest request = new RetrofitRequest.Builder()
                .addHeaders(headers)
                .get()
                .url("fund/v1.3/g/account/balances")
//                .post(param)//get请求用.get()
//                .url("supplier/v1.2/g/guides/register/checkPassword")
                .build();

//        tools.execut(request, new BaseSubscriber<ResponseBody>() {
//            @Override
//            public void onNext(ResponseBody responseBody) {
//                try {
//                    Log.e("------------","---execut--next-----=="+responseBody.string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e("------------","----------=="+e.getMessage());
//            }
//        });
        Subscription subscription = mTools.executResponse(request, new BaseSubscriber<Response<ResponseBody>>() {
            @Override
            public void onNext(Response<ResponseBody> response) {
                Log.e("------------","----executResponse---next-----=="+response.headers().toString());
                Log.e("------------","----executResponse---next-----=="+response.code());
                Log.e("------------","----executResponse---next-----=="+response.raw().request().url());
                try {
                    Log.e("------------","----executResponse---next-----=="+response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("------------","----executResponse---Throwable-----=="+e.getMessage());
                super.onError(e);
            }

        });
//        subscription.unsubscribe();//取消网络请求
    }

    @Override
    protected void onStart() {
        super.onStart();
        test();
    }

    private void test() {
        viewModel.getOfferLimit().observe(this, data -> {
            Log.d("test", "Finish");
        });
    }

    public void upload(View view) {
        viewModel.upload(getExternalFilesDir("test") + File.separator + "test.png");
    }

    public void download(View view) {
        String localPath = getExternalFilesDir("testt") + File.separator + "ttt.png";
//        viewModel.download("https://www.baidu.com/img/bd_logo1.png", localPath);

        String downUrl = "http://wap.dl.pinyin.sogou.com/wapdl/hole/201512/03/SogouInput_android_v7.11_sweb.apk";

        XXPermissions.with(this).permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        mTools.rxDownload(downUrl, new RxFileCallBack(Utils.getBasePath(MainActivity.this),"retrofittest.apk") {
                            @Override
                            public void onNext(Object tag, File file) {
                                Toast.makeText(MainActivity.this, "下载成功!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(Object tag, float progress, long downloaded, long total) {
                                Log.i("----------","-----onProgress----=="+progress);
                            }

                            @Override
                            public void onError(Object tag, Exception e) {

                            }

                            @Override
                            public void onCancel(Object tag, Exception e) {

                            }
                        });
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });

    }

    /**
     * 通用方式获取数据
     *
     * @param view
     */
    public void getData(View view) {
        String url = "ucenter/v1.0/c/user/information";
//        NetHelper.get().request(url, NetHelper.RequestType.POST, null, new NetListener<String>() {
//            @Override
//            public void onSuccess(String s, NetRaw netRaw) {
//                Log.d("test", s);
//            }
//        });

        resquestByOkhttp();
    }
}
