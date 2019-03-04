package tk.hongbo.hbc_network;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import tk.hongbo.hbc_network.entity.BodyEntity;
import tk.hongbo.hbc_network.entity.BodyInfoEntity;
import tk.hongbo.hbc_network.entity.LastOfferLimitVo;
import tk.hongbo.hbc_network.request.IRequestAccess;
import tk.hongbo.network.Net;
import tk.hongbo.network.NetHelper;
import tk.hongbo.network.data.NetRoot;
import tk.hongbo.network.data.NetRaw;
import tk.hongbo.network.helper.DownHelper;
import tk.hongbo.network.helper.UploadHelper;
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
//        getNetData();
//        test1();
//        test2();
//        test3();
        testBody();
        return liveData;
    }

    private void getNetData() {
        NetHelper.getIns().request(service.reportApp("114003750443", 1), new NetListener<String>() {
            @Override
            public void onSuccess(String o, NetRaw netRaw) {
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
            public void onSuccess(LastOfferLimitVo baseEntiry, NetRaw netRaw) {
                Log.d("test", baseEntiry.getLastOfferLimitTip());
            }

            @Override
            public void onFailure(int status, String message, Throwable t, NetRaw netRaw) {
                super.onFailure(status, message, t, netRaw);
                Log.d("test", "错误信息：" + message + ",错误状态码：" + status);
            }
        });
    }

    private void test3() {
        Call<ResponseBody> call = service.getOrderListAll1(0, 20, 0);
        NetHelper.getIns().request(call, new NetListener() {
            @Override
            public void onSuccess(Object o, NetRaw netRaw) {
                Log.d("test", String.valueOf(o));
            }
        });
    }

    /**
     * 测试body体请求
     */
    private void testBody() {
//        String msg = "{\"data\":{\"captchaValid\":1,\"role\":\"USER\",\"userExist\":1,\"valid\":1,\"deviceId\":\"201810161120144d76d27d086ff9cb813d6c61151037300184ac8e3aef51cc\",\"eventName\":\"login\",\"ip\":\"192.168.131.193\",\"location\":{\"gpsLatitude\":39.8999,\"gpsLongitude\":116.468748},\"timestamp\":\"1543638885127\",\"tokenId\":\"120fba4428bc8167cc2552043612c978\"},\"eventId\":\"login\"}";
        Call<ResponseBody> call = service.getBody(getTestData());
        NetHelper.getIns().request(call, new NetListener() {
            @Override
            public void onSuccess(Object o, NetRaw netRaw) {
                Log.d("test", String.valueOf(o));
            }
        });
    }

    private BodyEntity getTestData() {
        BodyEntity be = new BodyEntity();
        be.setEventId("login");
        BodyInfoEntity bie = new BodyInfoEntity();
        bie.setCaptchaValid(1);
        bie.setRole("USER");
        bie.setUserExist(1);
        bie.setValid(1);
        bie.setDeviceId("201810161120144d76d27d086ff9cb813d6c61151037300184ac8e3aef51cc");
        bie.setEventName("login");
        bie.setIp("192.168.131.193");
        bie.setTimestamp(String.valueOf(System.currentTimeMillis()));
        bie.setTokenId("1e79f23d627507dfab8c6121592b0e82");
        be.setData(bie);
        return be;
    }

    public void upload(String filePath) {
        UploadHelper.get().upload(new File(filePath), new UploadHelper.UploadListener() {
            @Override
            public void onSuccess(String urlPath, String cdnHost) {
                Log.d("test", "success: The path is " + urlPath + ",cdnHot:" + cdnHost);
            }

            @Override
            public void onFailure(String errorMsg, Throwable throwable) {
                Log.d("test", "failure: The error is " + throwable.getMessage());
            }
        });
    }

    public void download(String url, String localPath) {
        DownHelper.get().download(url, localPath, new DownHelper.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.d("test", "success");
            }

            @Override
            public void onDownloading(int progress) {
                Log.d("test", "Progress: " + progress);
            }

            @Override
            public void onDownloadFailed() {
                Log.d("test", "failure");
            }
        });
    }
}
