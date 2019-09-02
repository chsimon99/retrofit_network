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
package tk.hongbo.network.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tk.hongbo.network.utils.Utils;


/**
 * ResponseCallback
 * Created by imxu on 2019-04-01.
 * @param <T>  result T
 * @param <E>  E response
 */
public abstract class ResponseCallback<T, E>  implements Callback, ITransform<E> {

    protected Object tag;
    protected Handler handler;
    protected String TAG ="retrofitCallback";
    private Context context;

    public Handler getHandler() {
        return handler == null ? handler = new Handler(Looper.getMainLooper()) : null;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ResponseCallback(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public ResponseCallback() {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
    }

    /**
     * UI Thread
     */
    public void onStart(Object tag) {
    }

    /**
     * UI Thread
     * @param
     */
    public void onCompleted(Object tag) {
    }

    /**
     * UI Thread
     * @param progress
     */
    public void onProgress(Object tag, float progress, long transfered, long total) {
    }

    /**
     * UI Thread
     * @param tag
     * @param progress
     * @param speed
     * @param transfered
     * @param total
     */
    public void onProgress(Object tag, int progress, long speed, long transfered, long total) {
    }

    /**
     * 子类可以复写 默认不做后端数据校验 子类指定规则
     * if you parse reponse code in onHandleResponse, you should make this method return true.
     * @param responseBody
     * @return
     */
    public boolean isReponseOk(Object tag, ResponseBody responseBody) {
        return true;
    }

    /**
     * Thread Pool Thread
     * 对response进行转换成 T
     * @param response
     */
    public abstract T onHandleResponse(ResponseBody response) throws Exception;

    @Override
    public <T> T transform(E response, Class<T> classOfT)  throws Exception {
        return (T) response;
    }

    /**
     * Thread Pool Thread
     * @param tag
     * @param e
     */
    public abstract void onError(Object tag, Exception e);

    public abstract void onCancel(Object tag, Exception e);
    /**
     * Thread Pool Thread
     * @param response
     */
    public abstract void onNext(Object tag, Call call, T response);

    @Override
    public void  onFailure(final Call call, final IOException e) {
        if (Utils.checkMain()) {
            onError(tag, e);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onError(call.request().tag(), e);
                }
            });
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (call.isCanceled()) {
            onCancel(call.request().tag(), new Exception("已取消"));
        }
        tag = call.request().tag();
        if(isReponseOk(tag, response.body())) {
            try {
                onHandleResponse(response.body());
            } catch (Exception e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, e);
                    }
                });
            }
        }
    }

    /**
     * OnRelease 子类可以复写
     */
    public void onRelease() {

    }

    public static ResponseCallback CALLBACK_DEFAULT = new ResponseCallback() {
        @Override
        public void onFailure(Call call, IOException e) {
            onError(call.request().tag(), e);

        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                onHandleResponse(response.body());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public Object transform(Object response, Class classOfT) {
            return response;
        }
        @Override
        public Object onHandleResponse(ResponseBody response) throws Exception {
            return  response;
        }
        @Override
        public void onError(Object tag, Exception e) {
            onRelease();
        }
        @Override
        public void onCancel(Object tag, Exception e) {

        }
        @Override
        public void onNext(Object tag, Call call, Object response) {

        }
        @Override
        public void onCompleted(Object tag) {
            super.onCompleted(tag);
            onRelease();
        }
    };


}