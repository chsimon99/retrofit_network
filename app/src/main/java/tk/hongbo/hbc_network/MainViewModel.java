package tk.hongbo.hbc_network;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import tk.hongbo.hbc_network.entity.LastOfferLimitVo;
import tk.hongbo.hbc_network.request.IRequestAccess;
import tk.hongbo.network.Net;
import tk.hongbo.network.NetHelper;
import tk.hongbo.network.data.NetRoot;
import tk.hongbo.network.net.NetListener;

public class MainViewModel extends AndroidViewModel {

    MutableLiveData<LastOfferLimitVo> liveData;

    IRequestAccess service;

    public MainViewModel(@NonNull Application application) {
        super(application);
        service = Net.getIns().getRetrofit().create(IRequestAccess.class);
    }

    public MutableLiveData<LastOfferLimitVo> getOfferLimit() {
        liveData = new MutableLiveData<>();
        getNetData();
//        test1();
//        test2();
        return liveData;
    }

    private void getNetData() {
        NetHelper.getIns().request(service.reportApp("114003750443", 1), new NetListener<String>() {
            @Override
            public void onSuccess(String o) {
                Log.d("test", o);
            }
        });
    }

    private void test1() {
        LiveData<NetRoot<LastOfferLimitVo>> liveData = service.changePwdLivdata("a123456", "z123456");
        liveData.observeForever(netRoot -> {
            Log.d("test", netRoot.getMessage());
        });
    }

    private void test2() {
        Call<NetRoot<LastOfferLimitVo>> call = service.changePwd("a123456", "z123456");
        NetHelper.getIns().request(call, new NetListener<LastOfferLimitVo>() {
            @Override
            public void onSuccess(LastOfferLimitVo baseEntiry) {
                Log.d("test", baseEntiry.getLastOfferLimitTip());
            }

            @Override
            public void onFailure(int status, String message, Throwable t) {
                super.onFailure(status, message, t);
                Log.d("test", "错误信息：" + message + ",错误状态码：" + status);
            }
        });
    }
}
