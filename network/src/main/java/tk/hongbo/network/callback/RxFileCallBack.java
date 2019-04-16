/*
 *    Copyright (C) 2017 Tamic
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
package tk.hongbo.network.callback;


import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import tk.hongbo.network.utils.Utils;

/**
 * RxFileCallBack File回调
 * Created by imxu on 2019-04-04.
 */
public abstract class RxFileCallBack extends ResponseCallback<File, ResponseBody> {
    /*** 文件夹路径*/
    private String destFileDir;
    /*** 文件名*/
    private String destFileName = Utils.DEFAULT_FILENAME;
    /*** FileOutputStream*/
    FileOutputStream fos = null;
    /*** FileOutputStream*/
    InputStream is = null;
    private long sum = 0;
    private int updateCount = 0;
    private int interval = 1;
    private int  progress = 0;

    public RxFileCallBack(String destFileName) {
        this("", destFileName);
    }

    public RxFileCallBack(String fileDir, String fileName) {
        super();
        this.destFileDir = fileDir;
        this.destFileName = fileName;
    }

    @Override
    public File onHandleResponse(ResponseBody response) throws Exception {
       return transform(response);
    }

    public File transform(ResponseBody response) throws Exception {
        return onNextFile(response);
    }

    public File onNextFile(ResponseBody response) throws Exception {
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.byteStream();
            final long total = response.contentLength();
            if (total < 2048) {
                interval = (int) 0.2;
            } else {
                interval = 1;
            }
            File file = Utils.createDownloadFile(destFileDir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                if (total == -1 || total ==  0) {
                    progress = 100;
                } else {
                    progress = (int) (finalSum * 100 / total);
                }
                if (updateCount == 0 || progress >= updateCount) {
                    updateCount += interval;
                    handler = new Handler(Looper.getMainLooper());
                    final int finalProgress = progress;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(tag, finalProgress, finalSum, total);
                        }
                    });
                }
            }
            fos.flush();
            return file;

        } finally {
           onRelease();
        }
    }

    @Override
    public void onNext(final Object tag, okhttp3.Call call, final File response) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onNext(tag, response);
            }
        });
    }

    @Override
    public void onRelease() {
        super.onRelease();
        if (is != null) try {
            is.close();
            if (fos != null) fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            onError(tag, e);
        }
    }

    public abstract void onNext(Object tag, File file);

    public abstract void onProgress(Object tag, float progress, long downloaded, long total);

}
