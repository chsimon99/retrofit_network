package tk.hongbo.network.bussiness;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IRequestSend {

    @POST("passport/v1.0/report")
    Call<String> sendReporrt(@Body RequestBody jsonArray);
}
