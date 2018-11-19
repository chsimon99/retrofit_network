package tk.hongbo.hbc_network.request;

import android.arch.lifecycle.LiveData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tk.hongbo.hbc_network.entity.LastOfferLimitVo;
import tk.hongbo.network.data.NetRoot;

public interface IRequestAccess {

    @GET("price/v1.0/c/querySingleLastOfferLimit")
    Call<NetRoot<LastOfferLimitVo>> getSingleLastLimit(@Query("cityId") int cityId);

    @FormUrlEncoded
    @POST("communication/v1.0/c/app/switch")
    Call<ResponseBody> reportApp(@Field("userId") String userId, @Field("switchStatus") int switchStatus);

    @FormUrlEncoded
    @POST("ucenter/v1.0/c/user/password/update")
    LiveData<NetRoot<LastOfferLimitVo>> changePwdLivdata(@Field("originPassword") String originPassword, @Field("password") String password);

    @FormUrlEncoded
    @POST("ucenter/v1.0/c/user/password/update")
    Call<NetRoot<LastOfferLimitVo>> changePwd(@Field("originPassword") String originPassword, @Field("password") String password);
}
