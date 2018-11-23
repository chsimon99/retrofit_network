package tk.hongbo.network.helper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import tk.hongbo.network.data.OssTokenBean;
import tk.hongbo.network.data.OssTokenKeyBean;
import tk.hongbo.network.utils.DeviceInfo;

public class UploadHelper {

    private Executor executor = Executors.newFixedThreadPool(3);

    private volatile static UploadHelper helper;
    private final OkHttpClient okHttpClient;

    public static UploadHelper get() {
        if (helper == null) {
            synchronized (UploadHelper.class) {
                if (helper == null) {
                    helper = new UploadHelper();
                }
            }
        }
        return helper;
    }

    private UploadHelper() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * 上传图片
     */
    public void upload(File file, UploadListener uploadListener) {
        if (file.exists()) {
            if (DeviceInfo.isNetworkAvailable()) {
                uploadPicture(file, uploadListener);
            } else {
                if (uploadListener != null) {
                    uploadListener.onFailure("无网络", new Exception("Upload network error"));
                }
            }
        } else {
            if (uploadListener != null) {
                uploadListener.onFailure("文件不存在", new Exception("File not exist"));
            }
        }
    }

    private void uploadPicture(final File file, UploadListener listener) {
        TakeNumHelper.getInstance().getKey(new TakeNumHelper.KeyBackListener() {
            @Override
            public void onKeyBack(OssTokenBean ossTokenBean, OssTokenKeyBean ossTokenKeyBean) {
                uploadPicTask(ossTokenBean, ossTokenKeyBean, file, listener);
            }

            @Override
            public void onFail(String message) {
                if (listener != null) {
                    listener.onFailure(message, new Exception("Get take number error"));
                }
            }
        });
    }

    private void uploadPicTask(final OssTokenBean ossTokenBean, final OssTokenKeyBean ossTokenKeyBean, final File file,
                               UploadListener listener) {
        executor.execute(() -> {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("OSSAccessKeyId", ossTokenBean.getOssTokenParamBean().getOssAccessKeyId())
                    .addFormDataPart("policy", ossTokenBean.getOssTokenParamBean().getPolicy())
                    .addFormDataPart("Signature", ossTokenBean.getOssTokenParamBean().getSignature())
                    .addFormDataPart("key", ossTokenKeyBean.getKey())
                    .addFormDataPart("file", file.getName(), fileBody)
                    .build();
            Request request = new Request.Builder().url(ossTokenBean.getAddress()).post(requestBody).build();
            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    if (listener != null) {
                        listener.onFailure(e.getMessage(), e);
                    }
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                    if (listener != null) {
                        listener.onSuccess(ossTokenKeyBean.getPath());
                    }
                }
            });
        });
    }

    public interface UploadListener {
        void onSuccess(String urlPath);

        void onFailure(String errorMsg, Throwable throwable);
    }

}
