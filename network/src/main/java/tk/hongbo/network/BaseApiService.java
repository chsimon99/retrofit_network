/*
 *    Copyright (C) 2016 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package tk.hongbo.network;


import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * ApiService
 */
public interface BaseApiService {
    @POST()
    @FormUrlEncoded
    <T> Observable<Response<ResponseBody>> executePost(@Url() String url, @FieldMap Map<String, Object> maps);

    @POST()
    @FormUrlEncoded
    <T> Observable<ResponseBody> post(@Url() String url, @FieldMap Map<String, Object> maps);

    @GET()
    <T> Observable<Response<ResponseBody>> executeGet(@Url String url,@QueryMap Map<String, Object> maps);

    @DELETE()
    <T> Observable<Response<ResponseBody>> executeDelete( @Url String url, @QueryMap Map<String, Object> maps);

    @PUT()
    <T> Observable<Response<ResponseBody>> executePut(@Url String url,@FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> executePostBody(@Url String url, @Body Object object);

    @Multipart
    @POST()
    Observable<ResponseBody> upLoadImage(@Url() String url,@Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlie(@Url String fileUrl,@Part("description") RequestBody description,
            @Part("image\"; filename=\"image.jpg") MultipartBody.Part file);

    @POST()
    Observable<ResponseBody> uploadFiles(@Url() String url, @Body Map<String, RequestBody> maps);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlieWithPart(@Url String fileUrl,@Part() MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlieWithPartList(@Url String fileUrl,@Part List<MultipartBody.Part> list);

    @Multipart
    @POST()
    Observable<ResponseBody> uploadFlieWithPartMap(@Url String fileUrl,@PartMap Map<String, MultipartBody.Part> maps);

    @POST()
    Observable<ResponseBody> uploadFile(@Url() String url,@Body RequestBody file);

    @Multipart
    @POST
    Observable<ResponseBody> uploadFileWithPartMap(@Url() String url,@PartMap() Map<String, RequestBody> partMap,@Part() MultipartBody.Part file);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @GET
    Observable<ResponseBody> downloadSmallFile(@Url String fileUrl);

    @GET
    <T> Observable<ResponseBody> getTest(@Url String fileUrl,@QueryMap Map<String, Object> maps);

    @FormUrlEncoded
    @POST()
    <T> Observable<ResponseBody> postForm(@Url() String url,@FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postRequestBody(@Url() String url,@Body RequestBody Body);
}


