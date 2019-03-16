package tk.hongbo.hbc_network;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.hongbo.network.BaseSubscriber;
import tk.hongbo.network.RetrofitTools;
import tk.hongbo.network.data.NetRaw;
import tk.hongbo.network.helper.NetHelper;
import tk.hongbo.network.net.NetListener;
import tk.hongbo.network.request.RetrofitRequest;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    String baseUrl = "https://api5.huangbaoche.com/";
    int poolNum = 5;
    int keepAlive = 5;

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
        headers.put("Accept", "application/json");


        RetrofitTools tools = new RetrofitTools.Builder(this)
                .connectTimeout(30)
                .writeTimeout(15)
                .baseUrl(baseUrl)
                .addHeader(headers)
                .addInterceptor(null)
                .addNetworkInterceptor(null)
                .connectionPool(new ConnectionPool(poolNum, keepAlive, TimeUnit.MINUTES))//默认5个线程池
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//默认不填就是RxJavaCallAdapterFactory
                .addConverterFactory(GsonConverterFactory.create())//默认不填就是GsonConverterFactory
                .addLog(true)//默认false
                .build();

        tools.request("ucenter/v1.0/c/user/information", RetrofitTools.RequestType.POST, null, new NetListener<String>() {
            @Override
            public void onSuccess(String s, NetRaw netRaw) {
                Log.e("------------","------onSuccess----=="+s.toString());
            }
        });
        RetrofitRequest request = new RetrofitRequest.Builder()
                .headers(headers)
                .post(param)//get请求用.get(param)
                .url("ucenter/v1.0/c/user/information")
                .build();

        tools.execut(request, new BaseSubscriber<ResponseBody>(this) {
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    Log.e("------------","-----next-----=="+new String(responseBody.bytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("------------","----------=="+e.getMessage());
            }
        });
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
        viewModel.download("https://www.baidu.com/img/bd_logo1.png", localPath);
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
