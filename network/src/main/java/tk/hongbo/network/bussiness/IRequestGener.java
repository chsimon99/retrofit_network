package tk.hongbo.network.bussiness;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface IRequestGener {

    @GET
    Call<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> map);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> map);

    @POST
    Call<ResponseBody> body(@Url String url, @Body String requestBody);
}
