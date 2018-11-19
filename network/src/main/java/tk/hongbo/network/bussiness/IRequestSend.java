package tk.hongbo.network.bussiness;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tk.hongbo.network.data.NetRoot;

public interface IRequestSend {

    @POST("passport/v1.0/report")
    Call<NetRoot> sendReporrt(@Body RequestBody jsonArray);
}
