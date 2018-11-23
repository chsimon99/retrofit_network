package tk.hongbo.network.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tk.hongbo.network.utils.Log;

public class DownHelper {

    private volatile static DownHelper downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownHelper get() {
        if (downloadUtil == null) {
            synchronized (DownHelper.class) {
                if (downloadUtil == null) {
                    downloadUtil = new DownHelper();
                }
            }
        }
        return downloadUtil;
    }

    private DownHelper() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url      下载连接
     * @param localUrl 储存下载文件的路径,带名称
     * @param listener 下载监听
     */
    public void download(final String url, final String localUrl, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                Log.e("下载失败", e);
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                if (!isExistDir(localUrl)) {
                    Log.e("下载目录不存在", null);
                    listener.onDownloadFailed();
                    return;
                }
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(localUrl);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    Log.d("下载成功");
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    Log.e("下载写入文件异常", e);
                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     */
    private boolean isExistDir(String saveDir) {
        File downloadFile = new File(saveDir);
        File fileDir = new File(downloadFile.getAbsoluteFile().getParent());
        if (!fileDir.exists()) {
            return false;
        }
        return true;
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}
