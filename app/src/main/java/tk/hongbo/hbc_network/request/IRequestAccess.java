package tk.hongbo.hbc_network.request;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import tk.hongbo.hbc_network.entity.LastOfferLimitEntity;

public interface IRequestAccess {

    @GET("price/v1.0/c/querySingleLastOfferLimit")
    Call<LastOfferLimitEntity> getSingleLastLimit(@Query("cityId") int cityId);

    @FormUrlEncoded
    @POST("communication/v1.0/c/app/switch")
    Call<String> reportApp(@Field("userId") String userId, @Field("switchStatus") int switchStatus);
}
