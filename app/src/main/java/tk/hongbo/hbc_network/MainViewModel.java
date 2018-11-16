package tk.hongbo.hbc_network;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import retrofit2.Call;
import tk.hongbo.hbc_network.entity.LastOfferLimitEntity;
import tk.hongbo.hbc_network.request.IRequestAccess;
import tk.hongbo.network.Net;
import tk.hongbo.network.NetHelper;
import tk.hongbo.network.data.BaseEntiry;
import tk.hongbo.network.net.NetListener;

public class MainViewModel extends AndroidViewModel {

    MutableLiveData<LastOfferLimitEntity> liveData;

    IRequestAccess service;

    public MainViewModel(@NonNull Application application) {
        super(application);
        service = Net.getIns().getRetrofit().create(IRequestAccess.class);
    }

    public MutableLiveData<LastOfferLimitEntity> getOfferLimit() {
        liveData = new MutableLiveData<>();
//        getNetData();
        test2();
        return liveData;
    }

    private void getNetData() {
        NetHelper.getIns().request(service.getSingleLastLimit(201), new NetListener<LastOfferLimitEntity>() {
            @Override
            public void onSuccess(LastOfferLimitEntity lastOfferLimitEntity) {
                liveData.postValue(lastOfferLimitEntity);
            }
        });
    }

    private void test2() {
        Call<BaseEntiry> call = service.changePwd("z1234567", "z123456");
        NetHelper.getIns().request(call, new NetListener<BaseEntiry>() {
            @Override
            public void onSuccess(BaseEntiry baseEntiry) {
                Log.d("test", baseEntiry.getMessage());
            }

            @Override
            public void onFailure(BaseEntiry baseEntiry, String error) {
                super.onFailure(baseEntiry, error);
                Log.d("test", "错误信息：" + error + ",错误状态码：" + baseEntiry.getStatus());
            }
        });
    }
}
