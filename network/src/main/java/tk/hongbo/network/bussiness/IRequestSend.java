package tk.hongbo.network.bussiness;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import tk.hongbo.network.data.NetRoot;
import tk.hongbo.network.data.OssTokenBean;

public interface IRequestSend {

    @POST("passport/v1.0/report")
    Call<String> sendReporrt(@Body RequestBody jsonArray);

    @GET("passport/v1.0/ossToken")
    Call<NetRoot<OssTokenBean>> getOssToken();
}
